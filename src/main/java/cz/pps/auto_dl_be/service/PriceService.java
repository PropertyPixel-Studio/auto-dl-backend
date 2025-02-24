package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.model.medusa.Price;
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
                "VALUES ('" + price.getId() + "', '" + price.getTitle() + "', '" + price.getPrice_set_id() + "', '" + price.getCurrency_code() + "', " +
                "'{\"value\": \"" + price.getAmount() + "\", \"precision\": 2}'::jsonb, 0, NOW(), NOW(), NULL, NULL, " + price.getAmount() + ", NULL, NULL)" +
                "ON CONFLICT (id) DO UPDATE SET " +
                "title = EXCLUDED.title, " +
                "price_set_id = EXCLUDED.price_set_id, " +
                "currency_code = EXCLUDED.currency_code, " +
                "raw_amount = EXCLUDED.raw_amount, " +
                "amount = EXCLUDED.amount, " +
                "updated_at = NOW();";
        entityManager.createNativeQuery(sql).executeUpdate();
    }

    @Transactional
    public void updateAmount(Price price) {
        String sql = "UPDATE PRICE SET " +
                "raw_amount = '{\"value\": \"" + price.getAmount() + "\", \"precision\": 2}'::jsonb, " +
                "amount = " + price.getAmount() + ", " +
                "updated_at = NOW() " +
                "WHERE id = '" + price.getId() + "';";
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
