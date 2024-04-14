package com.playtomic.tests.wallet.domain.getwallet;

import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.domain.model.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetWalletService {

    private final WalletRepository walletRepository;

    public Mono<Wallet> getWallet(WalletRequest walletRequest) {
        return walletRepository.findById(walletRequest.id())
                .switchIfEmpty(Mono.error(new NonExistingWalletException()));
    }
}
