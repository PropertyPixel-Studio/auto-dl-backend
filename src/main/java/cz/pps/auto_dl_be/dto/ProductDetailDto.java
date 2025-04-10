package cz.pps.auto_dl_be.dto;

import cz.pps.auto_dl_be.model.InventoryLevelEntity;
import cz.pps.auto_dl_be.model.PriceEntity;
import cz.pps.auto_dl_be.model.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDto {
    private ProductEntity product;
    private InventoryLevelEntity inventory;
    private PriceEntity price;
}