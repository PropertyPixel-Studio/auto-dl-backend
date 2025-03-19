package cz.pps.auto_dl_be.dto.medusa;

import cz.pps.auto_dl_be.dto.detail.Article;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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
