package cz.pps.auto_dl_be.dto.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
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

    public Price(String amount, String tecDocId) {
        this.id = "price_" + tecDocId.replaceAll("[^a-zA-Z0-9-_]", "");
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
