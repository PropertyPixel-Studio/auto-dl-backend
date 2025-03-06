package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriteriaValueCounts {
    @JacksonXmlProperty(localName = "rawValue")
    private String rawValue;

    @JacksonXmlProperty(localName = "formattedValue")
    private String formattedValue;

    @JacksonXmlProperty(localName = "permittedKeyValue")
    private Boolean permittedKeyValue;

    @JacksonXmlProperty(localName = "count")
    private Long count;

}
