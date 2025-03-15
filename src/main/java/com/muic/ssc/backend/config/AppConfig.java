package com.muic.ssc.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    // Create RestTemplate bean for HTTP requests
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}