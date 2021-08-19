package com.playtomic.tests.wallet.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.util.Map;


/**
 * Handles the communication with Stripe.
 *
 * A real implementation would call to String using their API/SDK.
 * This dummy implementation throws an error when trying to charge less than 10€.
 */
@Service
public class StripeService {

    @NonNull
    private URI uri;

    @NonNull
    private RestTemplate restTemplate;

    public StripeService(@Value("stripe.simulator.uri") URI uri, @NotNull RestTemplateBuilder restTemplateBuilder) {
        this.uri = uri;
        this.restTemplate =
                restTemplateBuilder
                .errorHandler(new StripeRestTemplateResponseErrorHandler())
                .build();
    }

    /**
     * Charges money in the credit card.
     *
     * Ignore the fact that no CVC or expiration date are provided.
     *
     * @param creditCardNumber The number of the credit card
     * @param amount The amount that will be charged.
     *
     * @throws StripeServiceException
     */
    public void charge(String creditCardNumber, BigDecimal amount) throws StripeServiceException {
        Assert.notNull(creditCardNumber, "creditCardNumber == null");
        Assert.notNull(amount, "amount == null");

        ChargeRequest body = new ChargeRequest(creditCardNumber, amount);
        restTemplate.postForLocation(uri, body);
    }

    @AllArgsConstructor
    private static class ChargeRequest {

        @JsonProperty("credit_card")
        String creditCardNumber;

        @JsonProperty("amount")
        BigDecimal amount;
    }
}
