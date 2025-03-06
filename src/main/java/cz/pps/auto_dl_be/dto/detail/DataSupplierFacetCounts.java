package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSupplierFacetCounts {
    @JacksonXmlProperty(localName = "total")
    private Integer total;

    @JacksonXmlProperty(localName = "counts")
    private DataSupplierFacetCount counts;

}
