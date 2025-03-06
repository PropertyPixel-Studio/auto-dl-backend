package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.model.medusa.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
                "FALSE, 'published', '" + product.getThumbnail() + "', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, TRUE, '" + product.getExternalId() + "', NOW(), NOW(), " + getJSON(product) + ") " +
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

    @Transactional
    public Product findById(String id) {
        String sql = "SELECT id, title, handle, subtitle, description, thumbnail, external_id, " +
                "metadata->>'dataSupplierId' AS supplierId, " +
                "metadata->>'articleNumber' AS tecDocId, " +
                "metadata->>'mfrName' AS mfrName, " +
                "metadata->>'oemNumber' AS oemNumber " +
                "FROM PRODUCT WHERE id = :id";
        try {
            Object[] result = (Object[]) entityManager.createNativeQuery(sql)
                    .setParameter("id", "prod_" + id)
                    .getSingleResult();
            return getProduct(result);
        } catch (NoResultException e) {
            return null;
        }
    }

    private static String getJSON(Product product) {
        return "'{" +
                "\"dataSupplierId\": \"" + product.getSupplierId() +
                "\", \"articleNumber\": \"" + product.getTecDocId() +
                "\", \"mfrName\": \"" + product.getMfrName() +
                "\", \"oemNumber\": \"" + product.getOemNumber() +
                "\"}'::jsonb)";
    }

    private static Product getProduct(Object[] result) {
        Product product = new Product();
        product.setId((String) result[0]);
        product.setTitle((String) result[1]);
        product.setHandle((String) result[2]);
        product.setSubtitle((String) result[3]);
        product.setDescription((String) result[4]);
        product.setThumbnail((String) result[5]);
        product.setExternalId((String) result[6]);
        product.setSupplierId((String) result[7]);
        product.setTecDocId((String) result[8]);
        product.setMfrName((String) result[9]);
        product.setOemNumber((String) result[10]);
        return product;
    }
}