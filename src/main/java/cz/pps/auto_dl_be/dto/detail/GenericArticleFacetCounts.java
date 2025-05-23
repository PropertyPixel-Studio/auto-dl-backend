package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class GenericArticleFacetCounts {
    @JacksonXmlProperty(localName = "total")
    private Integer total;

    @JacksonXmlProperty(localName = "counts")
    private GenericArticleFacetCount counts;

}
