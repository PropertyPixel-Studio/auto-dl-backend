package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.model.medusa.InventoryItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class InventoryItemService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveWithQuery(InventoryItem inventoryItem) {
        String sql = "INSERT INTO INVENTORY_ITEM (id, sku, title, description, created_at, updated_at, deleted_at) " +
                "VALUES ('" + inventoryItem.getId() + "', '" + inventoryItem.getSku() + "', '" + inventoryItem.getTitle() + "', '" + inventoryItem.getDescription() + "', NOW(), NOW(), NULL);";
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
