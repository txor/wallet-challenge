package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.domain.getwallet.GetWalletService;
import com.playtomic.tests.wallet.domain.getwallet.NonExistingWalletException;
import com.playtomic.tests.wallet.domain.getwallet.WalletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class WalletController {
    private Logger log = LoggerFactory.getLogger(WalletController.class);

    private final GetWalletService getWalletService;

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
                .map(wallet -> new WalletDto(wallet.getId(), String.valueOf(wallet.getBalance())));
    }
}
