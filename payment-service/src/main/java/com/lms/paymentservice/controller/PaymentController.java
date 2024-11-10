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

    @PostMapping("/process")
    public PaymentResponse processPayment(@RequestBody PaymentRequest paymentRequest) {

        return paymentService.processPayment(paymentRequest);
    }
}