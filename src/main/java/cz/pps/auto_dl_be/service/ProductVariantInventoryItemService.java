package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dto.medusa.ProductVariantInventoryItem;
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
                     "VALUES (:variant_id, :inventory_item_id, :id, 1, NOW(), NOW(), NULL)";
        entityManager.createNativeQuery(sql)
                     .setParameter("variant_id", productVariantInventoryItem.getVariant_id())
                     .setParameter("inventory_item_id", productVariantInventoryItem.getInventory_item_id())
                     .setParameter("id", productVariantInventoryItem.getId())
                     .executeUpdate();
    }
}
