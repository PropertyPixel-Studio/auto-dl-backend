package cz.pps.auto_dl_be.dto.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class PriceSet {
    private String id;
    private String created_at;
    private String updated_at;

    public PriceSet(Article article) {
        this.id = "pset_" + article.getArticleNumber().replaceAll("[^a-zA-Z0-9-_]", "");
        this.created_at = LocalDateTime.now().toString();
        this.updated_at = LocalDateTime.now().toString();
    }
}
