package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.domain.getwallet.GetWalletService;
import com.playtomic.tests.wallet.domain.getwallet.WalletRequest;
import com.playtomic.tests.wallet.domain.model.NonExistingWalletException;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.domain.topup.TopUpRequest;
import com.playtomic.tests.wallet.domain.topup.TopUpWalletService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.function.Function;

@RestController
@RequiredArgsConstructor
public class WalletController {
    private Logger log = LoggerFactory.getLogger(WalletController.class);

    private final GetWalletService getWalletService;
    private final TopUpWalletService topUpWalletService;

    @RequestMapping("/")
    void log() {
        log.info("Logging from /");
    }

    @GetMapping(value = "/wallets/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<WalletDto> getWallet(@PathVariable("id") String id, ServerHttpResponse response) {
        return Mono.just(id)
                .map(WalletRequest::new)
                .flatMap(getWalletService::getWallet)
                .onErrorComplete(
                        exception -> {
                            if (exception instanceof NonExistingWalletException)
                                response.setStatusCode(HttpStatusCode.valueOf(404));
                            return true;
                        })
                .map(toWalletDto());
    }

    @PostMapping(value = "/wallets/{id}/topup")
    public Mono<WalletDto> topUp(@PathVariable("id") String id, @RequestBody TopUpDto topUp, ServerHttpResponse response) {
        return Mono.just(new TopUpRequest(id, topUp.creditCard(), new BigDecimal(topUp.amount())))
                .flatMap(topUpWalletService::topUpWallet)
                .onErrorComplete(
                        exception -> {
                            if (exception instanceof NonExistingWalletException)
                                response.setStatusCode(HttpStatusCode.valueOf(404));
                            return true;
                        })
                .map(toWalletDto());
    }

    private Function<Wallet, WalletDto> toWalletDto() {
        return wallet -> new WalletDto(wallet.getId(), String.valueOf(wallet.getBalance()));
    }
}
