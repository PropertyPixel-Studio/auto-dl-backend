package cz.pps.auto_dl_be.dto.detail;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JacksonXmlRootElement(localName = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
public class Detail {

    @JacksonXmlProperty(localName = "Body")
    private SoapBodyDetail body;
}
