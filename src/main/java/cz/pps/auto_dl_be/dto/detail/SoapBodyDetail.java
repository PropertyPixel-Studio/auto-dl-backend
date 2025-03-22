package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class SoapBodyDetail {

    @JacksonXmlProperty(localName = "getArticlesResponse", namespace = "http://server.cat.tecdoc.net")
    private GetArticlesResponse getArticlesResponse;
}
