package com.lms.paymentservice.controller;

import com.lms.paymentservice.model.PaymentRequest;
import com.lms.paymentservice.service.PaymentService;
import com.lms.paymentservice.database.PaymentDatabaseController;
import com.lms.paymentservice.model.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling payment-related requests.
 * This class provides endpoints to process payments.
 *
 * This class is responsible for receiving and responding to HTTP
 * requests related to payment processing.
 *
 * @author Callum Carroll
 */

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
<<<<<<< HEAD

    /**
     * Endpoint to process a payment.
     *
     * This method accepts a payment request, processes it using the
     * {@link PaymentService}, and returns a payment response.
     *
     * @param paymentRequest the payment request containing the details for the payment
     * @return a {@link PaymentResponse} object containing the outcome of the payment process
     */

=======
    PaymentDatabaseController db = new PaymentDatabaseController();
    /**
     * recieves payments and send them to the processpayment function for processing .
     *
     * @param PaymentRequest the details of the payment sent by the user
     * @return PaymentResponse Details of the payment post processing
     */
>>>>>>> 967793f28bc87ef12ad955aa9613e5f6b9c8b8e9
    @PostMapping("/process")
    public PaymentResponse processPayment(@RequestBody PaymentRequest paymentRequest) {

        return paymentService.processPayment(paymentRequest);
    }
}