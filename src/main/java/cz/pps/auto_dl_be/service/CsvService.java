package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.config.CsvConfig;
import cz.pps.auto_dl_be.dao.ItemDao;
import cz.pps.auto_dl_be.model.Item;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.*;

@Service
@RequiredArgsConstructor
public class CsvService {

    private final ItemDao itemDao;
//    private final CsvConfig csvConfig;

    private static Item getItem(String line) {
        String[] values = line.split(",");
        Item item = new Item();
        item.setProductCode(values[0]);
        item.setManufacturer(values[1]);
        item.setProductName(values[2]);
        item.setMainStock(values[3]);
        item.setOtherBranchStock(values[4]);
        item.setSupplierStock(values[5]);
        item.setPrice(values[6]);
        item.setVatRate(values[7]);
        item.setCurrency(values[8]);
        item.setDeposit(values[9]);
        item.setTecDocld(values[10]);
        item.setTecDocSupplierName(values[11]);
        item.setName(values[12]);
        return item;
    }

    public void downloadAndSaveCsvAsItems(String apiUrl) throws IOException {
        // Step 1: Download the CSV file from the API
        RestTemplate restTemplate = new RestTemplate();
        String csvData;
        try {
            csvData = restTemplate.getForObject(apiUrl, String.class);
        } catch (RestClientException e) {
            throw new IOException("Failed to download CSV data from API", e);
        }

        if (csvData == null) {
            throw new IOException("CSV data is null");
        }

        File csvFile = new File("data.csv");
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write(csvData);
        }

        // Step 2: Read the CSV file and save data as Item entities
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                Item item = getItem(line);
                itemDao.save(item);
            }
        }

        // Step 3: Delete the CSV file
        if (csvFile.exists()) {
            csvFile.delete();
        }
    }

    @PostConstruct
    public void init() {
        try {
            downloadAndSaveCsvAsItems("xxx");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}