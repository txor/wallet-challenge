package com.playtomic.tests.wallet.domain.getwallet;

import com.playtomic.tests.wallet.domain.model.NonExistingWalletException;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.domain.model.WalletRepository;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetWalletServiceTest {

    @Test
    void getTheExistingGivenWallet() {
        String walletID = "1234";
        BigDecimal balance = new BigDecimal("50.0");
        Wallet wallet = new Wallet(walletID, balance, true);
        WalletRepository walletRepository = mock(WalletRepository.class);
        when(walletRepository.findById(eq(walletID))).thenReturn(Mono.just(wallet));
        GetWalletService getWalletService = new GetWalletService(walletRepository);

        StepVerifier
                .create(getWalletService.getWallet(new WalletRequest(walletID)))
                .assertNext(
                        w -> {
                            assertEquals(walletID, w.getId());
                            assertEquals(balance, w.getBalance());
                        }
                )
                .expectComplete()
                .verify();
    }

    @Test
    void emmitNonExistingWalletExceptionForNonExistingWallet() {
        WalletRepository walletRepository = mock(WalletRepository.class);
        when(walletRepository.findById(anyString())).thenReturn(Mono.empty());
        GetWalletService getWalletService = new GetWalletService(walletRepository);

        StepVerifier
                .create(getWalletService.getWallet(new WalletRequest("non-existing")))
                .expectError(NonExistingWalletException.class)
                .verify();
    }
}