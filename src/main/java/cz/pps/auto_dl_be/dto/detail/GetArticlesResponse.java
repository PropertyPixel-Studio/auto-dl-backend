package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class GetArticlesResponse {

    @JacksonXmlProperty(localName = "totalMatchingArticles")
    private Integer totalMatchingArticles;

    @JacksonXmlProperty(localName = "maxAllowedPage")
    private Integer maxAllowedPage;

    @JacksonXmlProperty(localName = "articles")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Article> articles;

    @JacksonXmlProperty(localName = "dataSupplierFacets")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DataSupplierFacetCounts> dataSupplierFacets;

    @JacksonXmlProperty(localName = "genericArticleFacets")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<GenericArticleFacetCounts> genericArticleFacets;

    @JacksonXmlProperty(localName = "criteriaFacets")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CriteriaFacetCounts> criteriaFacets;

    @JacksonXmlProperty(localName = "status")
    private Integer status;

    @JacksonXmlProperty(localName = "statusText")
    private String statusText;

    @JacksonXmlProperty(localName = "assemblyGroupFacets")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<AssemblyGroupFacetCounts> assemblyGroupFacets;

    @JacksonXmlProperty(localName = "articleStatusFacets")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ArticleStatusFacetCounts> articleStatusFacets;
}

