package com.lms.paymentservice.controller;

import com.lms.paymentservice.model.PaymentRequest;
import com.lms.paymentservice.service.PaymentService;
import com.lms.paymentservice.database.PaymentDatabaseController;
import com.lms.paymentservice.model.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    PaymentDatabaseController db = new PaymentDatabaseController();
    /**
     * recieves payments and send them to the processpayment function for processing .
     *
     * @param PaymentRequest the details of the payment sent by the user
     * @return PaymentResponse Details of the payment post processing
     */
    @PostMapping("/process")
    public PaymentResponse processPayment(@RequestBody PaymentRequest paymentRequest) {

        return paymentService.processPayment(paymentRequest);
    }
}