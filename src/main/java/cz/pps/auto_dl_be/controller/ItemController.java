package cz.pps.auto_dl_be.controller;

import cz.pps.auto_dl_be.service.CsvService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@EnableScheduling
public class ItemController {
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final CsvService csvService;


    @GetMapping()
    public ResponseEntity<Void> getDetail() {
        try {
            csvService.downloadAndSaveCsvAsItems();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void scheduleGetDetail() {
        try {
            csvService.downloadAndSaveCsvAsItems();
        } catch (Exception e) {
            logger.error("Error has occured during the scheduled cron job: ", e);
        }
    }
}