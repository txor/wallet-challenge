package com.playtomic.tests.wallet.domain.topup;

import java.math.BigDecimal;

public record TopUpRequest(String walletId, String creditCard, BigDecimal amount) {
}
