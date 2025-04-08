package cz.pps.auto_dl_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "inventory_level")
public class InventoryLevelEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;

    @Column(name = "inventory_item_id", nullable = false)
    private String inventoryItemId;

    @Column(name = "location_id", nullable = false)
    private String locationId;

    @Column(name = "stocked_quantity", nullable = false)
    private BigDecimal stockedQuantity = BigDecimal.ZERO;

    @Column(name = "reserved_quantity", nullable = false)
    private BigDecimal reservedQuantity = BigDecimal.ZERO;

    @Column(name = "incoming_quantity", nullable = false)
    private BigDecimal incomingQuantity = BigDecimal.ZERO;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "raw_stocked_quantity")
    private String rawStockedQuantity;

    @Column(name = "raw_reserved_quantity", columnDefinition = "jsonb")
    private String rawReservedQuantity;

    @Column(name = "raw_incoming_quantity", columnDefinition = "jsonb")
    private String rawIncomingQuantity;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }
}