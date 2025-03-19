package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class LinkageTextRecord {
    @JacksonXmlProperty(localName = "informationTypeKey")
    private Integer informationTypeKey;

    @JacksonXmlProperty(localName = "informationTypeDescription")
    private String informationTypeDescription;

    @JacksonXmlProperty(localName = "isImmediateDisplay")
    private Boolean isImmediateDisplay;

    @JacksonXmlProperty(localName = "text")
    private String text;

}
