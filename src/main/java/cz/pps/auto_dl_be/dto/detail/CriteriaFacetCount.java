package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CriteriaFacetCount {
    @JacksonXmlProperty(localName = "criteria")
    private CriteriaInfo criteria;

    @JacksonXmlProperty(localName = "criteriaValueCounts")
    private CriteriaValueCounts criteriaValueCounts;

}
