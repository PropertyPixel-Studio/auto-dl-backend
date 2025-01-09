package cz.pps.auto_dl_be.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TecDocService {
    @Value("${tecdoc.api.url}")
    private String tecDocUrl;
    @Value("${tecdoc.api.key}")
    private String tecDocKey;

    private ResponseEntity<String> sendPostRequest(Object requestBody) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", tecDocKey);
        HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(tecDocUrl, HttpMethod.POST, request, String.class);
    }
}
