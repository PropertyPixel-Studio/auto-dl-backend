package cz.pps.auto_dl_be.dto.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@Data
public class InventoryLevel {
    private String id;
    private String inventory_item_id;
    private String location_id;
    private Integer stocked_quantity;
    private Integer raw_stocked_quantity;

    public InventoryLevel(Article article, String stocked_quantity, String supplierStock, String otherBranchStock) {
        this.id = "ilev_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.inventory_item_id = "iitem_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.location_id = "sloc_01JVH56RTY0QFABTJC3MT7HR0V";
        this.stocked_quantity = parseQuantity(stocked_quantity, supplierStock, otherBranchStock);
        this.raw_stocked_quantity = this.stocked_quantity;
    }

    public InventoryLevel(String stocked_quantity, String supplierStock, String otherBranchStock, String tecDocId) {
        this.id = "ilev_" + tecDocId.replaceAll("[^a-zA-Z0-9-_]", "");
        this.stocked_quantity = parseQuantity(stocked_quantity, supplierStock, otherBranchStock);
        this.raw_stocked_quantity = this.stocked_quantity;
    }

    private Integer parseQuantity(String stocked_quantity, String supplierStock, String otherBranchStock) {
        if (Objects.equals(stocked_quantity, ">4") || Objects.equals(supplierStock, ">4") || Objects.equals(otherBranchStock, ">4")) {
            return 5;
        }
        int sum = Integer.parseInt(stocked_quantity) + Integer.parseInt(supplierStock) + Integer.parseInt(otherBranchStock);
        return Math.min(sum, 5);
    }
}
