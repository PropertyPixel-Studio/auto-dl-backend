package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dao.*;
import cz.pps.auto_dl_be.dto.brands.Brand;
import cz.pps.auto_dl_be.dto.detail.Article;
import cz.pps.auto_dl_be.exception.CsvConversionException;
import cz.pps.auto_dl_be.exception.CsvDownloadException;
import cz.pps.auto_dl_be.exception.NoDataException;
import cz.pps.auto_dl_be.exception.SavingCsvException;
import cz.pps.auto_dl_be.model.Item;
import cz.pps.auto_dl_be.model.medusa.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CsvService {

    private static final Logger logger = LoggerFactory.getLogger(CsvService.class);
//    private final ItemDao itemDao;
    private final InventoryItemDao inventoryItemDao;
    private final InventoryLevelDao inventoryLevelDao;
    private final PriceDao priceDao;
    private final PriceSetDao priceSetDao;
    private final ProductDao productDao;
    private final ProductSalesChannelDao productSalesChannelDao;
    private final ProductVariantDao productVariantDao;
    private final ProductVariantInventoryItemDao productVariantInventoryItemDao;
    private final TecDocService tecDocService;
    @Value("${darma.url}")
    private String apiUrl;
    private final String[] testBrand = {"Herth+Buss Elparts", "METZGER", "FEBI BILSTEIN", "JP GROUP", "vika", "FAST", "OREX", "TOTAL"};

    private Item getItem(String line) {
        String[] values = line.split(";");
        Item item = new Item();
        item.setProductCode(values.length == 0 ? null : values[0]);
        item.setManufacturer(values.length <= 1 ? null : values[1]);
        item.setProductName(values.length <= 2 ? null : values[2]);
        item.setMainStock(values.length <= 3 ? null : values[3]);
        item.setOtherBranchStock(values.length <= 4 ? null : values[4]);
        item.setSupplierStock(values.length <= 5 ? null : values[5]);
        item.setPrice(values.length <= 6 ? null : values[6]);
        item.setVatRate(values.length <= 7 ? null : values[7]);
        item.setCurrency(values.length <= 8 ? null : values[8]);
        item.setDeposit(values.length <= 9 ? null : values[9]);
        item.setTecDocld(values.length <= 10 ? null : values[10]);
        item.setTecDocSupplierName(values.length <= 11 ? null : values[11]);
        return item;
    }

    private static File getCsvFile(String apiUrl) throws CsvDownloadException, NoDataException, SavingCsvException {
        WebClient webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024)) // 10 MB buffer size
                .build();
        String csvData;
        logger.info("Downloading CSV data from API...");
        try {
            csvData = webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            logger.error("Error occurred while downloading CSV data: {}", e.getMessage(), e);
            throw new CsvDownloadException("Failed to download CSV data from API", e);
        }

        if (csvData == null) {
            logger.warn("No data received from API");
            throw new NoDataException("CSV data is null");
        }

        File csvFile = new File("data.csv");
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write(csvData);
        } catch (IOException e) {
            logger.error("Error occurred while saving CSV data to file: {}", e.getMessage(), e);
            throw new SavingCsvException("Failed to write CSV data to file", e);
        }
        return csvFile;
    }

    public void downloadAndSaveCsvAsItems() throws CsvDownloadException, NoDataException, SavingCsvException, CsvConversionException {
        // Step 1: Download the CSV file from the API
        File csvFile = getCsvFile(this.apiUrl);

        // Step 2: Get brands from TecDoc API
        List<Brand> brands = tecDocService.fetchBrands().block();
        if (brands == null || brands.isEmpty()) {
            throw new NoDataException("No brands received from TecDoc API");
        }

        // Step 3: Read the CSV file and save data as Item entities
        convertToItems(csvFile, brands);

        // Step 4: Delete the CSV file
        if (csvFile.exists()) {
            csvFile.delete();
        }
    }

    private void convertToItems(File csvFile, List<Brand> brands) throws CsvConversionException {
        // Create a map of TecDocSupplierName to dataSupplierId
        Map<String, Integer> brandMap = brands.stream()
                .collect(Collectors.toMap(Brand::getMfrName, Brand::getDataSupplierId));

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.lines()
                    .skip(1) // Skip the first line
                    .map(this::getItem)
                    .filter(item -> item.getManufacturer() != null &&
                            Arrays.stream(testBrand).anyMatch(item.getManufacturer()::contains) &&
                            item.getTecDocSupplierName() != null &&
                            item.getTecDocld() != null)
                    .peek(item -> {
                        // Pair TecDocSupplierName to dataSupplierId
                        Integer dataSupplierId = brandMap.get(item.getTecDocSupplierName());
                        if (dataSupplierId != null) {
                            item.setTecDocSupplierID(String.valueOf(dataSupplierId));
                        }
                    })
                    .limit(1)
                    .forEach(item -> {
//                        itemDao.save(item);
                        // Fetch articles from TecDocService
                        List<Article> articles = tecDocService.fetchArticles(item.getTecDocld(), item.getTecDocSupplierID()).block();
                        try {
                            if (articles != null && !articles.isEmpty()) {
                                // Get the first article
                                Article firstArticle = articles.getFirst();

                                // Create objects from the first article
                                InventoryItem inventoryItem = new InventoryItem(firstArticle);
                                InventoryLevel inventoryLevel = new InventoryLevel(firstArticle, item.getMainStock());
                                Price price = new Price(firstArticle, item.getPrice());
                                PriceSet priceSet = new PriceSet(firstArticle);
                                Product product = new Product(firstArticle, item.getTecDocSupplierID());
                                ProductSalesChannel productSalesChannel = new ProductSalesChannel(firstArticle);
                                ProductVariant productVariant = new ProductVariant(firstArticle);
                                ProductVariantInventoryItem productVariantInventoryItem = new ProductVariantInventoryItem(firstArticle);
                                // Save the Product object using ProductDao
                                inventoryItemDao.save(inventoryItem);
                                inventoryLevelDao.save(inventoryLevel);
                                priceDao.save(price);
                                priceSetDao.save(priceSet);
                                productDao.save(product);
                                productSalesChannelDao.save(productSalesChannel);
                                productVariantDao.save(productVariant);
                                productVariantInventoryItemDao.save(productVariantInventoryItem);
                            }
                        } catch (Exception e) {
                            logger.info("Error occurred while saving product: {}", item.getTecDocld());
                            System.out.println(item);
                        }
                    });
        } catch (IOException e) {
            throw new CsvConversionException("Failed to convert CSV data to items", e);
        }
        logger.info("CSV data has been successfully converted to items and saved to the database");
    }

    @PostConstruct
    @Async
    public void init() {
        try {
            downloadAndSaveCsvAsItems();
            logger.info("Application has finished loading and is ready.");
        } catch (CsvDownloadException | NoDataException | SavingCsvException | CsvConversionException e) {
            logger.error("Error occurred during application initialization: {}", e.getMessage(), e);
        }
//        List<Article> articles = tecDocService.fetchArticles("2140449", "94").block();
//        System.out.println(articles);
    }
}