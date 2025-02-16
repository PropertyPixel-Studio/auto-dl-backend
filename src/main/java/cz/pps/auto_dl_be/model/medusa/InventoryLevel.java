package cz.pps.auto_dl_be.model.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public class InventoryLevel {
    private String id;
    private String inventory_item_id;
    private String location_id;
    private String stocked_quantity;
    private String raw_stocked_quantity;

    public InventoryLevel(Article article, String stocked_quantity) {
        this.id = "ilev_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.inventory_item_id = "iitem_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.location_id = "sloc_01JKBYKSP3NSJJCJG7FKKKPNS8";
        this.stocked_quantity = stocked_quantity;
        this.raw_stocked_quantity = stocked_quantity;
    }
}
