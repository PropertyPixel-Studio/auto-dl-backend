package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.model.medusa.ProductSalesChannel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ProductSalesChannelService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveWithQuery(ProductSalesChannel productSalesChannel) {
        String sql = "INSERT INTO PRODUCT_SALES_CHANNEL (product_id, sales_channel_id, id, created_at, updated_at, deleted_at) " +
                "VALUES ('" + productSalesChannel.getProduct_id() + "', '" + productSalesChannel.getSales_channel_id() + "', '" + productSalesChannel.getId() + "', NOW(), NOW(), NULL) " +
                "ON CONFLICT (product_id, sales_channel_id) DO UPDATE SET " +
                "updated_at = NOW();";
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
