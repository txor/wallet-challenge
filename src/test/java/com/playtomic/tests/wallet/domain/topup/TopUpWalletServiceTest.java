package com.playtomic.tests.wallet.domain.topup;

import com.playtomic.tests.wallet.domain.apiclient.ChargeRequest;
import com.playtomic.tests.wallet.domain.apiclient.ChargeResponse;
import com.playtomic.tests.wallet.domain.apiclient.PaymentApiClient;
import com.playtomic.tests.wallet.domain.apiclient.PaymentApiException;
import com.playtomic.tests.wallet.domain.model.CreditCardPaymentException;
import com.playtomic.tests.wallet.domain.model.NonExistingWalletException;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.domain.model.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopUpWalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private PaymentApiClient paymentApiClient;
    @Captor
    private ArgumentCaptor<String> walletIdCaptor;
    @Captor
    private ArgumentCaptor<ChargeRequest> chargeRequestCaptor;
    @Captor
    private ArgumentCaptor<Wallet> walletCaptor;

    @InjectMocks
    private TopUpWalletService topUpWalletService;

    @Test
    void topUpWallet() {
        String walletId = "1234";
        String creditCard = "4242424242424242";
        BigDecimal amount = new BigDecimal("50");
        when(walletRepository.findById(anyString()))
                .thenReturn(Mono.just(new Wallet(walletId, new BigDecimal("50"), true)));
        when(paymentApiClient.charge(any(ChargeRequest.class)))
                .thenReturn(Mono.just(new ChargeResponse("1234")));
        when(walletRepository.save(any()))
                .thenReturn(Mono.just(new Wallet(walletId, new BigDecimal("100"), true)));

        StepVerifier.create(topUpWalletService.topUpWallet(new TopUpRequest(walletId, creditCard, amount)))
                .expectNextCount(1)
                .verifyComplete();

        verify(walletRepository).findById(walletIdCaptor.capture());
        assertEquals(walletId, walletIdCaptor.getValue());
        verify(paymentApiClient).charge(chargeRequestCaptor.capture());
        ChargeRequest chargeRequest = chargeRequestCaptor.getValue();
        assertAll(
                () -> assertEquals(creditCard, chargeRequest.creditCardNumber()),
                () -> assertEquals(amount, chargeRequest.amount())
        );
        verify(walletRepository).save(walletCaptor.capture());
        Wallet wallet = walletCaptor.getValue();
        assertAll(
                () -> assertEquals(walletId, wallet.getId()),
                () -> assertEquals(new BigDecimal("100"), wallet.getBalance())
        );
    }

    @Test
    void topUpWalletEmitsErrorAndDoesNotTopUpIfPaymentApiClientThrowsPaymentApiException() {
        String walletId = "1234";
        String creditCard = "4242424242424242";
        BigDecimal amount = new BigDecimal("5");
        when(walletRepository.findById(anyString()))
                .thenReturn(Mono.just(new Wallet(walletId, new BigDecimal("50"), true)));
        when(paymentApiClient.charge(any(ChargeRequest.class)))
                .thenReturn(Mono.error(new PaymentApiException()));

        StepVerifier.create(topUpWalletService.topUpWallet(new TopUpRequest(walletId, creditCard, amount)))
                .expectError(CreditCardPaymentException.class)
                .verify();

        verify(walletRepository).findById(anyString());
        verify(paymentApiClient).charge(any(ChargeRequest.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void topUpWalletEmitsErrorAndDoesNotTopUpIfWalletDoesNotExist() {
        String walletId = "1234";
        String creditCard = "4242424242424242";
        BigDecimal amount = new BigDecimal("5");
        when(walletRepository.findById(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(topUpWalletService.topUpWallet(new TopUpRequest(walletId, creditCard, amount)))
                .expectError(NonExistingWalletException.class)
                .verify();

        verify(walletRepository).findById(anyString());
        verify(paymentApiClient, never()).charge(any(ChargeRequest.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }
}