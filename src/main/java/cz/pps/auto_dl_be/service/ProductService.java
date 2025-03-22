package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dao.ProductDao;
import cz.pps.auto_dl_be.dto.medusa.Product;
import cz.pps.auto_dl_be.model.ProductEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductDao productDao;
    @PersistenceContext
    private EntityManager entityManager;

    private static String getJSON(Product product) {
        return "{" +
                "\"dataSupplierId\": " + product.getSupplierId() +
                ", \"articleNumber\": \"" + product.getTecDocId() +
                "\", \"mfrName\": \"" + product.getMfrName() +
                "\", \"oemNumber\": " + product.getOemNumber() +
                "}";
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

    @Transactional
    public Page<ProductEntity> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productDao.findAll(pageable);
    }

    @Transactional
    public void saveWithQuery(Product product) {
        String sql = "INSERT INTO PRODUCT (id, title, handle, subtitle, description, is_giftcard, status, thumbnail, weight, length, height, width, origin_country, hs_code, mid_code, material, collection_id, type_id, discountable, external_id, created_at, updated_at, metadata) " +
                "VALUES (:id, :title, :handle, :subtitle, :description, FALSE, 'published', :thumbnail, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, TRUE, :external_id, NOW(), NOW(), CAST(:metadata AS jsonb));";
        entityManager.createNativeQuery(sql)
                .setParameter("id", product.getId())
                .setParameter("title", product.getTitle())
                .setParameter("handle", product.getHandle())
                .setParameter("subtitle", product.getSubtitle())
                .setParameter("description", product.getDescription())
                .setParameter("thumbnail", product.getThumbnail())
                .setParameter("external_id", product.getExternalId())
                .setParameter("metadata", getJSON(product))
                .executeUpdate();
        entityManager.flush();
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

    @Transactional
    public void updateProduct(Product product) {
        String sql = "UPDATE PRODUCT SET " +
                "title = :title, " +
                "handle = :handle, " +
                "subtitle = :subtitle, " +
                "description = :description, " +
                "thumbnail = :thumbnail, " +
                "external_id = :external_id, " +
                "metadata = CAST(:metadata AS jsonb), " +
                "updated_at = NOW() " +
                "WHERE id = :id";
        entityManager.createNativeQuery(sql)
                .setParameter("title", product.getTitle())
                .setParameter("handle", product.getHandle())
                .setParameter("subtitle", product.getSubtitle())
                .setParameter("description", product.getDescription())
                .setParameter("thumbnail", product.getThumbnail())
                .setParameter("external_id", product.getExternalId())
                .setParameter("metadata", getJSON(product))
                .setParameter("id", product.getId())
                .executeUpdate();
        entityManager.flush();
    }

    @Transactional
    public Stream<ProductEntity> getAllProductsAsStream() {
        return productDao.findAllAsStream();
    }
}