package cz.pps.auto_dl_be.controller;

import cz.pps.auto_dl_be.config.CsvConfig;
import cz.pps.auto_dl_be.model.Item;
import cz.pps.auto_dl_be.service.CsvService;
import cz.pps.auto_dl_be.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
//    private final CsvService csvService;

    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

//    @GetMapping("/csv")
//    public void getCsvFile() {
//        try {
//            csvService.downloadAndSaveCsvAsItems(csvConfig.getUrl());
//        } catch (IOException e) {
//            // Handle the exception, e.g., log the error or return a default value
//            e.printStackTrace();
//        }
//    }
}