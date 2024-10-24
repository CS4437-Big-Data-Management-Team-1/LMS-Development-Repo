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

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

   

    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        Stripe.apiKey = stripeSecretKey;

        try {
            
            ChargeCreateParams params =
                    ChargeCreateParams.builder()
                            .setAmount(Long.parseLong(paymentRequest.getAmount())) 
                            .setCurrency(paymentRequest.getCurrency())
                            .setDescription(paymentRequest.getDescription())
                            .setSource(paymentRequest.getSource()) 
                            .build();

            Charge charge = Charge.create(params); 

            
            return new PaymentResponse(true, charge.getId(), "Payment successful!");

        } catch (StripeException e) {
            
            return new PaymentResponse(false, null, e.getMessage());
        }
    }
}