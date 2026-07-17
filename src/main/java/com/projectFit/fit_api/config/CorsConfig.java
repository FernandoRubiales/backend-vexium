package com.projectFit.fit_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origenes donde se permite el request
        config.setAllowedOrigins(List.of(
                "http://localhost:5173"  // frontend react

        ));

        // Metodos HTTP posibles
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // Header para los request
        config.setAllowedHeaders(List.of(
                "Authorization",   // para el  JWT de Auth0
                "Content-Type"  // para mandar JSON
        ));

        // Navegador manda cookies y credenciales
        config.setAllowCredentials(true);

        // ¿Por cuántos segundos el navegador cachea el preflight?
        config.setMaxAge(3600L);

        // Aplicás esta config a todos los endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
