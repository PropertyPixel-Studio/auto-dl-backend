package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dto.medusa.Price;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PriceService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveWithQuery(Price price) {
        String sql = "INSERT INTO PRICE (id, title, price_set_id, currency_code, raw_amount, rules_count, created_at, updated_at, deleted_at, price_list_id, amount, min_quantity, max_quantity) " +
                "VALUES (:id, :title, :price_set_id, :currency_code, :raw_amount, 0, NOW(), NOW(), NULL, NULL, :amount, NULL, NULL) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "title = EXCLUDED.title, " +
                "price_set_id = EXCLUDED.price_set_id, " +
                "currency_code = EXCLUDED.currency_code, " +
                "raw_amount = EXCLUDED.raw_amount, " +
                "amount = EXCLUDED.amount, " +
                "updated_at = NOW();";
        entityManager.createNativeQuery(sql)
                .setParameter("id", price.getId())
                .setParameter("title", price.getTitle())
                .setParameter("price_set_id", price.getPrice_set_id())
                .setParameter("currency_code", price.getCurrency_code())
                .setParameter("raw_amount", "{\"value\": \"" + price.getAmount() + "\", \"precision\": 2}")
                .setParameter("amount", price.getAmount())
                .executeUpdate();
    }

    @Transactional
    public void updateAmount(Price price) {
        String sql = "UPDATE PRICE SET " +
                "raw_amount = :raw_amount, " +
                "amount = :amount, " +
                "updated_at = NOW() " +
                "WHERE id = :id";
        entityManager.createNativeQuery(sql)
                .setParameter("raw_amount", "{\"value\": \"" + price.getAmount() + "\", \"precision\": 2}")
                .setParameter("amount", price.getAmount())
                .setParameter("id", price.getId())
                .executeUpdate();
    }

    @Transactional
    public void updatePrice(Price price) {
        String sql = "UPDATE PRICE SET " +
                "title = :title, " +
                "updated_at = NOW() " +
                "WHERE id = :id";
        entityManager.createNativeQuery(sql)
                .setParameter("title", price.getTitle())
                .setParameter("id", price.getId())
                .executeUpdate();
    }
}
