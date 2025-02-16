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
        this.id = "iitem_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.sku = article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.title = article.getMfrName() + " " + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.description = getDescription(article);
    }

    private static String getDescription(Article article) {
        if (article.getGenericArticles() != null && !article.getGenericArticles().isEmpty()) {
            var genericArticle = article.getGenericArticles().getFirst();
            if (genericArticle != null) {
                return genericArticle.getAssemblyGroupName() + " - " + genericArticle.getGenericArticleDescription();
            }
        }
        return null;
    }
}
