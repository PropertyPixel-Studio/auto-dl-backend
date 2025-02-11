package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeNumberDetailsRecord {
    @JacksonXmlProperty(localName = "tradeNumber")
    private String tradeNumber;

    @JacksonXmlProperty(localName = "isImmediateDisplay")
    private Boolean isImmediateDisplay;

}
