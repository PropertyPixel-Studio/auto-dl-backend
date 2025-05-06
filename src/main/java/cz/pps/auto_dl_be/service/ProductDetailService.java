package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dao.InventoryLevelDao;
import cz.pps.auto_dl_be.dao.PriceDao;
import cz.pps.auto_dl_be.dao.ProductDao;
import cz.pps.auto_dl_be.dto.ProductDetailDto;
import cz.pps.auto_dl_be.model.InventoryLevelEntity;
import cz.pps.auto_dl_be.model.PriceEntity;
import cz.pps.auto_dl_be.model.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductDetailService {
    private final ProductDao productDao;
    private final InventoryLevelDao inventoryLevelDao;
    private final PriceDao priceDao;

    public List<ProductDetailDto> getProductDetailsById(List<String> ids) {
        List<String> cleanedIds = ids.stream()
                .map(id -> id.replaceAll("[^a-zA-Z0-9-_]", ""))
                .collect(Collectors.toList());

        CompletableFuture<List<ProductEntity>> productsFuture = fetchProducts(cleanedIds);
        CompletableFuture<List<InventoryLevelEntity>> inventoriesFuture = fetchInventories(cleanedIds);
        CompletableFuture<List<PriceEntity>> pricesFuture = fetchPrices(cleanedIds);

        CompletableFuture.allOf(productsFuture, inventoriesFuture, pricesFuture).join();

        try {
            List<ProductEntity> products = productsFuture.get();
            Map<String, InventoryLevelEntity> inventoryMap = createInventoryMap(inventoriesFuture.get());
            Map<String, PriceEntity> priceMap = createPriceMap(pricesFuture.get());

            List<ProductDetailDto> details = new ArrayList<>();
            for (ProductEntity product : products) {
                ProductDetailDto detailDto = new ProductDetailDto();
                detailDto.setProduct(product);
                detailDto.setInventory(inventoryMap.get(product.getId()));
                detailDto.setPrice(priceMap.get(product.getId()));
                details.add(detailDto);
            }

            return details;
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching data", e);
        }
    }

    @Async
    protected CompletableFuture<List<ProductEntity>> fetchProducts(List<String> ids) {
        List<String> productIds = ids.stream()
                .map(id -> "prod_" + id)
                .collect(Collectors.toList());
        return productDao.findByIdIn(productIds);
    }

    @Async
    protected CompletableFuture<List<InventoryLevelEntity>> fetchInventories(List<String> ids) {
        List<String> inventoryIds = ids.stream()
                .map(id -> "iitem_" + id)
                .collect(Collectors.toList());
        return inventoryLevelDao.findByInventoryItemIdIn(inventoryIds);
    }

    @Async
    protected CompletableFuture<List<PriceEntity>> fetchPrices(List<String> ids) {
        List<String> priceIds = ids.stream()
                .map(id -> "price_" + id)
                .collect(Collectors.toList());
        return priceDao.findByPriceListIdIn(priceIds);
    }

    private Map<String, InventoryLevelEntity> createInventoryMap(List<InventoryLevelEntity> inventories) {
        return inventories.stream()
                .collect(Collectors.toMap(
                    inv -> "prod_" + inv.getInventoryItemId().replace("iitem_", ""),
                    inv -> inv,
                    (a, b) -> a
                ));
    }

    private Map<String, PriceEntity> createPriceMap(List<PriceEntity> prices) {
        return prices.stream()
                .collect(Collectors.toMap(
                    price -> "prod_" + price.getPriceListId().replace("price_", ""),
                    price -> price,
                    (a, b) -> a
                ));
    }
}