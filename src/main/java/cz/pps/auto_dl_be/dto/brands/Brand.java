package cz.pps.auto_dl_be.dto.brands;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Brand {
    @JacksonXmlProperty(localName = "dataSupplierId")
    private int dataSupplierId;

    @JacksonXmlProperty(localName = "mfrName")
    private String mfrName;
}
