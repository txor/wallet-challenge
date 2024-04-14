package com.playtomic.tests.wallet.utils;

import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.domain.model.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import reactor.test.StepVerifier;

@TestComponent
@RequiredArgsConstructor
public class TestData {

    private final WalletRepository walletRepository;

    public void givenThereIsAWallet(String walletId, Double walletBalance) {
        StepVerifier
                .create(walletRepository.save(new Wallet(walletId, walletBalance)))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }
}
