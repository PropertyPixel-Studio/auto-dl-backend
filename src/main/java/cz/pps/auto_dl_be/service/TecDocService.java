package cz.pps.auto_dl_be.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import cz.pps.auto_dl_be.dto.brands.*;
import cz.pps.auto_dl_be.dto.detail.Article;
import cz.pps.auto_dl_be.dto.detail.Detail;
import cz.pps.auto_dl_be.dto.detail.GetArticlesResponse;
import cz.pps.auto_dl_be.dto.detail.SoapBodyDetail;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    private static final Logger logger = LoggerFactory.getLogger(TecDocService.class);
    private final WebClient webClient;
    private final XmlMapper xmlMapper;
    private final MeterRegistry meterRegistry;
    
    @Value("${tecdoc.api.url}")
    private String tecDocUrl;
    @Value("${tecdoc.api.key}")
    private String tecDocKey;
    @Value("${tecdoc.provider.id}")
    private String tecDocProviderId;
    
    private Counter tecDocApiCallCounter;
    private Counter tecDocApiBrandsCallCounter;
    private Counter tecDocApiArticlesCallCounter;
    private Counter tecDocApiErrorCounter;
    private Timer tecDocApiBrandsCallTimer;
    private Timer tecDocApiArticlesCallTimer;
    private Counter tecDocScrapingStartedCounter;
    private Counter tecDocScrapingCompletedCounter;
    private Counter tecDocScrapingFailedCounter;

    public TecDocService(WebClient.Builder webClientBuilder, MeterRegistry meterRegistry) {
        this.webClient = webClientBuilder.baseUrl(tecDocUrl).build();
        this.xmlMapper = new XmlMapper(); // Jackson XML Mapper
        this.meterRegistry = meterRegistry;
        initMetrics();
    }
    
    private void initMetrics() {
        tecDocApiCallCounter = Counter.builder("tecdoc.api.call.count")
                .description("Count of TecDoc API calls")
                .register(meterRegistry);
                
        tecDocApiBrandsCallCounter = Counter.builder("tecdoc.api.brands.call.count")
                .description("Count of TecDoc API brand fetch calls")
                .register(meterRegistry);
                
        tecDocApiArticlesCallCounter = Counter.builder("tecdoc.api.articles.call.count")
                .description("Count of TecDoc API articles fetch calls")
                .register(meterRegistry);
                
        tecDocApiErrorCounter = Counter.builder("tecdoc.api.error.count")
                .description("Count of TecDoc API call errors")
                .register(meterRegistry);
                
        tecDocApiBrandsCallTimer = Timer.builder("tecdoc.api.brands.call.time")
                .description("Time taken for TecDoc API brand fetch calls")
                .register(meterRegistry);
                
        tecDocApiArticlesCallTimer = Timer.builder("tecdoc.api.articles.call.time")
                .description("Time taken for TecDoc API articles fetch calls")
                .register(meterRegistry);
                
        tecDocScrapingStartedCounter = Counter.builder("tecdoc.scraping.started.count")
                .description("Count of TecDoc scraping operations started")
                .register(meterRegistry);
                
        tecDocScrapingCompletedCounter = Counter.builder("tecdoc.scraping.completed.count")
                .description("Count of TecDoc scraping operations successfully completed")
                .register(meterRegistry);
                
        tecDocScrapingFailedCounter = Counter.builder("tecdoc.scraping.failed.count")
                .description("Count of TecDoc scraping operations that failed")
                .register(meterRegistry);
                
        // Add a gauge for TecDoc scraping status (0=idle, 1=in-progress)
        meterRegistry.gauge("tecdoc.scraping.status", 0);
    }

    private String getBrandsXml() {
        ClassPathResource resource = new ClassPathResource("getBrands.xml");
        String template;
        try {
            template = new String(Files.readAllBytes(Paths.get(resource.getURI())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        template = template.replace("${providerId}", tecDocProviderId);
        return template;
    }

    public Mono<List<Brand>> fetchBrands() {
        tecDocApiBrandsCallCounter.increment();
        tecDocApiCallCounter.increment();
        meterRegistry.gauge("tecdoc.scraping.status", 1); // Set status to in-progress
        tecDocScrapingStartedCounter.increment();
        
        Timer.Sample sample = Timer.start(meterRegistry);
        
        String xmlContent = getBrandsXml();
        logger.info("Fetching brands from TecDoc API");
        
        return webClient.post()
                .uri(tecDocUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .header("X-API-KEY", tecDocKey)
                .bodyValue(xmlContent)
                .retrieve()
                .bodyToMono(String.class) // Get raw XML response
                .map(this::convertToSoapEnvelope) // Convert XML to Java object
                .map(this::extractBrands) // Extract brands from response
                .doOnSuccess(brands -> {
                    sample.stop(tecDocApiBrandsCallTimer);
                    tecDocScrapingCompletedCounter.increment();
                    meterRegistry.gauge("tecdoc.scraping.status", 0); // Reset status to idle
                    logger.info("Successfully fetched {} brands from TecDoc API", brands.size());
                })
                .onErrorResume(error -> {
                    tecDocApiErrorCounter.increment();
                    tecDocScrapingFailedCounter.increment();
                    sample.stop(tecDocApiBrandsCallTimer);
                    meterRegistry.gauge("tecdoc.scraping.status", 0); // Reset status to idle
                    logger.error("Error fetching brands from TecDoc API: {}", error.getMessage());
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
                .replace("${dataSupplierIds}", dataSupplierIds)
                .replace("${providerId}", tecDocProviderId);
        return template;
    }

    public Mono<List<Article>> fetchArticles(String searchQuery, String dataSupplierIds) {
        tecDocApiArticlesCallCounter.increment();
        tecDocApiCallCounter.increment();
        meterRegistry.gauge("tecdoc.scraping.status", 1); // Set status to in-progress
        
        Timer.Sample sample = Timer.start(meterRegistry);
        
        String xmlContent = getDetailXmlTemplate(searchQuery, dataSupplierIds);
        logger.info("Fetching articles from TecDoc API for query: {}, supplier: {}", searchQuery, dataSupplierIds);
        
        return webClient.post()
                .uri(tecDocUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .header("X-API-KEY", tecDocKey)
                .bodyValue(xmlContent)
                .retrieve()
                .bodyToMono(String.class) // Get raw XML response
                .map(this::convertToSoapDetail) // Convert XML to Java object
                .map(this::extractArticles) // Extract articles from response
                .doOnSuccess(articles -> {
                    sample.stop(tecDocApiArticlesCallTimer);
                    meterRegistry.gauge("tecdoc.scraping.status", 0); // Reset status to idle
                    logger.info("Successfully fetched {} articles from TecDoc API for query: {}", 
                            articles.size(), searchQuery);
                })
                .onErrorResume(error -> {
                    tecDocApiErrorCounter.increment();
                    sample.stop(tecDocApiArticlesCallTimer);
                    meterRegistry.gauge("tecdoc.scraping.status", 0); // Reset status to idle
                    logger.error("Error fetching articles from TecDoc API for query {}: {}", 
                            searchQuery, error.getMessage());
                    return Mono.just(List.of()); // Return empty list on failure
                });
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
