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
@Table(name = "inventory_item", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class InventoryItem {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "sku", nullable = true)
    private String sku;
    @Column(name = "title", nullable = true)
    private String title;
    @Column(name = "description", nullable = true)
    private String description;

    public InventoryItem(Article article) {
        this.id = "iitem_" + article.getArticleNumber();
        this.sku = article.getArticleNumber();
        this.title = article.getMfrName() + article.getArticleNumber();
        this.description = getDescription(article);
    }

    private static String getDescription(Article article) {
        return article.getGenericArticles().getFirst().getAssemblyGroupName() +
                "-" + article.getGenericArticles().getFirst().getGenericArticleDescription();
    }
}
