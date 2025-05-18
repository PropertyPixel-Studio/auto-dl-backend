package cz.pps.auto_dl_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Health check endpoints that do not require authentication")
public class HealthController {

    @Operation(
        summary = "Health check endpoint",
        description = "Provides basic health status of the application. This endpoint does not require authentication."
    )
    @ApiResponse(responseCode = "200", description = "Application is healthy")
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "auto-dl-backend");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}