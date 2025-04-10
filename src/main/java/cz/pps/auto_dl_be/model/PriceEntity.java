package cz.pps.auto_dl_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "price")
public class PriceEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "price_set_id", nullable = false)
    private String priceSetId;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "raw_amount", nullable = false)
    private String rawAmount;

    @Column(name = "rules_count")
    private Integer rulesCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;

    @Column(name = "price_list_id")
    private String priceListId;

    @Column(name = "amount", nullable = false)
    private String amount;

    @Column(name = "min_quantity")
    private Integer minQuantity;

    @Column(name = "max_quantity")
    private Integer maxQuantity;

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