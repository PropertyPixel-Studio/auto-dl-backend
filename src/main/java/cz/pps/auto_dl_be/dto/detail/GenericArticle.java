package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GenericArticle {

    @JacksonXmlProperty(localName = "genericArticleId")
    private int genericArticleId;

    @JacksonXmlProperty(localName = "genericArticleDescription")
    private String genericArticleDescription;

    @JacksonXmlProperty(localName = "assemblyGroupNodeId")
    private int assemblyGroupNodeId;

    @JacksonXmlProperty(localName = "assemblyGroupName")
    private String assemblyGroupName;

    @JacksonXmlProperty(localName = "legacyArticleId")
    private String legacyArticleId;

    @JacksonXmlProperty(localName = "linkageTargetTypes")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<String> linkageTargetTypes;
}
