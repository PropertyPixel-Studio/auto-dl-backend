package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dao.ProductDao;
import cz.pps.auto_dl_be.dto.medusa.Product;
import cz.pps.auto_dl_be.model.ProductEntity;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    private final ProductDao productDao;
    private final MeterRegistry meterRegistry;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private Counter productCreatedCounter;
    private Counter productUpdatedCounter;
    private Counter productQueryCounter;
    private Counter errorCounter;
    private Timer productCreationTimer;
    private Timer productUpdateTimer;
    
    private AtomicInteger dailyCreatedProducts;
    private AtomicInteger dailyUpdatedProducts;
    
    @PostConstruct
    private void initMetrics() {
        productCreatedCounter = Counter.builder("product.created.count")
                .description("Count of created products")
                .register(meterRegistry);
                
        productUpdatedCounter = Counter.builder("product.updated.count")
                .description("Count of updated products")
                .register(meterRegistry);
                
        productQueryCounter = Counter.builder("product.query.count")
                .description("Count of product queries")
                .register(meterRegistry);
                
        errorCounter = Counter.builder("product.error.count")
                .description("Count of errors in product operations")
                .register(meterRegistry);
                
        productCreationTimer = Timer.builder("product.creation.time")
                .description("Time taken to create a product")
                .register(meterRegistry);
                
        productUpdateTimer = Timer.builder("product.update.time")
                .description("Time taken to update a product")
                .register(meterRegistry);
        
        dailyCreatedProducts = new AtomicInteger(0);
        dailyUpdatedProducts = new AtomicInteger(0);
        
        meterRegistry.gauge("product.daily.created", dailyCreatedProducts);
        meterRegistry.gauge("product.daily.updated", dailyUpdatedProducts);
    }

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
        productQueryCounter.increment();
        try {
            Pageable pageable = PageRequest.of(page, size);
            return productDao.findAll(pageable);
        } catch (Exception e) {
            errorCounter.increment();
            logger.error("Error getting all products: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void saveWithQuery(Product product) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
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
            productCreatedCounter.increment();
            dailyCreatedProducts.incrementAndGet();
        } catch (Exception e) {
            errorCounter.increment();
            logger.error("Error saving product: {}", e.getMessage(), e);
            throw e;
        } finally {
            sample.stop(productCreationTimer);
        }
    }

    @Transactional
    public Product findById(String id) {
        productQueryCounter.increment();
        try {
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
        } catch (Exception e) {
            errorCounter.increment();
            logger.error("Error finding product by ID: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void updateProduct(Product product) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
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
            productUpdatedCounter.increment();
            dailyUpdatedProducts.incrementAndGet();
        } catch (Exception e) {
            errorCounter.increment();
            logger.error("Error updating product: {}", e.getMessage(), e);
            throw e;
        } finally {
            sample.stop(productUpdateTimer);
        }
    }

    @Transactional
    public Stream<ProductEntity> getAllProductsAsStream() {
        productQueryCounter.increment();
        try {
            return productDao.findAllAsStream();
        } catch (Exception e) {
            errorCounter.increment();
            logger.error("Error getting all products as stream: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Integer getProductCount() {
        try {
            return Math.toIntExact(productDao.count());
        } catch (Exception e) {
            errorCounter.increment();
            logger.error("Error getting product count: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    public void resetDailyCounters() {
        logger.info("Resetting daily counters. Products created today: {}, Products updated today: {}", 
                dailyCreatedProducts.get(), dailyUpdatedProducts.get());
        dailyCreatedProducts.set(0);
        dailyUpdatedProducts.set(0);
    }
}