package com.projectFit.fit_api.config;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfiguration {

    @Value("${app.mercadopago.access-token}")
    private String accessToken;

    // Se ejecuta cuando levanta la app
    // Configura el SDK de MercadoPago con tu access token
    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }
}
