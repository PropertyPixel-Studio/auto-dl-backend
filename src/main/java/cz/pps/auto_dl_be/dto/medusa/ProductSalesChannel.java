package cz.pps.auto_dl_be.dto.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class ProductSalesChannel {
    private String id;
    private String product_id;
    private String sales_channel_id;

    public ProductSalesChannel(Article article) {
        this.id = "prodsc_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.product_id = "prod_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.sales_channel_id = "sc_01JKBX85GG5BV0533D14VDY1RB";
    }
}
