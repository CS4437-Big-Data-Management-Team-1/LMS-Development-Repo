package com.lms.paymentservice.model;

/**
 * Model class representing a payment request.
 *
 * This class encapsulates the details required for processing a payment,
 * including the amount, currency, description, and source of the payment.
 *
 * Each field has corresponding getter and setter methods.
 *
 * @author Callum Carroll
 */


public class PaymentRequest {
    private String amount;
    private String currency;
    private String description;
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