package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CriteriaRecord {
    @JacksonXmlProperty(localName = "criteriaId")
    private Integer criteriaId;

    @JacksonXmlProperty(localName = "criteriaDescription")
    private String criteriaDescription;

    @JacksonXmlProperty(localName = "criteriaAbbrDescription")
    private String criteriaAbbrDescription;

    @JacksonXmlProperty(localName = "criteriaUnitDescription")
    private String criteriaUnitDescription;

    @JacksonXmlProperty(localName = "criteriaType")
    private String criteriaType;

    @JacksonXmlProperty(localName = "keyTableType")
    private String keyTableType;

    @JacksonXmlProperty(localName = "keyTableNum")
    private Integer keyTableNum;

    @JacksonXmlProperty(localName = "successorCriteriaId")
    private Integer successorCriteriaId;

    @JacksonXmlProperty(localName = "rawValue")
    private String rawValue;

    @JacksonXmlProperty(localName = "formattedValue")
    private String formattedValue;

    @JacksonXmlProperty(localName = "immediateDisplay")
    private Boolean immediateDisplay;

    @JacksonXmlProperty(localName = "isMandatory")
    private Boolean isMandatory;

    @JacksonXmlProperty(localName = "isInterval")
    private Boolean isInterval;

    @JacksonXmlProperty(localName = "matchesSearchQuery")
    private Boolean matchesSearchQuery;

}
