package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.model.medusa.ProductVariantPriceSet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ProductVariantPriceSetService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveWithQuery(ProductVariantPriceSet productVariantPriceSet) {
        String sql = "INSERT INTO product_variant_price_set (variant_id, price_set_id, id, created_at, updated_at, deleted_at) " +
                "VALUES ('" + productVariantPriceSet.getVariant_id() + "', '" + productVariantPriceSet.getPrice_set_id() + "', '" + productVariantPriceSet.getId() + "', NOW(), NOW(), NULL);";
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
