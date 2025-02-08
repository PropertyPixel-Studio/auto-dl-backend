package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Article {

    @JacksonXmlProperty(localName = "dataSupplierId")
    private int dataSupplierId;

    @JacksonXmlProperty(localName = "articleNumber")
    private String articleNumber;

    @JacksonXmlProperty(localName = "mfrId")
    private int mfrId;

    @JacksonXmlProperty(localName = "mfrName")
    private String mfrName;

    @JacksonXmlProperty(localName = "misc")
    private Misc misc;

    @JacksonXmlProperty(localName = "genericArticles")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<GenericArticle> genericArticles;

    @JacksonXmlProperty(localName = "gtins")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<String> gtins;

    @JacksonXmlProperty(localName = "oemNumbers")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<OemNumber> oemNumbers;

    @JacksonXmlProperty(localName = "articleCriteria")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ArticleCriteria> articleCriteria;

    @JacksonXmlProperty(localName = "hasPartsListParent")
    private boolean hasPartsListParent;

    @JacksonXmlProperty(localName = "hasAccessoryListParent")
    private boolean hasAccessoryListParent;

    @JacksonXmlProperty(localName = "images")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Image> images;

    @JacksonXmlProperty(localName = "searchQueryMatches")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SearchQueryMatch> searchQueryMatches;

    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Link> links;

    @JacksonXmlProperty(localName = "totalLinkages")
    private int totalLinkages;
}
