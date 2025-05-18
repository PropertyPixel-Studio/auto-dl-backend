package cz.pps.auto_dl_be.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = "apiKey",
    type = SecuritySchemeType.APIKEY,
    paramName = "API-KEY",
    in = io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER,
    description = "API key authentication"
)
public class OpenApiConfig {

    @Value("${filter.apikey:your-api-key-value}")
    private String apiKey;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auto-DL Backend API Documentation")
                        .version("1.0")
                        .description("Documentation of API endpoints for the Auto-DL backend application. " +
                                "All API endpoints require an API key to be provided in the 'API-KEY' header." +
                                "\n\nDefault API Key: " + apiKey)
                        .contact(new Contact()
                                .name("Propix Studio")
                                .url("https://propix.studio")))
                .addSecurityItem(new SecurityRequirement().addList("apiKey"))
                .components(new Components());
    }
}