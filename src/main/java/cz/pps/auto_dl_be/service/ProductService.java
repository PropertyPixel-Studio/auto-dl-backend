package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.model.medusa.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveWithQuery(Product product) {
        String sql = "INSERT INTO PRODUCT (id, title, handle, subtitle, description, is_giftcard, status, thumbnail, weight, length, height, width, origin_country, hs_code, mid_code, material, collection_id, type_id, discountable, external_id, created_at, updated_at, metadata) " +
                "VALUES ('" + product.getId() + "', '" + product.getTitle() + "', '" + product.getHandle() + "', '" + product.getSubtitle() + "', '" + product.getDescription() + "', " +
                "FALSE, 'published', '" + product.getThumbnail() + "', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, TRUE, '" + product.getExternalId() + "', NOW(), NOW(), " +
                "'{\"dataSupplierId\": " + product.getSupplierId() + ", \"articleNumber\": \"" + product.getTecDocId() + "\", \"mfrName\": \"" + product.getMfrName() + "\"}'::jsonb)" +
                "ON CONFLICT (id) DO UPDATE SET " +
                "title = EXCLUDED.title, " +
                "handle = EXCLUDED.handle, " +
                "subtitle = EXCLUDED.subtitle, " +
                "description = EXCLUDED.description, " +
                "thumbnail = EXCLUDED.thumbnail, " +
                "external_id = EXCLUDED.external_id, " +
                "updated_at = NOW(), " +
                "metadata = EXCLUDED.metadata;";
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}