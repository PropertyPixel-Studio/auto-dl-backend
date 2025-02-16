package cz.pps.auto_dl_be.model.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_variant", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class ProductVariant {
    @Id
    @Column(name = "id", nullable = true)
    private String id;
    @Column(name = "title", nullable = true)
    private String title;
    @Column(name = "sku", nullable = true)
    private String sku;
    @Column(name = "barcode", nullable = true)
    private String barcode;
    @Column(name = "ean", nullable = true)
    private String ean;
    @Column(name = "manage_inventory", nullable = true)
    private String manage_inventory;
    @Column(name = "product_id", nullable = true)
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
