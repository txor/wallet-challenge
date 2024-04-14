package com.playtomic.tests.wallet;

import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.domain.model.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class SampleWallet implements ApplicationRunner {

    private final WalletRepository walletRepository;

    @Override
    public void run(ApplicationArguments args) {
        walletRepository.save(new Wallet("999", new BigDecimal("68.9"), true)).block();
    }
}
