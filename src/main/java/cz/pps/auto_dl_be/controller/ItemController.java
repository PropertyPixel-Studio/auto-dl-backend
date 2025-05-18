package cz.pps.auto_dl_be.controller;

import cz.pps.auto_dl_be.dto.ProductDetailDto;
import cz.pps.auto_dl_be.dto.ProductIdsRequestDto;
import cz.pps.auto_dl_be.dto.detail.Article;
import cz.pps.auto_dl_be.model.Item;
import cz.pps.auto_dl_be.model.ProductEntity;
import cz.pps.auto_dl_be.service.MedusaService;
import cz.pps.auto_dl_be.service.ProductDetailService;
import cz.pps.auto_dl_be.service.ProductService;
import cz.pps.auto_dl_be.service.TecDocService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@EnableScheduling
@SecurityRequirement(name = "apiKey")
public class ItemController {
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final MedusaService medusaService;
    private final ProductService productService;
    private final ProductDetailService productDetailService;
    private final TecDocService tecDocService;


    @Operation(summary = "Saves items to the database", description = "Downloads a CSV file and saves items to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items were successfully saved."),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred.", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<Void> saveItemsIntoDB() {
        try {
            medusaService.downloadAndSaveCsvAsItems();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Updates items in the database", description = "Updates data in the database.")
    @ApiResponse(responseCode = "200", description = "Items were successfully updated.")
    @GetMapping("/update")
    public ResponseEntity<Void> updateItemsInB() {
        medusaService.updateDataInDatabase();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Fetches all products", description = "Returns a paginated list of all products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of products was successfully returned."),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters.", content = @Content)
    })
    @GetMapping("/products")
    public ResponseEntity<Page<ProductEntity>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProductEntity> products = productService.getAllProducts(page, size);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Service availability check", description = "Returns the status of the service.")
    @ApiResponse(responseCode = "200", description = "The service is available.")
    @GetMapping("/heartbeat")
    public ResponseEntity<Void> heartbeat() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Fetches product details", description = "Returns product details based on their IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product details were successfully returned."),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred.", content = @Content)
    })
    @PostMapping("/product-details")
    public ResponseEntity<List<ProductDetailDto>> getProductDetails(@RequestBody ProductIdsRequestDto request) {
        try {
            List<ProductDetailDto> productDetails = productDetailService.getProductDetailsById(request.getIds());
            return new ResponseEntity<>(productDetails, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching product details: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void scheduleSaveItemsIntoDB() {
        try {
            medusaService.downloadAndSaveCsvAsItems();
        } catch (Exception e) {
            logger.error("Error has occured during the scheduled cron job: ", e);
        }
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void scheduledUpdateItemsInB() {
        medusaService.updateDataInDatabase();
    }

    @Operation(summary = "Add single test product", description = "Adds a single test product to the database for testing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test product was successfully added."),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters.", content = @Content),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred.", content = @Content)
    })
    @PostMapping("/test-product")
    public ResponseEntity<String> addTestProduct(
            @RequestParam String tecDocId,
            @RequestParam String supplierId,
            @RequestParam(defaultValue = "1") String mainStock,
            @RequestParam(defaultValue = "0") String otherBranchStock,
            @RequestParam(defaultValue = "0") String supplierStock,
            @RequestParam(defaultValue = "1000") String price) {
        
        try {
            List<Article> articles = tecDocService.fetchArticles(tecDocId, supplierId).block();
            
            if (articles == null || articles.isEmpty()) {
                return ResponseEntity.badRequest().body("No articles found for the given tecDocId and supplierId");
            }
            
            Article article = articles.getFirst();
            
            // Create an Item with the necessary data using constructor
            Item item = new Item(
                null,  // productCode
                null,  // manufacturer
                null,  // productName
                mainStock,
                otherBranchStock,
                supplierStock,
                price,
                null,  // vatRate
                null,  // currency
                null,  // deposit
                tecDocId,
                null,  // tecDocSupplierName
                supplierId
            );
            
            // Save the article to the database
            medusaService.saveArticlesToDatabase(article, item);
            
            return ResponseEntity.ok("Test product added successfully: " + tecDocId);
        } catch (Exception e) {
            logger.error("Error adding test product: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error adding test product: " + e.getMessage());
        }
    }
}