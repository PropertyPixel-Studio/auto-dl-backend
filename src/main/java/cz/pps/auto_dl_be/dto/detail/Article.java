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
public class Article {
    @JacksonXmlProperty(localName = "dataSupplierId")
    private Long dataSupplierId;

    @JacksonXmlProperty(localName = "articleNumber")
    private String articleNumber;

    @JacksonXmlProperty(localName = "mfrId")
    private Long mfrId;

    @JacksonXmlProperty(localName = "mfrName")
    private String mfrName;

    @JacksonXmlProperty(localName = "misc")
    private MiscArticleDataRecord misc;

    @JacksonXmlProperty(localName = "genericArticles")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<GenericArticleRecord> genericArticles;

    @JacksonXmlProperty(localName = "articleText")
    private ArticleTextRecord articleText;

    @JacksonXmlProperty(localName = "gtins")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<String> gtins;

    @JacksonXmlProperty(localName = "tradeNumbers")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<String> tradeNumbers;

    @JacksonXmlProperty(localName = "tradeNumbersDetails")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<TradeNumberDetailsRecord> tradeNumbersDetails;

    @JacksonXmlProperty(localName = "oemNumbers")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ArticleRefRecord> oemNumbers;

    @JacksonXmlProperty(localName = "replacesArticles")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ArticleRefRecord> replacesArticles;

    @JacksonXmlProperty(localName = "replacedByArticles")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ArticleRefRecord> replacedByArticles;

    @JacksonXmlProperty(localName = "articleCriteria")
    private CriteriaRecord articleCriteria;

    @JacksonXmlProperty(localName = "linkages")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ArticleLinkageRecord> linkages;

    @JacksonXmlProperty(localName = "partsList")
    private PartsListRecord partsList;

    @JacksonXmlProperty(localName = "hasPartsListParent")
    private Boolean hasPartsListParent;

    @JacksonXmlProperty(localName = "accessoryList")
    private AccessoryListRecord accessoryList;

    @JacksonXmlProperty(localName = "hasAccessoryListParent")
    private Boolean hasAccessoryListParent;

    @JacksonXmlProperty(localName = "pdfs")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<PDFRecord> pdfs;

    @JacksonXmlProperty(localName = "images")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ImageRecord> images;

    @JacksonXmlProperty(localName = "comparableNumbers")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ArticleRefRecord> comparableNumbers;

    @JacksonXmlProperty(localName = "searchQueryMatches")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SearchQueryMatch> searchQueryMatches;

    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<LinkRecord> links;

    @JacksonXmlProperty(localName = "totalLinkages")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Integer> totalLinkages;

    @JacksonXmlProperty(localName = "prices")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<PriceRecord> prices;

    @JacksonXmlProperty(localName = "articleLogisticsCriteria")
    private CriteriaRecord articleLogisticsCriteria;

}
