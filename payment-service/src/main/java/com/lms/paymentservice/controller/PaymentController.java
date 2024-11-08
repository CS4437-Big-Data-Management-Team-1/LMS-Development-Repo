package com.lms.paymentservice.controller;

import com.lms.paymentservice.model.PaymentRequest;
import com.lms.paymentservice.model.PaymentResponse;
import com.lms.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Controller class for handling payment-related HTTP requests.
 * Provides an endpoint for processing payments.
 */

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     *
     *
     * @param paymentRequest The request payload containing payment details.
     * @return PaymentResponse indicating the success or failure of the payment.
     */

    @PostMapping("/process")
    public PaymentResponse processPayment(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.processPayment(paymentRequest);
    }
}