package dev.studentpp1.streamingservice.payments.domain.exception;

public class PaymentDomainException extends RuntimeException {
    public PaymentDomainException(String message) {
        super(message);
    }
}
