package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.domain.getwallet.GetWalletService;
import com.playtomic.tests.wallet.domain.model.NonExistingWalletException;
import com.playtomic.tests.wallet.domain.getwallet.WalletRequest;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.domain.topup.TopUpRequest;
import com.playtomic.tests.wallet.domain.topup.TopUpWalletService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private GetWalletService getWalletService;
    @MockBean
    private TopUpWalletService topUpWalletService;
    @Captor
    private ArgumentCaptor<TopUpRequest> topUpRequestCaptor;

    @Test
    void getWalletReturnsAWalletJsonObject() {
        String id = "1";
        String balance = "50.0";
        when(getWalletService.getWallet(any(WalletRequest.class)))
                .thenReturn(Mono.just(new Wallet(id, new BigDecimal(balance))));

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

    @Test
    void topUpWalletCallsTopUpAndReturnsOk() {
        String walletId = "1234";
        String cardNumber = "4242424242424242";
        String amount = "50";
        String request = "{\"credit-card\": \"" + cardNumber + "\", \"amount\": " + amount + "}";

        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/wallets/{id}/topup").build(walletId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(request), String.class))
                .exchange()
                .expectStatus()
                .isOk();

        verify(topUpWalletService).topUpWallet(topUpRequestCaptor.capture());
        TopUpRequest topUpRequest = topUpRequestCaptor.getValue();
        assertAll(
                () -> assertEquals(walletId, topUpRequest.walletId()),
                () -> assertEquals(cardNumber, topUpRequest.creditCard()),
                () -> assertEquals(new BigDecimal(amount), topUpRequest.amount())
        );
    }

    @Test
    void topUpNonExistingWalletReturns404() {
        when(topUpWalletService.topUpWallet(any(TopUpRequest.class)))
                .thenReturn(Mono.error(new NonExistingWalletException()));

        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/wallets/{id}/topup").build("non-existing"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just("{\"credit-card\": \"1324\", \"amount\": 1234}"), String.class))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void topUpWalletWithInvalidAmountReturnsBadRequest() {
        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/wallets/{id}/topup").build("1234"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just("{\"credit-card\": \"1234\", \"amount\": invalid}"), String.class))
                .exchange()
                .expectStatus()
                .isBadRequest();

        verify(topUpWalletService, never()).topUpWallet(any(TopUpRequest.class));
    }
}