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
@Table(name = "price", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Price {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "title", nullable = true)
    private String title;
    @Column(name = "price_set_id", nullable = true)
    private String price_set_id;
    @Column(name = "currency_code", nullable = true)
    private String currency_code;
    @Column(name = "raw_amount", nullable = true)
    private String raw_amount;
    @Column(name = "amount", nullable = true)
    private String amount;

    public Price(Article article, String raw_amount, String amount) {
        this.id = "price_" + article.getArticleNumber();
        this.title = article.getMfrName() + article.getArticleNumber();
        this.price_set_id = "pset_" + article.getArticleNumber();
        this.currency_code = "czk";
        this.raw_amount = raw_amount;
        this.amount = amount;
    }
}
