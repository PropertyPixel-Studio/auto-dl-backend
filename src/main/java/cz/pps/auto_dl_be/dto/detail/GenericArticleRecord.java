package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericArticleRecord {
    @JacksonXmlProperty(localName = "genericArticleId")
    private Integer genericArticleId;

    @JacksonXmlProperty(localName = "genericArticleDescription")
    private String genericArticleDescription;

    @JacksonXmlProperty(localName = "assemblyGroupNodeId")
    private Long assemblyGroupNodeId;

    @JacksonXmlProperty(localName = "assemblyGroupName")
    private String assemblyGroupName;

    @JacksonXmlProperty(localName = "legacyArticleId")
    private Integer legacyArticleId;

    @JacksonXmlProperty(localName = "linkageTargetTypes")
    private String linkageTargetTypes;

}
