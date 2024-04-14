package com.playtomic.tests.wallet.infra;

import com.playtomic.tests.wallet.domain.apiclient.ChargeRequest;
import com.playtomic.tests.wallet.domain.apiclient.ChargeResponse;
import com.playtomic.tests.wallet.domain.apiclient.PaymentApiClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DummyPaymentApiClient implements PaymentApiClient {
    @Override
    public Mono<ChargeResponse> charge(ChargeRequest chargeRequest) {
        return Mono.just(new ChargeResponse("1"));
    }
}
