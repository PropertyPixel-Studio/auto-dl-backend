package cz.pps.auto_dl_be.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${filter.apikey:your-api-key-value}")
    private String apiKey;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "apiKey";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Auto-DL Backend API Documentation")
                        .version("1.0")
                        .description("Documentation of API endpoints for the Auto-DL backend application. " +
                                "All API endpoints require an API key to be provided in the 'API-KEY' header.")
                        .contact(new Contact()
                                .name("Propix Studio")
                                .url("https://propix.studio")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name("API-KEY")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("API key authentication. Enter the API key value in the field below.")));
    }
}