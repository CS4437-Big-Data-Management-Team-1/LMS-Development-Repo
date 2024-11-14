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
            @RequestHeader(value = "Authorisation", required = false) String authorisationHeader) {
            try {
                String msg = authService.validateToken(authorisationHeader);
                String[] splits = msg.split("Access granted for user: ");
                String uid = splits[1];
                PaymentResponse response = paymentService.processPayment(paymentRequest, uid);
                return ResponseEntity.ok("Payment processed");
            }catch(RuntimeException e){
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getStackTrace()[0].getLineNumber() + " " + e + " " +  e.getMessage());
            }

    }
}