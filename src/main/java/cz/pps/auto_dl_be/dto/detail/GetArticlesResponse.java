package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetArticlesResponse {

    @JacksonXmlProperty(localName = "totalMatchingArticles")
    private int totalMatchingArticles;

    @JacksonXmlProperty(localName = "maxAllowedPage")
    private int maxAllowedPage;

    @JacksonXmlProperty(localName = "articles")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Article> articles;

    @JacksonXmlProperty(localName = "status")
    private int status;
}
