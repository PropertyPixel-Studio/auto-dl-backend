package cz.pps.auto_dl_be.dto.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProductSalesChannel {
    private String id;
    private String product_id;
    private String sales_channel_id;

    public ProductSalesChannel(Article article) {
        this.id = "prodsc_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.product_id = "prod_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.sales_channel_id = "sc_01JRD5A7QZJ5NPJ1263F8TA2DX";
    }
}
