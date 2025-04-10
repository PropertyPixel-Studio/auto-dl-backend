package cz.pps.auto_dl_be.dao;

import cz.pps.auto_dl_be.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Repository
public interface ProductDao extends JpaRepository<ProductEntity, String> {
    @Query("SELECT p FROM ProductEntity p")
    Stream<ProductEntity> findAllAsStream();

    @Async
    CompletableFuture<List<ProductEntity>> findByIdIn(List<String> productIds);
}