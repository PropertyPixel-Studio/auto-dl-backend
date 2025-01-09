package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dao.ItemDao;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.*;

@Service
@RequiredArgsConstructor
public class CsvService {

    private static final Logger logger = LoggerFactory.getLogger(CsvService.class);
    private final ItemDao itemDao;
    @Value("${darma.url}")
    private String apiUrl;

    private static Item getItem(String line) {
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
        RestTemplate restTemplate = new RestTemplate();
        String csvData;
        logger.info("Downloading CSV data from API...");
        try {
            csvData = restTemplate.getForObject(apiUrl, String.class);
        } catch (RestClientException e) {
            throw new CsvDownloadException("Failed to download CSV data from API", e);
        }

        if (csvData == null) {
            throw new NoDataException("CSV data is null");
        }

        File csvFile = new File("data.csv");
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write(csvData);
        } catch (IOException e) {
            throw new SavingCsvException("Failed to write CSV data to file", e);
        }
        return csvFile;
    }

    public void downloadAndSaveCsvAsItems() throws CsvDownloadException, NoDataException, SavingCsvException, CsvConversionException {
        // Step 1: Download the CSV file from the API
        File csvFile = getCsvFile(this.apiUrl);

        // Step 2: Read the CSV file and save data as Item entities
        convertToItems(csvFile);

        // Step 3: Delete the CSV file
        if (csvFile.exists()) {
            csvFile.delete();
        }
    }

    private void convertToItems(File csvFile) throws CsvConversionException {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the first line
                }
                Item item = getItem(line);
                itemDao.save(item);
            }
        } catch (IOException e) {
            throw new CsvConversionException("Failed to convert CSV data to items", e);
        }
    }

    @PostConstruct
    public void init() {
        try {
            downloadAndSaveCsvAsItems();
            logger.info("Application has finished loading and is ready.");
        } catch (CsvDownloadException | NoDataException | SavingCsvException | CsvConversionException e) {
            e.printStackTrace();
        }
    }
}