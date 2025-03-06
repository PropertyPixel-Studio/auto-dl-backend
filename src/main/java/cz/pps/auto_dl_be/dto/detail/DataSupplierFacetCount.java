package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSupplierFacetCount {
    @JacksonXmlProperty(localName = "dataSupplierId")
    private Integer dataSupplierId;

    @JacksonXmlProperty(localName = "mfrId")
    private Integer mfrId;

    @JacksonXmlProperty(localName = "mfrName")
    private String mfrName;

    @JacksonXmlProperty(localName = "count")
    private Long count;

}
