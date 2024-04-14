package com.playtomic.tests.wallet.infra.stripe.impl;


import com.playtomic.tests.wallet.domain.apiclient.ChargeRequest;
import com.playtomic.tests.wallet.domain.apiclient.ChargeResponse;
import com.playtomic.tests.wallet.domain.apiclient.PaymentApiException;
import com.playtomic.tests.wallet.infra.stripe.StripeAmountTooSmallException;
import com.playtomic.tests.wallet.infra.stripe.StripeClient;
import com.playtomic.tests.wallet.infra.stripe.StripeServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(StripeClient.class)
@ActiveProfiles(profiles = "test")
public class StripeClientTest {

    @Value("${stripe.simulator.charges-uri}")
    private String testUri;

    @Autowired
    private StripeClient client;
    @Autowired
    private MockRestServiceServer server;

    @Test
    void test_exception_interface() {
        server.expect(requestTo(testUri))
                .andRespond(withStatus(HttpStatusCode.valueOf(422)));

        StepVerifier
                .create(client.charge(new ChargeRequest("4242 4242 4242 4242", new BigDecimal(5))))
                .expectError(PaymentApiException.class)
                .verify();
    }

    @Test
    public void test_ok_interface() throws StripeServiceException {
        server.expect(requestTo(testUri))
                .andRespond(withSuccess("{ \"id\": 1234 }", MediaType.APPLICATION_JSON));

        StepVerifier
                .create(client.charge(new ChargeRequest("4242 4242 4242 4242", new BigDecimal(15))))
                .expectNext(new ChargeResponse("1234"))
                .verifyComplete();
    }

    @Test
    public void test_exception() {
        server.expect(requestTo(testUri))
                .andRespond(withStatus(HttpStatusCode.valueOf(422)));

        Assertions.assertThrows(StripeAmountTooSmallException.class,
                () -> client.charge("4242 4242 4242 4242", new BigDecimal(5)));
    }

    @Test
    public void test_ok() throws StripeServiceException {
        server.expect(requestTo(testUri))
                .andRespond(withSuccess("{ \"id\": 1234 }", MediaType.APPLICATION_JSON));

        client.charge("4242 4242 4242 4242", new BigDecimal(15));
    }
}

