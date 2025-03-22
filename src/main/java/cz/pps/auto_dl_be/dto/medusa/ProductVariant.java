package cz.pps.auto_dl_be.dto.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProductVariant {
    private String id;
    private String title;
    private String sku;
    private String barcode;
    private String ean;
    private String manage_inventory;
    private String product_id;

    public ProductVariant(Article article) {
        this.id = "variant_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.title = article.getMfrName() + " " + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.sku = article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.barcode = (article.getGtins() != null && !article.getGtins().isEmpty()) ? article.getGtins().getFirst() : null;
        this.ean = (article.getGtins() != null && !article.getGtins().isEmpty()) ? article.getGtins().getFirst() : null;
        this.manage_inventory = "true";
        this.product_id = "prod_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
    }
}
