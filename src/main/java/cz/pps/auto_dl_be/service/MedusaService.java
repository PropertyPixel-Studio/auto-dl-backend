package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dto.brands.Brand;
import cz.pps.auto_dl_be.dto.detail.Article;
import cz.pps.auto_dl_be.dto.medusa.*;
import cz.pps.auto_dl_be.exception.CsvConversionException;
import cz.pps.auto_dl_be.exception.CsvDownloadException;
import cz.pps.auto_dl_be.exception.NoDataException;
import cz.pps.auto_dl_be.exception.SavingCsvException;
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
import jakarta.transaction.Transactional;
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
import java.util.stream.Stream;

@Service
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
    private final Timer fetchParseTimer;
    private final Counter productsProcessedCounter;
    private final Counter productsCreatedCounter;
    private final Counter productsUpdatedCounter;
    private final Counter productsFailedCounter;
    private final Timer tecDocApiTimer;
    private final Counter tecDocApiFailureCounter;
    private final Counter skippedItemCounter;


    @Value("${darma.url}")
    private String apiUrl;

    public MedusaService(TecDocService tecDocService, InventoryItemService inventoryItemService, InventoryLevelService inventoryLevelService, PriceService priceService, PriceSetService priceSetService, ProductSalesChannelService productSalesChannelService, ProductService productService, ProductVariantInventoryItemService productVariantInventoryItemService, ProductVariantService productVariantService, ProductVariantPriceSetService productVariantPriceSetService, MeterRegistry meterRegistry) {
        this.tecDocService = tecDocService;
        this.inventoryItemService = inventoryItemService;
        this.inventoryLevelService = inventoryLevelService;
        this.priceService = priceService;
        this.priceSetService = priceSetService;
        this.productSalesChannelService = productSalesChannelService;
        this.productService = productService;
        this.productVariantInventoryItemService = productVariantInventoryItemService;
        this.productVariantService = productVariantService;
        this.productVariantPriceSetService = productVariantPriceSetService;
        this.meterRegistry = meterRegistry;

        // Initialize metrics
        this.fetchParseTimer = Timer.builder("autodl.fetch.parse.duration")
                .description("Time taken to fetch CSV, parse, and save/update products")
                .register(meterRegistry);
        this.productsProcessedCounter = Counter.builder("autodl.products.processed.total")
                .description("Total number of products processed from CSV")
                .register(meterRegistry);
        this.productsCreatedCounter = Counter.builder("autodl.products.created.total")
                .description("Total number of new products created")
                .register(meterRegistry);
        this.productsUpdatedCounter = Counter.builder("autodl.products.updated.total")
                .description("Total number of existing products updated")
                .register(meterRegistry);
        this.productsFailedCounter = Counter.builder("autodl.products.failed.total")
                .description("Total number of products that failed to save or update")
                .register(meterRegistry);
        this.tecDocApiTimer = Timer.builder("autodl.tecdoc.api.duration")
                .description("Time taken for TecDoc API calls")
                .tags("operation", "") // Tag will be set dynamically
                .register(meterRegistry);
        this.tecDocApiFailureCounter = Counter.builder("autodl.tecdoc.api.failures.total")
                .description("Total number of TecDoc API call failures")
                .tags("operation", "") // Tag will be set dynamically
                .register(meterRegistry);
        this.skippedItemCounter = Counter.builder("autodl.products.skipped.invalid")
                .description("Total number of items skipped due to being invalid or missing brand mapping")
                .register(meterRegistry);
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
        fetchParseTimer.record(() -> {
            try {
                logger.info("Starting CSV download and processing...");
                File csvFile = getCsvFile(apiUrl);
                List<Brand> brands = fetchBrands();
                logger.info("Fetched brands from TecDoc API");
                convertToItemsAndSave(csvFile, brands);
                deleteCsvFile(csvFile);
                logger.info("Finished CSV download and processing.");
            } catch (CsvDownloadException | NoDataException | SavingCsvException | CsvConversionException e) {
                // Re-throw checked exceptions which record doesn't handle well
                // Or handle them appropriately, maybe log and record failure metric
                 logger.error("Error during download and save process", e);
                 // Consider adding a failure counter metric here
                 throw new RuntimeException(e); // Wrap in unchecked or handle differently
            } catch (Exception e) {
                logger.error("Unexpected error during download and save process", e);
                // Consider adding a failure counter metric here
                throw e; // Re-throw other exceptions
            }
        });
    }

    @Async
    @Transactional
    public void updateDataInDatabase() {
        Stream<ProductEntity> dataStream = productService.getAllProductsAsStream();
        dataStream.forEach(product -> {
            List<Article> articles = tecDocService.fetchArticles(product.getTecDocId(), product.getSupplierId()).block();
            if (articles != null && !articles.isEmpty()) {
                updateArticlesToDatabase(articles.getFirst(), product);
                logger.info("Updated product: {}", product.getTecDocId());
            }
        });
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
            logger.info("CSV data has been successfully saved to file");
        } catch (IOException e) {
            logger.error("Error occurred while saving CSV data to file: {}", e.getMessage(), e);
            throw new SavingCsvException("Failed to write CSV data to file", e);
        }
        return csvFile;
    }

    private List<Brand> fetchBrands() throws NoDataException {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            List<Brand> brands = tecDocService.fetchBrands().block();
            if (brands == null || brands.isEmpty()) {
                tecDocApiFailureCounter.increment(); // Increment failure counter
                throw new NoDataException("No brands received from TecDoc API");
            }
            sample.stop(tecDocApiTimer.tag("operation", "fetchBrands")); // Stop timer on success
            return brands;
        } catch (Exception e) {
            sample.stop(tecDocApiTimer.tag("operation", "fetchBrands")); // Stop timer even on failure
            tecDocApiFailureCounter.increment(); // Increment failure counter
            logger.error("Error fetching brands from TecDoc", e);
            if (e instanceof NoDataException) {
                throw e; // Re-throw specific exception
            }
            throw new RuntimeException("Failed to fetch brands", e); // Wrap other exceptions
        }
    }


    private void convertToItemsAndSave(File csvFile, List<Brand> brands) throws CsvConversionException {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            Map<String, Integer> brandMap = createBrandMap(brands);
            br.lines()
                    .skip(1)
                    .map(this::getItem)
                    .filter(item -> {
                        boolean valid = isValidItem(item, brandMap);
                        if (!valid) {
                            skippedItemCounter.increment(); // Increment skipped counter if invalid
                        }
                        return valid;
                    })
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
        productsProcessedCounter.increment(); // Count every item processed
        String id = item.getTecDocId().replaceAll("[^a-zA-Z0-9-_]", "");

        Product existingProduct = productService.findById(id);
        if (existingProduct == null) {
            logger.info("Saving new product: {}", id);
            Timer.Sample sample = Timer.start(meterRegistry);
            List<Article> articles = null;
            try {
                articles = tecDocService.fetchArticles(item.getTecDocId(), item.getTecDocSupplierID()).block();
                sample.stop(tecDocApiTimer.tag("operation", "fetchArticles")); // Stop timer on success
                if (articles != null && !articles.isEmpty()) {
                    saveArticlesToDatabase(articles.getFirst(), item);
                } else {
                    logger.warn("No articles found for TecDoc ID: {}, Supplier ID: {}", item.getTecDocId(), item.getTecDocSupplierID());
                    tecDocApiFailureCounter.increment(); // Count as failure if no articles returned
                    productsFailedCounter.increment(); // Also count as product failure
                }
            } catch (Exception e) {
                sample.stop(tecDocApiTimer.tag("operation", "fetchArticles")); // Stop timer even on failure
                tecDocApiFailureCounter.increment(); // Increment TecDoc failure counter
                productsFailedCounter.increment(); // Also count as product failure
                logger.error("Error fetching articles for TecDoc ID: {}, Supplier ID: {}", item.getTecDocId(), item.getTecDocSupplierID(), e);
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
            productsCreatedCounter.increment(); // Increment after successful save
        } catch (Exception e) {
            logger.info("Error occurred while saving product: {}, error: {}", item.getTecDocId(), e.getMessage());
            productsFailedCounter.increment(); // Increment failure counter
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
            logger.info("Error occurred while updating product: {}, error: {}", productEntity.getTecDocId(), e.getMessage());
            productsFailedCounter.increment(); // Increment failure counter
        }
    }

    private void updateDarmaDataInDatabase(Item item) {
        InventoryLevel inventoryLevel = new InventoryLevel(item.getMainStock(), item.getSupplierStock(), item.getOtherBranchStock(), item.getTecDocId());
        Price price = new Price(item.getPrice(), item.getTecDocId());

        inventoryLevelService.updateStockedQuantity(inventoryLevel);
        priceService.updateAmount(price);
        productsUpdatedCounter.increment(); // Increment after successful update
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
