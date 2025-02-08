package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleCriteria {

    @JacksonXmlProperty(localName = "criteriaId")
    private int criteriaId;

    @JacksonXmlProperty(localName = "criteriaDescription")
    private String criteriaDescription;

    @JacksonXmlProperty(localName = "criteriaAbbrDescription")
    private String criteriaAbbrDescription;

    @JacksonXmlProperty(localName = "criteriaType")
    private String criteriaType;

    @JacksonXmlProperty(localName = "keyTableType")
    private String keyTableType;

    @JacksonXmlProperty(localName = "keyTableNum")
    private int keyTableNum;

    @JacksonXmlProperty(localName = "rawValue")
    private String rawValue;

    @JacksonXmlProperty(localName = "formattedValue")
    private String formattedValue;

    @JacksonXmlProperty(localName = "immediateDisplay")
    private boolean immediateDisplay;

    @JacksonXmlProperty(localName = "isMandatory")
    private boolean isMandatory;

    @JacksonXmlProperty(localName = "isInterval")
    private boolean isInterval;
}
