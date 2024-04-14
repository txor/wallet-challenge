package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.domain.getwallet.GetWalletService;
import com.playtomic.tests.wallet.domain.getwallet.NonExistingWalletException;
import com.playtomic.tests.wallet.domain.getwallet.WalletRequest;
import com.playtomic.tests.wallet.domain.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private GetWalletService getWalletService;

    @Test
    void getWalletReturnsAWalletJsonObject() {
        String id = "1";
        String balance = "50.0";
        when(getWalletService.getWallet(any(WalletRequest.class)))
                .thenReturn(Mono.just(new Wallet(id, Double.parseDouble(balance))));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/wallets/{id}").build(id))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.balance").isEqualTo(balance);
    }

    @Test
    void getNonExistingWalletReturns404() {
        when(getWalletService.getWallet(any(WalletRequest.class)))
                .thenReturn(Mono.error(new NonExistingWalletException()));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/wallets/{id}").build("non-existing"))
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}