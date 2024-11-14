package com.lms.paymentservice.model;

import java.math.BigDecimal ;
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
    private BigDecimal amount;

    /**
     * Constructs a new {@code PaymentResponse} with the specified success status,
     * transaction ID, and message.
     *
     * @param success       whether the payment was successful
     * @param transactionId the unique identifier for the transaction
     * @param message       additional information about the transaction outcome
     * @param amount        amount of payment
     */

    public PaymentResponse(boolean success, String transactionId, String message, BigDecimal amount) {


        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
        this.amount = amount;
    }

    /**
     * Basic getters and setters after methods have passed
     */

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
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    @Override
    public String toString() {
        return "PaymentResponse{" +
                "success=" + success +
                ", transactionId='" + transactionId + '\'' +
                ", message='" + message + '\'' +
                ", amount='" + String.valueOf(amount) + '\'' +
                '}';
    }
}