package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dto.brands.Brand;
import cz.pps.auto_dl_be.dto.detail.Article;
import cz.pps.auto_dl_be.dto.medusa.*;
import cz.pps.auto_dl_be.exception.CsvConversionException;
import cz.pps.auto_dl_be.exception.CsvDownloadException;
import cz.pps.auto_dl_be.exception.NoDataException;
import cz.pps.auto_dl_be.exception.SavingCsvException;
import cz.pps.auto_dl_be.model.Item;
import cz.pps.auto_dl_be.model.ProductEntity;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MedusaService {

    private static final Logger logger = LoggerFactory.getLogger(MedusaService.class);
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
    private final MeterRegistry meterRegistry;
    @Value("${darma.url}")
    private String apiUrl;

    private Counter csvDownloadCounter;
    private Counter csvConversionCounter;
    private Counter productSavedCounter;
    private Counter productUpdatedCounter;
    private Counter errorCounter;
    private Timer downloadTimer;
    private Timer conversionTimer;
    private Counter darmaScrapingStartedCounter;
    private Counter darmaScrapingCompletedCounter;
    private Counter darmaScrapingFailedCounter;
    private Timer darmaScrapingTimer;

    @PostConstruct
    private void initMetrics() {
        csvDownloadCounter = Counter.builder("medusa.csv.download.count")
                .description("Count of downloaded CSV files")
                .register(meterRegistry);

        csvConversionCounter = Counter.builder("medusa.csv.conversion.count")
                .description("Count of converted CSV files")
                .register(meterRegistry);

        productSavedCounter = Counter.builder("medusa.product.saved.count")
                .description("Count of saved products")
                .register(meterRegistry);

        productUpdatedCounter = Counter.builder("medusa.product.updated.count")
                .description("Count of updated products")
                .register(meterRegistry);

        errorCounter = Counter.builder("medusa.error.count")
                .description("Count of errors")
                .register(meterRegistry);

        downloadTimer = Timer.builder("medusa.csv.download.time")
                .description("Time taken to download CSV file")
                .register(meterRegistry);

        conversionTimer = Timer.builder("medusa.csv.conversion.time")
                .description("Time taken to convert CSV file")
                .register(meterRegistry);
                
        darmaScrapingStartedCounter = Counter.builder("darma.scraping.started.count")
                .description("Count of Darma scraping operations started")
                .register(meterRegistry);
                
        darmaScrapingCompletedCounter = Counter.builder("darma.scraping.completed.count")
                .description("Count of Darma scraping operations successfully completed")
                .register(meterRegistry);
                
        darmaScrapingFailedCounter = Counter.builder("darma.scraping.failed.count")
                .description("Count of Darma scraping operations that failed")
                .register(meterRegistry);
                
        darmaScrapingTimer = Timer.builder("darma.scraping.time")
                .description("Time taken to complete Darma scraping operations")
                .register(meterRegistry);
                
        meterRegistry.gauge("medusa.product.count",
                productService,
                ProductService::getProductCount);
                
        // Add a gauge for Darma scraping status (0=idle, 1=in-progress)
        meterRegistry.gauge("darma.scraping.status", 0);
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
        item.setTecDocId(values.size() <= 10 ? null : values.get(10));
        item.setTecDocSupplierName(values.size() <= 11 ? null : values.get(11));
        return item;
    }

    @Async
    public void downloadAndSaveCsvAsItems() throws CsvDownloadException, NoDataException, SavingCsvException, CsvConversionException {
        Timer.Sample scrapeSample = Timer.start(meterRegistry);
        darmaScrapingStartedCounter.increment();
        meterRegistry.gauge("darma.scraping.status", 1); // Set status to in-progress
        
        logger.info("Starting Darma data scraping process");
        try {
            logger.info("Downloading CSV data from Darma");
            File csvFile = getCsvFile(apiUrl);
            List<Brand> brands = fetchBrands();
            logger.info("Fetched brands from TecDoc API");
            convertToItemsAndSave(csvFile, brands);
            deleteCsvFile(csvFile);
            
            darmaScrapingCompletedCounter.increment();
            logger.info("Darma data scraping process completed successfully");
        } catch (Exception e) {
            darmaScrapingFailedCounter.increment();
            logger.error("Darma data scraping process failed: {}", e.getMessage(), e);
            throw e;
        } finally {
            scrapeSample.stop(darmaScrapingTimer);
            meterRegistry.gauge("darma.scraping.status", 0); // Reset status to idle
        }
    }

    @Async
    @Transactional
    public void updateDataInDatabase() {
        Timer.Sample scrapeSample = Timer.start(meterRegistry);
        darmaScrapingStartedCounter.increment();
        meterRegistry.gauge("darma.scraping.status", 1); // Set status to in-progress
        
        logger.info("Starting product database update process");
        try {
            Stream<ProductEntity> dataStream = productService.getAllProductsAsStream();
            dataStream.forEach(product -> {
                try {
                    List<Article> articles = tecDocService.fetchArticles(product.getTecDocId(), product.getSupplierId()).block();
                    if (articles != null && !articles.isEmpty()) {
                        updateArticlesToDatabase(articles.getFirst(), product);
                        logger.info("Updated product: {}", product.getTecDocId());
                    }
                } catch (Exception e) {
                    errorCounter.increment();
                    logger.error("Error updating product {}: {}", product.getTecDocId(), e.getMessage(), e);
                }
            });
            
            darmaScrapingCompletedCounter.increment();
            logger.info("Product database update process completed successfully");
        } catch (Exception e) {
            darmaScrapingFailedCounter.increment();
            logger.error("Product database update process failed: {}", e.getMessage(), e);
            throw e;
        } finally {
            scrapeSample.stop(darmaScrapingTimer);
            meterRegistry.gauge("darma.scraping.status", 0); // Reset status to idle
        }
    }

    private File getCsvFile(String apiUrl) throws CsvDownloadException, NoDataException, SavingCsvException {
        WebClient webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024))
                .build();
        String csvData = downloadCsvData(webClient, apiUrl);
        logger.info("CSV data has been successfully downloaded from Darma");
        return saveCsvDataToFile(csvData);
    }

    private String downloadCsvData(WebClient webClient, String apiUrl) throws CsvDownloadException {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            String result = webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            csvDownloadCounter.increment();
            return result;
        } catch (Exception e) {
            errorCounter.increment();
            logger.error("Error occurred while downloading CSV data: {}", e.getMessage(), e);
            throw new CsvDownloadException("Failed to download CSV data from API", e);
        } finally {
            sample.stop(downloadTimer);
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
            logger.info("CSV data has been successfully saved to file");
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
        Timer.Sample sample = Timer.start(meterRegistry);
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            Map<String, Integer> brandMap = createBrandMap(brands);
            br.lines()
                    .skip(1)
                    .map(this::getItem)
                    .filter(item -> isValidItem(item, brandMap))
                    .peek(item -> pairTecDocSupplierNameToDataSupplierId(item, brandMap))
                    .forEach(this::processItem);

            csvConversionCounter.increment();
        } catch (IOException e) {
            errorCounter.increment();
            throw new CsvConversionException("Failed to convert CSV data to items", e);
        } finally {
            sample.stop(conversionTimer);
        }
        logger.info("CSV data has been successfully converted to items and saved to the database");
    }

    private Map<String, Integer> createBrandMap(List<Brand> brands) {
        return brands.stream()
                .collect(Collectors.toMap(Brand::getMfrName, Brand::getDataSupplierId));
    }

    private boolean isValidItem(Item item, Map<String, Integer> brandMap) {
        return item.getManufacturer() != null &&
                brandMap.containsKey(item.getTecDocSupplierName()) &&
                item.getTecDocSupplierName() != null &&
                item.getTecDocId() != null &&
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
        String id = item.getTecDocId().replaceAll("[^a-zA-Z0-9-_]", "");

        Product existingProduct = productService.findById(id);
        if (existingProduct == null) {
            logger.info("Saving new product: {}", id);
            List<Article> articles = tecDocService.fetchArticles(item.getTecDocId(), item.getTecDocSupplierID()).block();
            if (articles != null && !articles.isEmpty()) {
                saveArticlesToDatabase(articles.getFirst(), item);
                productSavedCounter.increment();
            }
        } else {
            logger.info("Updating existing product: {}", id);
            updateDarmaDataInDatabase(item);
            productUpdatedCounter.increment();
        }
    }

    public void saveArticlesToDatabase(Article article, Item item) {
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
            errorCounter.increment();
            logger.info("Error occurred while saving product: {}, error: {}", item.getTecDocId(), e.getMessage());
            throw e; // Rethrow to let the controller handle it
        }
    }

    private void updateArticlesToDatabase(Article article, ProductEntity productEntity) {
        try {
            InventoryItem inventoryItem = new InventoryItem(article);
            Price price = new Price(article);
            Product product = new Product(article, productEntity.getSupplierId());
            ProductVariant productVariant = new ProductVariant(article);

            productService.updateProduct(product);
            inventoryItemService.updateInventoryItem(inventoryItem);
            priceService.updatePrice(price);
            productVariantService.updateProductVariant(productVariant);
        } catch (Exception e) {
            errorCounter.increment();
            logger.info("Error occurred while updating product: {}, error: {}", productEntity.getTecDocId(), e.getMessage());
        }
    }

    private void updateDarmaDataInDatabase(Item item) {
        InventoryLevel inventoryLevel = new InventoryLevel(item.getMainStock(), item.getSupplierStock(), item.getOtherBranchStock(), item.getTecDocId());
        Price price = new Price(item.getPrice(), item.getTecDocId());

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
}