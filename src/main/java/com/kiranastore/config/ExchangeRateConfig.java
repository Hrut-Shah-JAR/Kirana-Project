package com.kiranastore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ExchangeRateConfig {

    /**
     * Creates a RestClient configured with the exchange rate API base URL.
     *
     * @param baseUrl API base URL
     * @return configured RestClient
     */
    @Bean
    public RestClient exchangeRateRestClient(
            @Value("${fxrates.api.base-url:https://api.fxratesapi.com/latest}") String baseUrl
    ) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("fxrates.api.base-url is missing or blank");
        }
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
