package com.lms.paymentservice.service;

import com.lms.paymentservice.model.PaymentRequest;
import com.lms.paymentservice.model.PaymentResponse;
import com.lms.paymentservice.database.PaymentDatabaseController;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
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

    PaymentDatabaseController db = new PaymentDatabaseController();
    /**
     * Processes payment, validates, creates PaymentResponse object, add to database  and returns the response
     * @param PaymentRequest   payment request passed through by user
     * @response PaymentResponse Details of the payment post processing
     */

    public PaymentResponse processPayment(PaymentRequest paymentRequest, String token) {
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
            PaymentResponse response = new PaymentResponse(true, charge.getId(), "Payment successful!", paymentRequest.getAmount());
            db.connectToDB();
            db.addPaymentToDB(response, token);
            return response;

        } catch (StripeException e) {
            // Handle the error, return a failure response with an error message
            return new PaymentResponse(false, null, e.getMessage(), null);
        }
    }
}