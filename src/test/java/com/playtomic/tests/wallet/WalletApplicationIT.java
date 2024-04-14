package com.playtomic.tests.wallet;

import com.playtomic.tests.wallet.domain.model.WalletRepository;
import com.playtomic.tests.wallet.utils.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestData.class)
@ActiveProfiles(profiles = "develop")
class WalletApplicationIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private TestData testData;
    @Autowired
    private WalletRepository walletRepository;

    @Test
    void getWallet() {
        String walletId = "1234";
        String walletBalance = "50";
        testData.givenThereIsJustAWallet(walletId, walletBalance);

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/wallets/{id}").build(walletId))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(walletId)
                .jsonPath("$.balance").isEqualTo(walletBalance);
    }

    @Test
    void topUpWallet() {
        String walletId = "1234";
        testData.givenThereIsJustAWallet(walletId, "50");
        String request = """
                    {
                        "credit-card": "4567890123451234",
                        "amount": 50
                    }
                """;

        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/wallets/{id}/topup").build(walletId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(request), String.class))
                .exchange()
                .expectStatus()
                .isOk();

        StepVerifier
                .create(walletRepository.findById(walletId))
                .assertNext(wallet -> {
                    assertEquals(walletId, wallet.getId());
                    assertEquals(new BigDecimal("100"), wallet.getBalance());
                })
                .verifyComplete();
    }
}
