package com.lms.gameservice.service;

import com.lms.paymentservice.model.PaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class PaymentServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentServiceClient paymentServiceClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessPayment_Success() {
        // Arrange: Set up a mock PaymentRequest and response
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount("1000");
        paymentRequest.setCurrency("usd");
        paymentRequest.setDescription("Test Payment");
        paymentRequest.setSource("tok_visa");

        String mockToken = "mock_token";

        // Mock successful response
        when(restTemplate.exchange(
                eq("http://localhost:8080/api/payment/process"),
                eq(HttpMethod.POST),
                any(),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>("Payment successful!", HttpStatus.OK));

        // Act: Call the processPayment method
        String response = paymentServiceClient.processPayment(paymentRequest, mockToken);

        // Assert: Check that the response is as expected
        assertEquals("Payment successful!", response);
    }

    @Test
    public void testProcessPayment_Failure() {
        // Arrange: Set up a mock PaymentRequest
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount("1000");
        paymentRequest.setCurrency("usd");
        paymentRequest.setDescription("Test Payment");
        paymentRequest.setSource("tok_visa");

        String mockToken = "mock_token";

        // Mock a failed response
        when(restTemplate.exchange(
                eq("http://localhost:8080/api/payment/process"),
                eq(HttpMethod.POST),
                any(),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>("Payment failed", HttpStatus.BAD_REQUEST));

        // Act & Assert: Check that an exception is thrown for failed payment
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentServiceClient.processPayment(paymentRequest, mockToken);
        });

        assertEquals("Payment processing failed", exception.getMessage());
    }
}