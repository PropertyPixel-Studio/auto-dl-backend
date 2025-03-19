package cz.pps.auto_dl_be.controller;

import cz.pps.auto_dl_be.model.ProductEntity;
import cz.pps.auto_dl_be.service.MedusaService;
import cz.pps.auto_dl_be.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@EnableScheduling
public class ItemController {
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final MedusaService medusaService;
    private final ProductService productService;


    @GetMapping()
    public ResponseEntity<Void> saveItemsIntoDB() {
        try {
            medusaService.downloadAndSaveCsvAsItems();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductEntity>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProductEntity> products = productService.getAllProducts(page, size);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

//    @GetMapping("/update")
//    public ResponseEntity<Void> updateItemsInDB() {
//        productService.updateProducts();
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @GetMapping("/heartbeat")
    public ResponseEntity<Void> heartbeat() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void scheduleSaveItemsIntoDB() {
        try {
            medusaService.downloadAndSaveCsvAsItems();
        } catch (Exception e) {
            logger.error("Error has occured during the scheduled cron job: ", e);
        }
    }
}