package com.playtomic.tests.wallet;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WalletApplicationConfiguration {

    @Bean
    public RestTemplateBuilder getRestTemplateBuilder() {
        return new RestTemplateBuilder();
    }
}
