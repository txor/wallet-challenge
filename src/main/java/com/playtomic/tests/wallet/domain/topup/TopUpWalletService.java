package com.playtomic.tests.wallet.domain.topup;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TopUpWalletService {
    public Mono<Void> topUpWallet(TopUpRequest topUpRequest) {
        return Mono.empty().then();
    }
}

