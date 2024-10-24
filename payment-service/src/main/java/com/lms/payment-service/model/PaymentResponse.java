package com.lms.paymentservice.model;

public class PaymentResponse {
    private boolean success;
    private String transactionId;
    private String message;

    public PaymentResponse(boolean success, String transactionId, String message) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
    }

    
}