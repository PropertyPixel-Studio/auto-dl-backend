package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ArticleStatusFacetCount {
    @JacksonXmlProperty(localName = "articleStatusId")
    private Integer articleStatusId;

    @JacksonXmlProperty(localName = "articleStatusDescription")
    private String articleStatusDescription;

    @JacksonXmlProperty(localName = "count")
    private Long count;

}
