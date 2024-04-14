package com.playtomic.tests.wallet.domain.topup;

import com.playtomic.tests.wallet.domain.apiclient.PaymentApiClient;
import com.playtomic.tests.wallet.domain.model.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TopUpWalletService {

    private final PaymentApiClient paymentApiClient;
    private final WalletRepository walletRepository;

    public Mono<Void> topUpWallet(TopUpRequest topUpRequest) {
        return Mono.empty().then();
    }
}

