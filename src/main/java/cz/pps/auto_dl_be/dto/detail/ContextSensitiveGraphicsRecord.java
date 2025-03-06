package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContextSensitiveGraphicsRecord {
    @JacksonXmlProperty(localName = "articleNumber")
    private String articleNumber;

    @JacksonXmlProperty(localName = "genericArticleId")
    private Integer genericArticleId;

    @JacksonXmlProperty(localName = "genericArticleDescription")
    private String genericArticleDescription;

    @JacksonXmlProperty(localName = "shape")
    private String shape;

    @JacksonXmlProperty(localName = "coords")
    private String coords;

    @JacksonXmlProperty(localName = "x1")
    private Integer x1;

    @JacksonXmlProperty(localName = "y1")
    private Integer y1;

    @JacksonXmlProperty(localName = "x2")
    private Integer x2;

    @JacksonXmlProperty(localName = "y2")
    private Integer y2;

}
