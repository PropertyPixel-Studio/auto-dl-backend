package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssemblyGroupFacetCount {
    @JacksonXmlProperty(localName = "assemblyGroupNodeId")
    private Long assemblyGroupNodeId;

    @JacksonXmlProperty(localName = "assemblyGroupName")
    private String assemblyGroupName;

    @JacksonXmlProperty(localName = "assemblyGroupType")
    private String assemblyGroupType;

    @JacksonXmlProperty(localName = "parentNodeId")
    private Long parentNodeId;

    @JacksonXmlProperty(localName = "children")
    private Integer children;

    @JacksonXmlProperty(localName = "count")
    private Long count;

}
