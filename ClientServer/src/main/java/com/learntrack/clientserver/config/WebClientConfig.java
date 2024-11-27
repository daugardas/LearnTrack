package com.learntrack.clientserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${resource.server.url}")
    private String resourceServerUrl;

    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .baseUrl(resourceServerUrl);
    }
} 