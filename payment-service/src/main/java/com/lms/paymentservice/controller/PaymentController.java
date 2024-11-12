package com.lms.paymentservice.controller;

import com.lms.paymentservice.model.PaymentRequest;
import com.lms.paymentservice.service.PaymentService;
import com.lms.paymentservice.service.AuthService;
import com.lms.paymentservice.database.PaymentDatabaseController;
import com.lms.paymentservice.model.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PaymentService paymentService;
    PaymentDatabaseController db = new PaymentDatabaseController();
    /**
     * recieves payments and send them to the processpayment function for processing .
     *
     * @param PaymentRequest the details of the payment sent by the user
     * @param authorisationHeader JWT token confiming the user is logged in
     * @return PaymentResponse Details of the payment post processing
     */
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(
            @RequestBody PaymentRequest paymentRequest,
            @RequestHeader(value = "Authorization", required = false) String authorisationHeader) {
            System.out.println("req;" + paymentRequest);
            try {
                System.out.println("auth: " + authorisationHeader );
                String uid = authService.validateToken(authorisationHeader);

                PaymentResponse response = paymentService.processPayment(paymentRequest, authorisationHeader);
                return ResponseEntity.ok(response);
            }catch(RuntimeException e){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
            }

    }
}