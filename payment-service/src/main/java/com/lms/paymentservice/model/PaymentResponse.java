package com.lms.paymentservice.model;

public class PaymentResponse {
    private boolean success;
    private String transactionId;
    private String message;
    private String amount;

    public PaymentResponse(boolean success, String transactionId, String message, String amount) {

        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
        this.amount = amount;
    }

    // Getter and Setter for success
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    // Getter and Setter for transactionId
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    // Getter and Setter for message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getter and Setter for amount
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}