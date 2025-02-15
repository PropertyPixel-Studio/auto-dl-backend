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
@Table(name = "product_variant_inventory_item", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class ProductVariantInventoryItem {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "variant_id", nullable = true)
    private String variant_id;
    @Column(name = "inventory_item_id", nullable = true)
    private String inventory_item_id;

    public ProductVariantInventoryItem(Article article) {
        this.id = "pvitem_" + article.getArticleNumber();
        this.variant_id = "variant_" + article.getArticleNumber();
        this.inventory_item_id = "iitem_" + article.getArticleNumber();
    }
}
