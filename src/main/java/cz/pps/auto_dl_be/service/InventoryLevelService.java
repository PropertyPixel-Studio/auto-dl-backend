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
        // Create JSON strings
        String rawStockedQuantityJson = "{\"value\": " + inventoryLevel.getStocked_quantity() + ", \"precision\": 20}";
        String rawReservedQuantityJson = "{\"value\": 0, \"precision\": 20}";
        String rawIncomingQuantityJson = "{\"value\": 0, \"precision\": 20}";

        String sql = "INSERT INTO INVENTORY_LEVEL (id, inventory_item_id, location_id, stocked_quantity, reserved_quantity, incoming_quantity, raw_stocked_quantity, raw_reserved_quantity, raw_incoming_quantity, created_at, updated_at, deleted_at) " +
                "VALUES (:id, :inventory_item_id, :location_id, :stocked_quantity, 0, 0, " +
                "CAST(:raw_stocked_quantity AS jsonb), " +
                "CAST(:raw_reserved_quantity AS jsonb), " +
                "CAST(:raw_incoming_quantity AS jsonb), NOW(), NOW(), NULL)";

        entityManager.createNativeQuery(sql)
                .setParameter("id", inventoryLevel.getId())
                .setParameter("inventory_item_id", inventoryLevel.getInventory_item_id())
                .setParameter("location_id", inventoryLevel.getLocation_id())
                .setParameter("stocked_quantity", inventoryLevel.getStocked_quantity())
                .setParameter("raw_stocked_quantity", rawStockedQuantityJson)
                .setParameter("raw_reserved_quantity", rawReservedQuantityJson)
                .setParameter("raw_incoming_quantity", rawIncomingQuantityJson)
                .executeUpdate();

        entityManager.flush();
    }

    @Transactional
    public void updateStockedQuantity(InventoryLevel inventoryLevel) {
        String rawStockedQuantityJson = "{\"value\": " + inventoryLevel.getStocked_quantity() + ", \"precision\": 20}";

        String sql = "UPDATE INVENTORY_LEVEL SET " +
                "stocked_quantity = :stocked_quantity, " +
                "raw_stocked_quantity = CAST(:raw_stocked_quantity AS jsonb), " +
                "updated_at = NOW() " +
                "WHERE id = :id";

        entityManager.createNativeQuery(sql)
                .setParameter("stocked_quantity", inventoryLevel.getStocked_quantity())
                .setParameter("raw_stocked_quantity", rawStockedQuantityJson)
                .setParameter("id", inventoryLevel.getId())
                .executeUpdate();

        entityManager.flush();
    }
}
