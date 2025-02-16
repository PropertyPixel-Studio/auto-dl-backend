package cz.pps.auto_dl_be.model.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.Normalizer;

@Entity
@Table(name = "product", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "title", nullable = true)
    private String title;
    @Column(name = "handle", nullable = true)
    private String handle;
    @Column(name = "thumbnail", nullable = true)
    private String thumbnail;
    @Column(name = "external_id", nullable = true)
    private String externalId;
    @Column(name = "subtitle", nullable = true)
    private String subtitle;
    @Column(name = "description", nullable = true)
    private String description;
    @Column(name = "metadata", nullable = true)
    private String supplier_id;

    public Product(Article article, String supplier_id) {
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
        this.supplier_id = supplier_id;
    }

    private static String getTitle(Article article) {
        return (article.getGenericArticles() != null && !article.getGenericArticles().isEmpty()) ?
                article.getGenericArticles().getFirst().getGenericArticleDescription() + " " + article.getMfrName() + " " + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "") :
                article.getMfrName() + " " + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
    }
}