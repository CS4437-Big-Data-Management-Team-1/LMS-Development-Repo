package com.lms.paymentservice.model;
/**
 * This class encapsulates the response details of a payment transaction,
 * including the success status, transaction ID, and an informational message.
 *
 * Shows the response to a payment request
 *
 * @author Callum Carroll
 */

public class PaymentResponse {
    private boolean success;
    private String transactionId;
    private String message;

    /**
     * Constructs a new {@code PaymentResponse} with the specified success status,
     * transaction ID, and message.
     *
     * @param success       whether the payment was successful
     * @param transactionId the unique identifier for the transaction
     * @param message       additional information about the transaction outcome
     */

    public PaymentResponse(boolean success, String transactionId, String message) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
    }

    /**
     *
     * Basic getters and setters after methods have passed
     *
     */

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}