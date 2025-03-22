package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dto.medusa.PriceSet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PriceSetService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveWithQuery(PriceSet priceSet) {
        String sql = "INSERT INTO PRICE_SET (id, created_at, updated_at, deleted_at) " +
                "VALUES (:id, NOW(), NOW(), NULL)";
        entityManager.createNativeQuery(sql)
                .setParameter("id", priceSet.getId())
                .executeUpdate();
        entityManager.flush();
    }
}
