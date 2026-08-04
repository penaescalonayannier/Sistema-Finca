package com.kynsoft.report.controller;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    @GetMapping("")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok().body(
                Map.of(
                "status", "UP",
                "service", "EstadoCuenta API",
                "timestamp", LocalDateTime.now().toString()
            )
        );
    }
    
    @GetMapping("/test-cors")
    public ResponseEntity<?> testCors() {
        return ResponseEntity.ok().body(
            Map.of(
                "message", "CORS está funcionando",
                "allowedOrigins", new String[]{"http://localhost:8080", "http://localhost:8081", "http://localhost:5173"}
            )
        );
    }
}