package com.playtomic.tests.wallet.infra.stripe;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.playtomic.tests.wallet.domain.apiclient.ChargeRequest;
import com.playtomic.tests.wallet.domain.apiclient.ChargeResponse;
import com.playtomic.tests.wallet.domain.apiclient.PaymentApiClient;
import com.playtomic.tests.wallet.domain.apiclient.PaymentApiException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.net.URI;


/**
 * Handles the communication with Stripe.
 * <p>
 * A real implementation would call to String using their API/SDK.
 * This dummy implementation throws an error when trying to charge less than 10€.
 */
@Service
public class StripeClient implements PaymentApiClient {

    private final URI chargesUri;

    private final URI refundsUri;

    private final RestTemplate restTemplate;

    public StripeClient(@Value("${stripe.simulator.charges-uri}") @NonNull URI chargesUri,
                        @Value("${stripe.simulator.refunds-uri}") @NonNull URI refundsUri,
                        @NonNull RestTemplateBuilder restTemplateBuilder) {
        this.chargesUri = chargesUri;
        this.refundsUri = refundsUri;
        this.restTemplate =
                restTemplateBuilder
                        .errorHandler(new StripeRestTemplateResponseErrorHandler())
                        .build();
    }

    @Override
    public Mono<ChargeResponse> charge(ChargeRequest stripeChargeRequest) {
        return Mono.fromCallable(
                        () -> charge(stripeChargeRequest.creditCardNumber(), stripeChargeRequest.amount()))
                .onErrorResume(error -> error instanceof StripeServiceException, throwable -> Mono.error(new PaymentApiException()))
                .map(payent -> new ChargeResponse(payent.id()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Charges money in the credit card.
     * <p>
     * Ignore the fact that no CVC or expiration date are provided.
     *
     * @param creditCardNumber The number of the credit card
     * @param amount           The amount that will be charged.
     * @throws StripeServiceException
     */
    public ChargeResponse charge(@NonNull String creditCardNumber, @NonNull BigDecimal amount) throws StripeServiceException {
        StripeChargeRequest body = new StripeChargeRequest(creditCardNumber, amount);
        return restTemplate.postForObject(chargesUri, body, ChargeResponse.class);
    }

    /**
     * Refunds the specified payment.
     */
    public void refund(@NonNull String paymentId) throws StripeServiceException {
        // Object.class because we don't read the body here.
        restTemplate.postForEntity(chargesUri.toString(), null, Object.class, paymentId);
    }

    private record StripeChargeRequest(@JsonProperty("credit_card") String creditCardNumber, BigDecimal amount) {
    }
}
