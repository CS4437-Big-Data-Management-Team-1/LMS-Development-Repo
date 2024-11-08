package com.lms.paymentservice.model;

/**
 * Represents a request for processing a payment.
 * Contains information required by the payment gateway.
 */

public class PaymentRequest {
    private String amount;
    private String currency;
    private String description;
    // token representing the payment method
    private String source;  

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}