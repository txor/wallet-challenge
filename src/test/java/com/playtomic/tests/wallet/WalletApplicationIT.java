package com.playtomic.tests.wallet;

import com.playtomic.tests.wallet.domain.model.WalletRepository;
import com.playtomic.tests.wallet.utils.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestData.class)
@ActiveProfiles(profiles = "test")
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
        testData.givenThereIsAWallet(walletId, walletBalance);

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
}
