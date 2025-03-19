package cz.pps.auto_dl_be.dto.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
public class Price {
    private String id;
    private String title;
    private String price_set_id;
    private String currency_code;
    private String raw_amount;
    private String amount;

    public Price(Article article, String amount) {
        this.id = "price_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.title = article.getMfrName() + " " + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.price_set_id = "pset_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.currency_code = "czk";
        this.raw_amount = amount;
        this.amount = amount;
    }

    public Price(String amount) {
        this.raw_amount = amount;
        this.amount = amount;
    }

    public Price(Article article) {
        this.id = "price_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.title = article.getMfrName() + " " + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.price_set_id = "pset_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.currency_code = "czk";
    }
}
