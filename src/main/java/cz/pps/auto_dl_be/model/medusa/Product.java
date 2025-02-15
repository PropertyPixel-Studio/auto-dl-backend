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

    public Product(Article article) {
        this.id = "prod_" + article.getArticleNumber();
        this.title = getTitle(article);
        this.handle = this.title.toLowerCase().replace(" ", "-").replace(",", "");
        this.thumbnail = article.getImages().getFirst().getImageURL400();
        this.externalId = article.getGenericArticles().getFirst().getLegacyArticleId().toString();
        this.subtitle = article.getGenericArticles().getFirst().getAssemblyGroupName();
        this.description = article.getGenericArticles().getFirst().getGenericArticleDescription();
    }

    private static String getTitle(Article article) {
        return article.getGenericArticles().getFirst().getGenericArticleDescription() +
                " " + article.getMfrName() +
                " " + article.getArticleNumber();
    }
}