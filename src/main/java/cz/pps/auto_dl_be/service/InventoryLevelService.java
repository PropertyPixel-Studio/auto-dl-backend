package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dto.medusa.InventoryLevel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class InventoryLevelService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveWithQuery(InventoryLevel inventoryLevel) {
        String sql = "INSERT INTO INVENTORY_LEVEL (id, inventory_item_id, location_id, stocked_quantity, reserved_quantity, incoming_quantity, raw_stocked_quantity, raw_reserved_quantity, raw_incoming_quantity, created_at, updated_at, deleted_at) " +
                "VALUES (:id, :inventory_item_id, :location_id, :stocked_quantity, 0, 0, " +
                "'{\"value\": :stocked_quantity, \"precision\": 20}'::jsonb, " +
                "'{\"value\": \"0\", \"precision\": 20}'::jsonb, " +
                "'{\"value\": \"0\", \"precision\": 20}'::jsonb, NOW(), NOW(), NULL)";
        entityManager.createNativeQuery(sql)
                .setParameter("id", inventoryLevel.getId())
                .setParameter("inventory_item_id", inventoryLevel.getInventory_item_id())
                .setParameter("location_id", inventoryLevel.getLocation_id())
                .setParameter("stocked_quantity", inventoryLevel.getStocked_quantity())
                .executeUpdate();
    }

    @Transactional
    public void updateStockedQuantity(InventoryLevel inventoryLevel) {
        String sql = "UPDATE INVENTORY_LEVEL SET " +
                "stocked_quantity = :stocked_quantity, " +
                "raw_stocked_quantity = '{\"value\": :stocked_quantity, \"precision\": 20}'::jsonb, " +
                "updated_at = NOW() " +
                "WHERE id = :id";
        entityManager.createNativeQuery(sql)
                .setParameter("stocked_quantity", inventoryLevel.getStocked_quantity())
                .setParameter("id", inventoryLevel.getId())
                .executeUpdate();
    }
}
