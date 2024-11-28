package com.lms.gameservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.lms.gameservice.service.PaymentServiceClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentServiceClient paymentServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void makePayment_successfulResponse_returnsTrue() {
        // Arrange
        BigDecimal amount = new BigDecimal("10.00");
        int gameId = 123;
        String token = "valid_jwt_token";
        String paymentServiceUrl = "http://localhost:8081/api/payment/process";

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

        ResponseEntity<String> mockResponse = new ResponseEntity<>("Payment processed successfully", HttpStatus.OK);

        when(restTemplate.exchange(
                eq(paymentServiceUrl),
                eq(HttpMethod.POST),
                eq(entity),
                eq(String.class)
        )).thenReturn(mockResponse);

        // Act
        boolean result = paymentServiceClient.makePayment(amount, gameId, token);

        // Assert
        assertTrue(result);
        verify(restTemplate, times(1)).exchange(eq(paymentServiceUrl), eq(HttpMethod.POST), eq(entity), eq(String.class));
    }

    @Test
    void makePayment_failedResponse_returnsFalse() {
        // Arrange
        BigDecimal amount = new BigDecimal("10.00");
        int gameId = 123;
        String token = "valid_jwt_token";
        String paymentServiceUrl = "http://localhost:8081/api/payment/process";

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

        ResponseEntity<String> mockResponse = new ResponseEntity<>("Payment failed", HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                eq(paymentServiceUrl),
                eq(HttpMethod.POST),
                eq(entity),
                eq(String.class)
        )).thenReturn(mockResponse);

        // Act
        boolean result = paymentServiceClient.makePayment(amount, gameId, token);

        // Assert
        assertFalse(result);
        verify(restTemplate, times(1)).exchange(eq(paymentServiceUrl), eq(HttpMethod.POST), eq(entity), eq(String.class));
    }
}