package com.lms.paymentservice.model;

/**
 * Represents a response from a payment processing request.
 * Contains information about the success or failure of the payment
 * along with transaction details.
 */

public class PaymentResponse {
    private boolean success;
    private String transactionId;
    private String message;

    /**
     * Constructor to initialize PaymentResponse with payment result details.
     *
     * @param success      true if the payment was successful, false if not
     * @param transactionId the unique identifier for the transaction, or null if unsuccessful
     * @param message      message about the payment result
     */

    public PaymentResponse(boolean success, String transactionId, String message) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
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
}