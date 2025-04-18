package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PartsListRecord {
    @JacksonXmlProperty(localName = "articleNumber")
    private String articleNumber;

    @JacksonXmlProperty(localName = "genericArticleId")
    private Integer genericArticleId;

    @JacksonXmlProperty(localName = "genericArticleDescription")
    private String genericArticleDescription;

    @JacksonXmlProperty(localName = "quantity")
    private Integer quantity;

    @JacksonXmlProperty(localName = "criteria")
    private CriteriaRecord criteria;

}
