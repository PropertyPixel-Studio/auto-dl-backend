package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.model.medusa.ProductVariant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ProductVariantService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveWithQuery(ProductVariant productVariant) {
        String sql = "INSERT INTO PRODUCT_VARIANT (id, title, sku, barcode, ean, upc, allow_backorder, manage_inventory, hs_code, origin_country, mid_code, material, weight, length, height, width, metadata, variant_rank, product_id, created_at, updated_at, deleted_at) " +
                "VALUES ('" + productVariant.getId() + "', '" + productVariant.getTitle() + "', '" + productVariant.getSku() + "', '" + productVariant.getBarcode() + "', '" + productVariant.getEan() + "', NULL, 'FALSE', 'TRUE', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, '" + productVariant.getProduct_id() + "', NOW(), NOW(), NULL);";
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
