package com.playtomic.tests.wallet.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TopUpDto(@JsonProperty("credit-card") String creditCard, String amount) {
}
