package cz.pps.auto_dl_be.dto.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryItem {
    private String id;
    private String sku;
    private String title;
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
