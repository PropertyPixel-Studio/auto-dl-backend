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
@Table(name = "inventory_level", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class InventoryLevel {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "inventory_item_id", nullable = true)
    private String inventory_item_id;
    @Column(name = "location_id", nullable = true)
    private String location_id;
    @Column(name = "stocked_quantity", nullable = true)
    private String stocked_quantity;
    @Column(name = "raw_stocked_quantity", nullable = true)
    private String raw_stocked_quantity = null;

    public InventoryLevel(Article article, String stocked_quantity) {
        this.id = "ilev_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.inventory_item_id = "iitem_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.location_id = "sloc_01JKBYKSP3NSJJCJG7FKKKPNS8";
        this.stocked_quantity = stocked_quantity;
    }
}
