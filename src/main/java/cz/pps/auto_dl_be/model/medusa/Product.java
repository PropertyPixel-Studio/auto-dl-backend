package cz.pps.auto_dl_be.model.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Component
@Getter
@Setter
@NoArgsConstructor
public class Product {
    private String id;
    private String title;
    private String handle;
    private String thumbnail;
    private String externalId;
    private String subtitle;
    private String description;
    private String supplierId;

    public Product(Article article, String supplierId) {
        this.id = "prod_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.title = getTitle(article);
        this.handle = this.title != null ? Normalizer.normalize(this.title, Normalizer.Form.NFD)
                .replace(" ", "-").replace(",", "")
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9-_]", "") : null;
        this.thumbnail = (article.getImages() != null && !article.getImages().isEmpty()) ? article.getImages().getFirst().getImageURL400() : null;
        this.externalId = (article.getGenericArticles() != null && !article.getGenericArticles().isEmpty()) ? article.getGenericArticles().getFirst().getLegacyArticleId().toString() : null;
        this.subtitle = (article.getGenericArticles() != null && !article.getGenericArticles().isEmpty()) ? article.getGenericArticles().getFirst().getAssemblyGroupName() : null;
        this.description = (article.getGenericArticles() != null && !article.getGenericArticles().isEmpty()) ? article.getGenericArticles().getFirst().getGenericArticleDescription() : null;
        this.supplierId = supplierId;
    }

    private static String getTitle(Article article) {
        return (article.getGenericArticles() != null && !article.getGenericArticles().isEmpty()) ?
                article.getGenericArticles().getFirst().getGenericArticleDescription() + " " + article.getMfrName() + " " + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "") :
                article.getMfrName() + " " + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
    }
}