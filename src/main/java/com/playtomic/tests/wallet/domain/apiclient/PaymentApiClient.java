package com.playtomic.tests.wallet.domain.apiclient;

import reactor.core.publisher.Mono;

public interface PaymentApiClient {
    Mono<ChargeResponse> charge(ChargeRequest chargeRequest);
}
