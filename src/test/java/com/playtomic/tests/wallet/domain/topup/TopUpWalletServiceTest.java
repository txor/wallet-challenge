package com.playtomic.tests.wallet.domain.topup;

import com.playtomic.tests.wallet.domain.apiclient.ChargeRequest;
import com.playtomic.tests.wallet.domain.apiclient.ChargeResponse;
import com.playtomic.tests.wallet.domain.apiclient.PaymentApiClient;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.domain.model.WalletRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TopUpWalletServiceTest {

    @Captor
    private ArgumentCaptor<String> walletIdCaptor;
    @Captor
    private ArgumentCaptor<ChargeRequest> chargeRequestCaptor;
    @Captor
    private ArgumentCaptor<Wallet> walletCaptor;

    @Test
    void topUpWallet() {
        String walletId = "1234";
        String creditCard = "4242424242424242";
        BigDecimal amount = new BigDecimal("50");
        PaymentApiClient paymentApiClient = mock(PaymentApiClient.class);
        when(paymentApiClient.charge(any(ChargeRequest.class)))
                .thenReturn(Mono.just(new ChargeResponse("1234")));
        WalletRepository walletRepository = mock(WalletRepository.class);
        when(walletRepository.findById(anyString()))
                .thenReturn(Mono.just(new Wallet(walletId, new BigDecimal("50"))));
        TopUpWalletService topUpWalletService = new TopUpWalletService(paymentApiClient, walletRepository);

        StepVerifier
                .create(topUpWalletService.topUpWallet(new TopUpRequest(walletId, creditCard, amount)))
                .verifyComplete();

        verify(paymentApiClient).charge(chargeRequestCaptor.capture());
        ChargeRequest chargeRequest = chargeRequestCaptor.getValue();
        assertAll(
                () -> assertEquals(creditCard, chargeRequest.creditCardNumber()),
                () -> assertEquals(amount, chargeRequest.amount())
        );
        verify(walletRepository).findById(walletIdCaptor.capture());
        assertEquals(walletId, walletIdCaptor.getValue());
        verify(walletRepository).save(walletCaptor.capture());
        Wallet wallet = walletCaptor.getValue();
        assertAll(
                () -> assertEquals(walletId, wallet.getId()),
                () -> assertEquals(new BigDecimal("100"), wallet.getBalance())
        );
    }
}