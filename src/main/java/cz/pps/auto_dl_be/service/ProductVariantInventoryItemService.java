package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.model.medusa.ProductVariantInventoryItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ProductVariantInventoryItemService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveWithQuery(ProductVariantInventoryItem productVariantInventoryItem) {
        String sql = "INSERT INTO PRODUCT_VARIANT_INVENTORY_ITEM (variant_id, inventory_item_id, id, required_quantity, created_at, updated_at, deleted_at) " +
                "VALUES ('" + productVariantInventoryItem.getVariant_id() + "', '" + productVariantInventoryItem.getInventory_item_id() +
                "', '" + productVariantInventoryItem.getId() + "', 1, NOW(), NOW(), NULL);";
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
