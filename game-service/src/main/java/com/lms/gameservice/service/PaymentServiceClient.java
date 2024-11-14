package com.lms.gameservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;

@Service
public class PaymentServiceClient {

    @Autowired
    private RestTemplate restTemplate;

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
