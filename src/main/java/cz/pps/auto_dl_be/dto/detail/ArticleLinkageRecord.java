package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleLinkageRecord {
    @JacksonXmlProperty(localName = "linkageTargetTypeId")
    private Integer linkageTargetTypeId;

    @JacksonXmlProperty(localName = "linkageTargetId")
    private Integer linkageTargetId;

    @JacksonXmlProperty(localName = "legacyArticleLinkId")
    private Integer legacyArticleLinkId;

    @JacksonXmlProperty(localName = "genericArticleId")
    private Integer genericArticleId;

    @JacksonXmlProperty(localName = "genericArticleDescription")
    private String genericArticleDescription;

    @JacksonXmlProperty(localName = "linkageCriteria")
    private CriteriaRecord linkageCriteria;

    @JacksonXmlProperty(localName = "linkageText")
    private LinkageTextRecord linkageText;

}
