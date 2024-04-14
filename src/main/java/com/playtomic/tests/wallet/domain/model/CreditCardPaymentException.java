package com.playtomic.tests.wallet.domain.model;

public class CreditCardPaymentException extends RuntimeException {
    public CreditCardPaymentException(Throwable throwable) {
        super(throwable);
    }
}
