package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dto.medusa.ProductVariant;
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
                "VALUES (:id, :title, :sku, :barcode, :ean, NULL, 'true', 'true', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, :product_id, NOW(), NOW(), NULL)";
        entityManager.createNativeQuery(sql)
                .setParameter("id", productVariant.getId())
                .setParameter("title", productVariant.getTitle())
                .setParameter("sku", productVariant.getSku())
                .setParameter("barcode", productVariant.getBarcode())
                .setParameter("ean", productVariant.getEan())
                .setParameter("product_id", productVariant.getProduct_id())
                .executeUpdate();
    }

    @Transactional
    public void updateProductVariant(ProductVariant productVariant) {
        String sql = "UPDATE PRODUCT_VARIANT SET " +
                "title = '" + productVariant.getTitle() + "', " +
                "sku = '" + productVariant.getSku() + "', " +
                "barcode = '" + productVariant.getBarcode() + "', " +
                "ean = '" + productVariant.getEan() + "', " +
                "updated_at = NOW() " +
                "WHERE id = '" + productVariant.getId() + "'";
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
