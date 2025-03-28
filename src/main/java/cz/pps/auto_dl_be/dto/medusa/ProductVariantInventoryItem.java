package cz.pps.auto_dl_be.dto.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProductVariantInventoryItem {
    private String id;
    private String variant_id;
    private String inventory_item_id;

    public ProductVariantInventoryItem(Article article) {
        this.id = "pvitem_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.variant_id = "variant_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.inventory_item_id = "iitem_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
    }
}
