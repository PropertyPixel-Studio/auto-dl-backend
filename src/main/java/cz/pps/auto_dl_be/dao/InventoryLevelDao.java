package cz.pps.auto_dl_be.dao;

import cz.pps.auto_dl_be.model.InventoryLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public interface InventoryLevelDao extends JpaRepository<InventoryLevelEntity, String> {
    @Async
    CompletableFuture<List<InventoryLevelEntity>> findByInventoryItemIdIn(List<String> productIds);
}