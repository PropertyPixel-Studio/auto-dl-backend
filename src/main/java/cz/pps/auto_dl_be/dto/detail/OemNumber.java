package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OemNumber {

    @JacksonXmlProperty(localName = "articleNumber")
    private String articleNumber;

    @JacksonXmlProperty(localName = "mfrId")
    private int mfrId;

    @JacksonXmlProperty(localName = "mfrName")
    private String mfrName;

    @JacksonXmlProperty(localName = "matchesSearchQuery")
    private boolean matchesSearchQuery;
}
