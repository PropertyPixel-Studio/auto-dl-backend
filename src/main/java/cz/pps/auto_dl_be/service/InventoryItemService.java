package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dto.medusa.InventoryItem;
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
                     "VALUES (:id, :sku, :title, :description, NOW(), NOW(), NULL)";
        entityManager.createNativeQuery(sql)
                     .setParameter("id", inventoryItem.getId())
                     .setParameter("sku", inventoryItem.getSku())
                     .setParameter("title", inventoryItem.getTitle())
                     .setParameter("description", inventoryItem.getDescription())
                     .executeUpdate();
    }

    @Transactional
    public void updateInventoryItem(InventoryItem inventoryItem) {
        String sql = "UPDATE INVENTORY_ITEM SET " +
                "title = :title, " +
                "description = :description, " +
                "updated_at = NOW() " +
                "WHERE id = :id";
        entityManager.createNativeQuery(sql)
                .setParameter("title", inventoryItem.getTitle())
                .setParameter("description", inventoryItem.getDescription())
                .setParameter("id", inventoryItem.getId())
                .executeUpdate();
    }
}
