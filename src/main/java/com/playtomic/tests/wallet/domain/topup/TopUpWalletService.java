package com.playtomic.tests.wallet.domain.topup;

import com.playtomic.tests.wallet.domain.apiclient.ChargeRequest;
import com.playtomic.tests.wallet.domain.apiclient.PaymentApiClient;
import com.playtomic.tests.wallet.domain.apiclient.PaymentApiException;
import com.playtomic.tests.wallet.domain.model.CreditCardPaymentException;
import com.playtomic.tests.wallet.domain.model.NonExistingWalletException;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.domain.model.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TopUpWalletService {

    private final WalletRepository walletRepository;
    private final PaymentApiClient paymentApiClient;

    public Mono<Wallet> topUpWallet(TopUpRequest topUpRequest) {
        return walletRepository.findById(topUpRequest.walletId())
                .switchIfEmpty(Mono.error(new NonExistingWalletException()))
                .flatMap(wallet ->
                        paymentApiClient.charge(new ChargeRequest(topUpRequest.creditCard(), topUpRequest.amount()))
                                .onErrorResume(throwable -> throwable instanceof PaymentApiException,
                                        throwable -> Mono.error(new CreditCardPaymentException(throwable)))
                                .flatMap(payment ->
                                        walletRepository.save(new Wallet(wallet.getId(), wallet.getBalance().add(topUpRequest.amount()), false)))
                );
    }
}

