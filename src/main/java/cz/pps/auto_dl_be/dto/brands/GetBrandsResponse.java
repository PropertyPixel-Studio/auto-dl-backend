package cz.pps.auto_dl_be.dto.brands;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetBrandsResponse {
    @JacksonXmlProperty(localName = "status")
    private int status;

    @JacksonXmlProperty(localName = "data")
    private BrandData data;
}
