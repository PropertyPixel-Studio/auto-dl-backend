package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class SearchQueryMatch {
    @JacksonXmlProperty(localName = "matchType")
    private String matchType;

    @JacksonXmlProperty(localName = "description")
    private String description;

    @JacksonXmlProperty(localName = "match")
    private String match;

    @JacksonXmlProperty(localName = "mfrId")
    private Long mfrId;

    @JacksonXmlProperty(localName = "mfrName")
    private String mfrName;

}
