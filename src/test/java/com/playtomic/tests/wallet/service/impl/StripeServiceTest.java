package com.playtomic.tests.wallet.service.impl;


import com.playtomic.tests.wallet.service.StripeAmountTooSmallException;
import com.playtomic.tests.wallet.service.StripeService;
import com.playtomic.tests.wallet.service.StripeServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;

import java.math.BigDecimal;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(StripeService.class)
@ActiveProfiles(profiles = "test")
public class StripeServiceTest {

    @Value("${stripe.simulator.charges-uri}")
    private String testUri;

    @Autowired
    private StripeService client;
    @Autowired
    private MockRestServiceServer server;

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

