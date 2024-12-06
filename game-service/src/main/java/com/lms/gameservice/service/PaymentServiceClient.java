package com.lms.gameservice.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
/**
 * Service class for processing payments.
 *
 * This class sends the info related to a payment request (the amount, the gameId and the token) and sends it via http to the payment service, where it is processed
 * Also calls the jwt validator in the user service to assure user is logged in
 *
 * @author Mark Harrison
 */
@Service
public class PaymentServiceClient {

    @Autowired
    private RestTemplate restTemplate;
    /**
     * Service class for processing payments.
     *
     * This class sends the info related to a payment request (the amount, the gameId and the token) and sends it via http to the payment service, where it is processed
     * Also calls the jwt validator in the user service to assure user is logged in
     * @param amount            the amount being paid. Equal to the entry fee.
     * @param gameId            the game id of the lms game being paid for
     * @param token             User's jwt token to ensure login
     * @return boolean          The status of the payment
     * @author Mark Harrison
     */
    public boolean makePayment(BigDecimal amount, int gameId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorisation", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = "{" +
                "\"amount\": \"" + amount.toString() + "\"," +
                "\"currency\": \"EUR\"," +
                "\"description\": \"Joining game " + gameId + " with entry fee of " + amount + "\"," +
                "\"source\": \"tok_visa\"" +

                "}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(

                "http://payment-service:8081/api/payment/process",

                HttpMethod.POST,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return true ;
        } else {
            return false;
        }
    }


    public void updateUserBalance(String userId, double amount) {
        String url = "http://user-service:8080/api/users/" + userId + "/addToBalance"; 

        HttpEntity<Double> entity = new HttpEntity<>(amount);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error updating user balance");
        }
    }
}
