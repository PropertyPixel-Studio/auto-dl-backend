package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleRefRecord {
    @JacksonXmlProperty(localName = "articleNumber")
    private String articleNumber;

    @JacksonXmlProperty(localName = "dataSupplierId")
    private Integer dataSupplierId;

    @JacksonXmlProperty(localName = "mfrId")
    private Integer mfrId;

    @JacksonXmlProperty(localName = "mfrName")
    private String mfrName;

    @JacksonXmlProperty(localName = "matchesSearchQuery")
    private Boolean matchesSearchQuery;

    @JacksonXmlProperty(localName = "referenceTypeKey")
    private String referenceTypeKey;

    @JacksonXmlProperty(localName = "referenceTypeDescription")
    private String referenceTypeDescription;

    @Override
    public String toString() {
        return "{" +
                "\"articleNumber\": \"" + articleNumber + "\"," +
                "\"dataSupplierId\": " + dataSupplierId + "," +
                "\"mfrId\": " + mfrId + "," +
                "\"mfrName\": \"" + mfrName + "\"," +
                "\"matchesSearchQuery\": " + matchesSearchQuery + "," +
                "\"referenceTypeKey\": \"" + referenceTypeKey + "\"," +
                "\"referenceTypeDescription\": \"" + referenceTypeDescription + "\"" +
                "}";
    }
}
