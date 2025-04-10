package cz.pps.auto_dl_be.dao;

import cz.pps.auto_dl_be.model.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public interface PriceDao extends JpaRepository<PriceEntity, String> {
    @Async
    CompletableFuture<List<PriceEntity>> findByPriceListIdIn(List<String> productIds);
}