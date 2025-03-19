package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class MiscArticleDataRecord {
    @JacksonXmlProperty(localName = "additionalDescriptionId")
    private Integer additionalDescriptionId;

    @JacksonXmlProperty(localName = "additionalDescription")
    private String additionalDescription;

    @JacksonXmlProperty(localName = "articleStatusId")
    private Integer articleStatusId;

    @JacksonXmlProperty(localName = "articleStatusDescription")
    private String articleStatusDescription;

    @JacksonXmlProperty(localName = "articleStatusValidFromDate")
    private Integer articleStatusValidFromDate;

    @JacksonXmlProperty(localName = "quantityPerPackage")
    private Integer quantityPerPackage;

    @JacksonXmlProperty(localName = "quantityPerPartPerPackage")
    private Integer quantityPerPartPerPackage;

    @JacksonXmlProperty(localName = "isSelfServicePacking")
    private Boolean isSelfServicePacking;

    @JacksonXmlProperty(localName = "hasMandatoryMaterialCertification")
    private Boolean hasMandatoryMaterialCertification;

    @JacksonXmlProperty(localName = "isRemanufacturedPart")
    private Boolean isRemanufacturedPart;

    @JacksonXmlProperty(localName = "isAccessory")
    private Boolean isAccessory;

    @JacksonXmlProperty(localName = "batchSize1")
    private Integer batchSize1;

    @JacksonXmlProperty(localName = "batchSize2")
    private Integer batchSize2;

}
