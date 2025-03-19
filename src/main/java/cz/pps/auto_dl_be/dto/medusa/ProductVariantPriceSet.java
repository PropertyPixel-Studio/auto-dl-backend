package cz.pps.auto_dl_be.dto.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
public class ProductVariantPriceSet {
    private String variant_id;
    private String price_set_id;
    private String id;

    public ProductVariantPriceSet(Article article) {
        this.id = "prodsc_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.variant_id = "variant_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.price_set_id = "pset_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
    }
}