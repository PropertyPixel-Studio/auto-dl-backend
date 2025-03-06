package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.model.medusa.InventoryLevel;
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
                "VALUES ('" + inventoryLevel.getId() + "', '" + inventoryLevel.getInventory_item_id() + "', '" + inventoryLevel.getLocation_id() + "', " + inventoryLevel.getStocked_quantity() + ", 0, 0, " +
                "'{\"value\": \"" + inventoryLevel.getStocked_quantity() + "\", \"precision\": 20}'::jsonb, " +
                "'{\"value\": \"0\", \"precision\": 20}'::jsonb, " +
                "'{\"value\": \"0\", \"precision\": 20}'::jsonb, NOW(), NOW(), NULL)" +
                "ON CONFLICT (id) DO UPDATE SET " +
                "inventory_item_id = EXCLUDED.inventory_item_id, " +
                "location_id = EXCLUDED.location_id, " +
                "stocked_quantity = EXCLUDED.stocked_quantity, " +
                "reserved_quantity = EXCLUDED.reserved_quantity, " +
                "incoming_quantity = EXCLUDED.incoming_quantity, " +
                "raw_stocked_quantity = EXCLUDED.raw_stocked_quantity, " +
                "raw_reserved_quantity = EXCLUDED.raw_reserved_quantity, " +
                "raw_incoming_quantity = EXCLUDED.raw_incoming_quantity, " +
                "updated_at = NOW();";
        entityManager.createNativeQuery(sql).executeUpdate();
    }

    @Transactional
    public void updateStockedQuantity(InventoryLevel inventoryLevel) {
        String sql = "UPDATE INVENTORY_LEVEL SET " +
                "stocked_quantity = " + inventoryLevel.getStocked_quantity() + ", " +
                "raw_stocked_quantity = '{\"value\": \"" + inventoryLevel.getStocked_quantity() + "\", \"precision\": 20}'::jsonb, " +
                "updated_at = NOW() " +
                "WHERE id = '" + inventoryLevel.getId() + "';";
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
