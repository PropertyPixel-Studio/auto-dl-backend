package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkRecord {
    @JacksonXmlProperty(localName = "fileName")
    private String fileName;

    @JacksonXmlProperty(localName = "typeDescription")
    private String typeDescription;

    @JacksonXmlProperty(localName = "typeKey")
    private Integer typeKey;

    @JacksonXmlProperty(localName = "headerDescription")
    private String headerDescription;

    @JacksonXmlProperty(localName = "headerKey")
    private Integer headerKey;

    @JacksonXmlProperty(localName = "sortNumber")
    private Integer sortNumber;

    @JacksonXmlProperty(localName = "assetSource")
    private String assetSource;

    @JacksonXmlProperty(localName = "url")
    private String url;

    @JacksonXmlProperty(localName = "description")
    private String description;

}
