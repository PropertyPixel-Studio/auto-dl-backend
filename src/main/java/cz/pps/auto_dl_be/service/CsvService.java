package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dto.brands.Brand;
import cz.pps.auto_dl_be.dto.detail.Article;
import cz.pps.auto_dl_be.exception.CsvConversionException;
import cz.pps.auto_dl_be.exception.CsvDownloadException;
import cz.pps.auto_dl_be.exception.NoDataException;
import cz.pps.auto_dl_be.exception.SavingCsvException;
import cz.pps.auto_dl_be.model.Item;
import cz.pps.auto_dl_be.model.medusa.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CsvService {

    private static final Logger logger = LoggerFactory.getLogger(CsvService.class);
    private final TecDocService tecDocService;
    private final InventoryItemService inventoryItemService;
    private final InventoryLevelService inventoryLevelService;
    private final PriceService priceService;
    private final PriceSetService priceSetService;
    private final ProductSalesChannelService productSalesChannelService;
    private final ProductService productService;
    private final ProductVariantInventoryItemService productVariantInventoryItemService;
    private final ProductVariantService productVariantService;
    private final ProductVariantPriceSetService productVariantPriceSetService;

    @Value("${darma.url}")
    private String apiUrl;

    private final String[] testBrand = {"Herth+Buss Elparts", "METZGER", "FEBI BILSTEIN", "JP GROUP", "vika", "FAST", "OREX", "TOTAL"};

    @Async
    public void downloadAndSaveCsvAsItems() throws CsvDownloadException, NoDataException, SavingCsvException, CsvConversionException {
        File csvFile = getCsvFile(apiUrl);
        List<Brand> brands = fetchBrands();
        convertToItemsAndSave(csvFile, brands);
        deleteCsvFile(csvFile);
    }

    private File getCsvFile(String apiUrl) throws CsvDownloadException, NoDataException, SavingCsvException {
        WebClient webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024))
                .build();
        String csvData = downloadCsvData(webClient, apiUrl);
        return saveCsvDataToFile(csvData);
    }

    private String downloadCsvData(WebClient webClient, String apiUrl) throws CsvDownloadException {
        try {
            return webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            logger.error("Error occurred while downloading CSV data: {}", e.getMessage(), e);
            throw new CsvDownloadException("Failed to download CSV data from API", e);
        }
    }

    private File saveCsvDataToFile(String csvData) throws NoDataException, SavingCsvException {
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

    private List<Brand> fetchBrands() throws NoDataException {
        List<Brand> brands = tecDocService.fetchBrands().block();
        if (brands == null || brands.isEmpty()) {
            throw new NoDataException("No brands received from TecDoc API");
        }
        return brands;
    }

    private void convertToItemsAndSave(File csvFile, List<Brand> brands) throws CsvConversionException {
        Map<String, Integer> brandMap = createBrandMap(brands);

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.lines()
                    .skip(1)
                    .map(this::getItem)
                    .filter(this::isValidItem)
                    .peek(item -> pairTecDocSupplierNameToDataSupplierId(item, brandMap))
                    .forEach(this::processItem);
        } catch (IOException e) {
            throw new CsvConversionException("Failed to convert CSV data to items", e);
        }
        logger.info("CSV data has been successfully converted to items and saved to the database");
    }

    private Map<String, Integer> createBrandMap(List<Brand> brands) {
        return brands.stream()
                .collect(Collectors.toMap(Brand::getMfrName, Brand::getDataSupplierId));
    }

    private boolean isValidItem(Item item) {
        return item.getManufacturer() != null &&
                Arrays.stream(testBrand).anyMatch(item.getManufacturer()::contains) &&
                item.getTecDocSupplierName() != null &&
                item.getTecDocld() != null &&
                (!Objects.equals(item.getMainStock(), "0") ||
                        !Objects.equals(item.getSupplierStock(), "0") ||
                        !Objects.equals(item.getOtherBranchStock(), "0"));
    }

    private void pairTecDocSupplierNameToDataSupplierId(Item item, Map<String, Integer> brandMap) {
        Integer dataSupplierId = brandMap.get(item.getTecDocSupplierName());
        if (dataSupplierId != null) {
            item.setTecDocSupplierID(String.valueOf(dataSupplierId));
        }
    }

    private void processItem(Item item) {
        String id = item.getTecDocld().replaceAll("[^a-zA-Z0-9-_]", "");

        Product existingProduct = productService.findById(id);
        if (existingProduct == null) {
            logger.info("Saving new product: {}", id);
            List<Article> articles = tecDocService.fetchArticles(item.getTecDocld(), item.getTecDocSupplierID()).block();
            if (articles != null && !articles.isEmpty()) {
                saveArticlesToDatabase(articles.getFirst(), item);
            }
        } else {
            logger.info("Updating existing product: {}", id);
            updateDarmaDataInDatabase(item);
        }
    }

    private void saveArticlesToDatabase(Article article, Item item) {
        try {
            InventoryItem inventoryItem = new InventoryItem(article);
            InventoryLevel inventoryLevel = new InventoryLevel(article, item.getMainStock(), item.getSupplierStock(), item.getOtherBranchStock());
            Price price = new Price(article, item.getPrice());
            PriceSet priceSet = new PriceSet(article);
            ProductSalesChannel productSalesChannel = new ProductSalesChannel(article);
            Product product = new Product(article, item.getTecDocSupplierID());
            ProductVariantInventoryItem productVariantInventoryItem = new ProductVariantInventoryItem(article);
            ProductVariant productVariant = new ProductVariant(article);
            ProductVariantPriceSet productVariantPriceSet = new ProductVariantPriceSet(article);

            productService.saveWithQuery(product);
            inventoryItemService.saveWithQuery(inventoryItem);
            inventoryLevelService.saveWithQuery(inventoryLevel);
            priceSetService.saveWithQuery(priceSet);
            priceService.saveWithQuery(price);
            productSalesChannelService.saveWithQuery(productSalesChannel);
            productVariantInventoryItemService.saveWithQuery(productVariantInventoryItem);
            productVariantService.saveWithQuery(productVariant);
            productVariantPriceSetService.saveWithQuery(productVariantPriceSet);
        } catch (Exception e) {
            logger.info("Error occurred while saving product: {}", item.getTecDocld());
        }
    }

    private void updateDarmaDataInDatabase(Item item) {
        InventoryLevel inventoryLevel = new InventoryLevel(item.getMainStock(), item.getSupplierStock(), item.getOtherBranchStock());
        Price price = new Price(item.getPrice());

        inventoryLevelService.updateStockedQuantity(inventoryLevel);
        priceService.updateAmount(price);
    }

    private void deleteCsvFile(File csvFile) {
        if (csvFile.exists()) {
            csvFile.delete();
        }
    }

    private Item getItem(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ';' && !inQuotes) {
                values.add(currentField.isEmpty() ? null : currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }
        values.add(currentField.isEmpty() ? null : currentField.toString()); // add the last field

        return getItem(values);
    }

    private static Item getItem(List<String> values) {
        Item item = new Item();

        item.setProductCode(values.isEmpty() ? null : values.get(0));
        item.setManufacturer(values.size() <= 1 ? null : values.get(1));
        item.setProductName(values.size() <= 2 ? null : values.get(2));
        item.setMainStock(values.size() <= 3 ? null : values.get(3));
        item.setOtherBranchStock(values.size() <= 4 ? null : values.get(4));
        item.setSupplierStock(values.size() <= 5 ? null : values.get(5));
        item.setPrice(values.size() <= 6 ? null : values.get(6));
        item.setVatRate(values.size() <= 7 ? null : values.get(7));
        item.setCurrency(values.size() <= 8 ? null : values.get(8));
        item.setDeposit(values.size() <= 9 ? null : values.get(9));
        item.setTecDocld(values.size() <= 10 ? null : values.get(10));
        item.setTecDocSupplierName(values.size() <= 11 ? null : values.get(11));
        return item;
    }
}