package com.lms.paymentservice.handler;

import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the Payment Service.
 * Intercepts specific exceptions and provides customized responses for them.
 */

@ControllerAdvice
public class PaymentExceptionHandler {

    /**
     * Handles StripeException, which is thrown by the Stripe API when an error occurs
     * during payment processing.
     *
     * @param ex the StripeException instance containing details of the error.
     * @return a ResponseEntity with the error message and a BAD_REQUEST (400) status.
     */

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<String> handleStripeException(StripeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}