package com.playtomic.tests.wallet.domain.apiclient;

import java.math.BigDecimal;

public record ChargeRequest(String creditCardNumber, BigDecimal amount) {
}
