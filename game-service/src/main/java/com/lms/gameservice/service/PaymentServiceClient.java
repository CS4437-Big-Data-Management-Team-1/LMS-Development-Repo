package com.lms.gameservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
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

                "http://localhost:8081/api/payment/process",

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
}
