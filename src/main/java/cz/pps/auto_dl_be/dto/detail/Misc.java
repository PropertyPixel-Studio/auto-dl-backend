package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Misc {

    @JacksonXmlProperty(localName = "additionalDescriptionId")
    private int additionalDescriptionId;

    @JacksonXmlProperty(localName = "additionalDescription")
    private String additionalDescription;

    @JacksonXmlProperty(localName = "articleStatusId")
    private int articleStatusId;

    @JacksonXmlProperty(localName = "articleStatusDescription")
    private String articleStatusDescription;

    @JacksonXmlProperty(localName = "articleStatusValidFromDate")
    private String articleStatusValidFromDate;

    @JacksonXmlProperty(localName = "quantityPerPackage")
    private int quantityPerPackage;

    @JacksonXmlProperty(localName = "quantityPerPartPerPackage")
    private int quantityPerPartPerPackage;

    @JacksonXmlProperty(localName = "isSelfServicePacking")
    private boolean isSelfServicePacking;

    @JacksonXmlProperty(localName = "hasMandatoryMaterialCertification")
    private boolean hasMandatoryMaterialCertification;

    @JacksonXmlProperty(localName = "isRemanufacturedPart")
    private boolean isRemanufacturedPart;

    @JacksonXmlProperty(localName = "isAccessory")
    private boolean isAccessory;
}
