package com.lms.paymentservice.service;

import com.lms.paymentservice.model.PaymentRequest;
import com.lms.paymentservice.model.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class for processing payments.
 *
 * This class interacts with the Stripe API to create and process charges based on
 * payment requests, using Stripe's SDK for communication. It handles the conversion
 * of {@link PaymentRequest} data into a Stripe charge and generates a {@link PaymentResponse}
 * to indicate the outcome of the transaction.
 *
 * https://docs.stripe.com/api
 *
 * The Stripe secret key is obtained from system properties and set as the API key
 * for all Stripe operations.
 *
 * @author Callum Carroll
 */

@Service
public class PaymentService {

    private String stripeSecretKey = System.getProperty("STRIPE_SECRET_KEY");


    /**
     * Processes a payment request by creating a Stripe charge.
     *
     * This method constructs a charge request using the information in the
     * {@link PaymentRequest} object, then attempts to process the charge using
     * the Stripe API. If successful, it returns a {@link PaymentResponse} with
     * a success status and the charge ID. If there is an error, it returns a
     * failure response with an error message.
     *
     * @param paymentRequest the payment request containing the details of the payment
     * @return a {@link PaymentResponse} indicating the outcome of the payment
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

            Charge charge = Charge.create(params);  // Perform the actual charge with Stripe API

            // Return success response with charge ID
            return new PaymentResponse(true, charge.getId(), "Payment successful!");

        } catch (StripeException e) {
            // Handle the error, return a failure response with an error message
            return new PaymentResponse(false, null, e.getMessage());
        }
    }
}