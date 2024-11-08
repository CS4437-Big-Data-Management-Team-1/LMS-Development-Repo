package com.lms.paymentservice.service;

import com.lms.paymentservice.model.PaymentRequest;
import com.lms.paymentservice.model.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    // Injects the Stripe secret key from env

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    /**
     * Processes a payment request using the Stripe API.
     *
     * @param paymentRequest contains the details for the payment (amount, currency, etc.)
     * @return a PaymentResponse indicating whether the payment was successful or not
     */

    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        Stripe.apiKey = stripeSecretKey;

        try {
            // Create a Charge object using Stripe's SDK
            ChargeCreateParams params =
                    ChargeCreateParams.builder()
                            .setAmount(Long.parseLong(paymentRequest.getAmount())) // Amount in cents
                            .setCurrency(paymentRequest.getCurrency())
                            .setDescription(paymentRequest.getDescription())
                            .setSource(paymentRequest.getSource()) // Source from client-side
                            .build();

            // Call Stripe's API to create the charge
            Charge charge = Charge.create(params);  // Perform the actual charge with Stripe API

            // Return success response with charge ID
            return new PaymentResponse(true, charge.getId(), "Payment successful!");

        } catch (StripeException e) {
            // Handle the error, return a failure response with an error message
            return new PaymentResponse(false, null, e.getMessage());
        }
    }
}