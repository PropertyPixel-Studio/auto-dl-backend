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
@Table(name = "product_sales_channel", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class ProductSalesChannel {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "product_id", nullable = true)
    private String product_id;
    @Column(name = "sales_channel_id", nullable = true)
    private String sales_channel_id;

    public ProductSalesChannel(Article article) {
        this.id = "prodsc_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.product_id = "prod_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.sales_channel_id = "sc_01JKBX85GG5BV0533D14VDY1RB";
    }
}
