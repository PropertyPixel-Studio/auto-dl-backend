package cz.pps.auto_dl_be.model.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "price_set", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class PriceSet {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "created_at", nullable = true)
    private String created_at;
    @Column(name = "updated_at", nullable = true)
    private String updated_at;

    public PriceSet(Article article) {
        this.id = "pset_" + article.getArticleNumber();
        this.created_at = LocalDateTime.now().toString();
        this.updated_at = LocalDateTime.now().toString();
    }
}
