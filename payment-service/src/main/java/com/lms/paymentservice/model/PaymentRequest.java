package com.lms.paymentservice.model;
import java.math.BigDecimal;
/**
 * Model class representing a payment request.
 *
 * This class encapsulates the details required for processing a payment,
 * including the amount, currency, description, and source of the payment.
 *
 * @author Callum Carroll
 */

public class PaymentRequest {
    private BigDecimal amount;
    private String currency;
    private String description;
    private String source;  

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
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

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "amount='" + amount + '\'' +
                ", currency='" + currency + '\'' +
                ", description='" + description + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}