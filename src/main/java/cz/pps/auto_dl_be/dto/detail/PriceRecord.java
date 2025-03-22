package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PriceRecord {
    @JacksonXmlProperty(localName = "currencyCode")
    private String currencyCode;

    @JacksonXmlProperty(localName = "discountGroup")
    private String discountGroup;

    @JacksonXmlProperty(localName = "isDiscount")
    private Boolean isDiscount;

    @JacksonXmlProperty(localName = "price")
    private Double price;

    @JacksonXmlProperty(localName = "priceCents")
    private Integer priceCents;

    @JacksonXmlProperty(localName = "kindOfPriceKey")
    private Integer kindOfPriceKey;

    @JacksonXmlProperty(localName = "kindOfPriceDescription")
    private String kindOfPriceDescription;

    @JacksonXmlProperty(localName = "priceUnitKey")
    private Integer priceUnitKey;

    @JacksonXmlProperty(localName = "priceUnitDescription")
    private String priceUnitDescription;

    @JacksonXmlProperty(localName = "quantityUnitKey")
    private String quantityUnitKey;

    @JacksonXmlProperty(localName = "quantityUnitDescription")
    private String quantityUnitDescription;

    @JacksonXmlProperty(localName = "validDateFrom")
    private Integer validDateFrom;

    @JacksonXmlProperty(localName = "validDateTo")
    private Integer validDateTo;

}
