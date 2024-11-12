package com.lms.paymentservice.handler;

import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for handling payment-related exceptions.
 *
 * This class is annotated with {@code @ControllerAdvice}, making it a centralized
 * exception handler across all controllers within the application. It provides
 * specific handling for exceptions thrown by the Stripe API.
 *
 * @author Callum Carroll
 */

@ControllerAdvice
public class PaymentExceptionHandler {

    /**
     * Handles {@link StripeException} exceptions.
     *
     * This method is triggered when a {@code StripeException} is thrown, typically
     * due to an error in payment processing with the Stripe API. It returns an
     * HTTP 400 (Bad Request) response with the exception's message as the response body.
     *
     * @param ex the {@code StripeException} thrown during payment processing
     * @return a {@link ResponseEntity} containing the error message and HTTP status code
     */

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<String> handleStripeException(StripeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}