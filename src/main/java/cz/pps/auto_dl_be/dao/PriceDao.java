package cz.pps.auto_dl_be.dao;

import cz.pps.auto_dl_be.model.medusa.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceDao extends JpaRepository<Price, String> {

}