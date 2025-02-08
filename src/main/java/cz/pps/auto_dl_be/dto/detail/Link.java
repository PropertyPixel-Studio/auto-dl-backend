package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Link {

    @JacksonXmlProperty(localName = "url")
    private String url;

    @JacksonXmlProperty(localName = "typeDescription")
    private String typeDescription;

    @JacksonXmlProperty(localName = "headerDescription")
    private String headerDescription;

    @JacksonXmlProperty(localName = "sortNumber")
    private int sortNumber;

    @JacksonXmlProperty(localName = "assetSource")
    private String assetSource;
}
