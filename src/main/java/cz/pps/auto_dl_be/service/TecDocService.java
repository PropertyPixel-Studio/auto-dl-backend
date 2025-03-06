package cz.pps.auto_dl_be.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import cz.pps.auto_dl_be.dto.brands.*;
import cz.pps.auto_dl_be.dto.detail.Article;
import cz.pps.auto_dl_be.dto.detail.Detail;
import cz.pps.auto_dl_be.dto.detail.GetArticlesResponse;
import cz.pps.auto_dl_be.dto.detail.SoapBodyDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class TecDocService {
    @Value("${tecdoc.api.url}")
    private String tecDocUrl;
    @Value("${tecdoc.api.key}")
    private String tecDocKey;
    private final WebClient webClient;
    private final XmlMapper xmlMapper;

    public TecDocService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(tecDocUrl).build();
        this.xmlMapper = new XmlMapper(); // Jackson XML Mapper
    }

    private String getBrandsXml() {
        ClassPathResource resource = new ClassPathResource("getBrands.xml");
        try {
            return new String(Files.readAllBytes(Paths.get(resource.getURI())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Mono<List<Brand>> fetchBrands() {
        String xmlContent = getBrandsXml();
        return webClient.post()
                .uri(tecDocUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .header("X-API-KEY", tecDocKey)
                .bodyValue(xmlContent)
                .retrieve()
                .bodyToMono(String.class) // Get raw XML response
                .map(this::convertToSoapEnvelope) // Convert XML to Java object
                .map(this::extractBrands) // Extract brands from response
                .onErrorResume(error -> {
                    System.err.println("Error occurred: " + error.getMessage());
                    return Mono.just(List.of()); // Return empty list on failure
                });
    }

    private SoapEnvelope convertToSoapEnvelope(String xml) {
        try {
            return xmlMapper.readValue(xml, SoapEnvelope.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing XML response", e);
        }
    }

    private List<Brand> extractBrands(SoapEnvelope envelope) {
        return Optional.ofNullable(envelope)
                .map(SoapEnvelope::getBody)
                .map(SoapBodyBrands::getGetBrandsResponse)
                .map(GetBrandsResponse::getData)
                .map(BrandData::getBrands)
                .orElse(List.of()); // Return empty list if any value is null
    }

    private String getDetailXmlTemplate(String searchQuery, String dataSupplierIds) {
        ClassPathResource resource = new ClassPathResource("getDetail.xml");
        String template;
        try {
            template = new String(Files.readAllBytes(Paths.get(resource.getURI())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        template = template.replace("${searchQuery}", searchQuery)
                .replace("${dataSupplierIds}", dataSupplierIds);
        return template;
    }

    public Mono<List<Article>> fetchArticles(String searchQuery, String dataSupplierIds) {
        String xmlContent = getDetailXmlTemplate(searchQuery, dataSupplierIds);
        return webClient.post()
                .uri(tecDocUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .header("X-API-KEY", tecDocKey)
                .bodyValue(xmlContent)
                .retrieve()
                .bodyToMono(String.class) // Get raw XML response
                .map(this::convertToSoapDetail) // Convert XML to Java object
                .map(this::extractArticles); // Extract articles from response
    }

    public List<Article> extractArticles(Detail detail) {
        return Optional.ofNullable(detail)
                .map(Detail::getBody)
                .map(SoapBodyDetail::getGetArticlesResponse)
                .map(GetArticlesResponse::getArticles)
                .orElse(List.of()); // Return empty list if any value is null
    }

    private Detail convertToSoapDetail(String xml) {
        try {
            return xmlMapper.readValue(xml, Detail.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing XML response", e);
        }
    }
}
