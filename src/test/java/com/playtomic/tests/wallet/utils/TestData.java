package com.playtomic.tests.wallet.utils;

import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.domain.model.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@TestComponent
@RequiredArgsConstructor
public class TestData {

    private final WalletRepository walletRepository;

    public void givenThereIsAWallet(String walletId, String walletBalance) {
        StepVerifier
                .create(walletRepository.save(new Wallet(walletId, new BigDecimal(walletBalance))))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }
}
