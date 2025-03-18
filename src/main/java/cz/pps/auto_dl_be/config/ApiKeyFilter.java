package cz.pps.auto_dl_be.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class ApiKeyFilter extends GenericFilterBean {

    private static final String API_KEY_HEADER = "API-KEY";
    private static String VALID_API_KEY;

    @Value("${filter.apikey}")
    public void setValidApiKey(String apiKey) {
        VALID_API_KEY = apiKey;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String apiKey = httpRequest.getHeader(API_KEY_HEADER);

        if (apiKey == null || !apiKey.equals(VALID_API_KEY)) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
            return;
        }

        chain.doFilter(request, response);
    }
}
