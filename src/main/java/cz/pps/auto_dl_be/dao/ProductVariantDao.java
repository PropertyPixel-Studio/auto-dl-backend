package cz.pps.auto_dl_be.dao;

import cz.pps.auto_dl_be.model.medusa.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantDao extends JpaRepository<ProductVariant, String> {
}
