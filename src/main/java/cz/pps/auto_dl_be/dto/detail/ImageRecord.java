package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ImageRecord {
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

    @JacksonXmlProperty(localName = "imageURL50")
    private String imageURL50;

    @JacksonXmlProperty(localName = "imageURL100")
    private String imageURL100;

    @JacksonXmlProperty(localName = "imageURL200")
    private String imageURL200;

    @JacksonXmlProperty(localName = "imageURL400")
    private String imageURL400;

    @JacksonXmlProperty(localName = "imageURL800")
    private String imageURL800;

    @JacksonXmlProperty(localName = "imageURL1600")
    private String imageURL1600;

    @JacksonXmlProperty(localName = "imageURL3200")
    private String imageURL3200;

    @JacksonXmlProperty(localName = "frame")
    private Integer frame;

    @JacksonXmlProperty(localName = "totalFrames")
    private Integer totalFrames;

    @JacksonXmlProperty(localName = "contextSensitiveGraphics")
    private ContextSensitiveGraphicsRecord contextSensitiveGraphics;

}
