package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dao.ItemDao;
import cz.pps.auto_dl_be.dto.brands.Brand;
import cz.pps.auto_dl_be.dto.detail.Article;
import cz.pps.auto_dl_be.exception.CsvConversionException;
import cz.pps.auto_dl_be.exception.CsvDownloadException;
import cz.pps.auto_dl_be.exception.NoDataException;
import cz.pps.auto_dl_be.exception.SavingCsvException;
import cz.pps.auto_dl_be.model.Item;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CsvService {

    private static final Logger logger = LoggerFactory.getLogger(CsvService.class);
    private final ItemDao itemDao;
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
            br.lines() // Stream<Item> itemStream =
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
                    .forEach(itemDao::save);
//            addArticleDetail(itemStream);
        } catch (IOException e) {
            throw new CsvConversionException("Failed to convert CSV data to items", e);
        }
    }

    private void addArticleDetail(Stream<Item> itemStream) {
        itemStream.forEach(item -> {
            List<Article> articles = tecDocService.fetchDetail(item.getTecDocld(), item.getTecDocSupplierID()).block();
            if (articles != null && !articles.isEmpty()) {
                item.setArticleDetailFull(articles.toString());
                itemDao.save(item);
            }
        });
    }

    @PostConstruct
    public void init() {
        try {
            downloadAndSaveCsvAsItems();
            logger.info("Application has finished loading and is ready.");
        } catch (CsvDownloadException | NoDataException | SavingCsvException | CsvConversionException e) {
            logger.error("Error occurred during application initialization: {}", e.getMessage(), e);
        }
    }
}