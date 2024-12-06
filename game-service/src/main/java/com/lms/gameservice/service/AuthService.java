package com.lms.gameservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;

    public String validateToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorisation", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(

                "http://user-service:8080/api/users/validate-jwt",

                HttpMethod.POST,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Invalid or expired token");
        }
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Return isAdmin field from user-service
    public boolean getUserAdminStatus(String token, String userId) {
        String url = "http://user-service:8080/api/users/" + userId;  // Replace with your endpoint

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorisation", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            // Parse response to get only the 'isAdmin' field
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("isAdmin").asBoolean(false);  // Default to false if isAdmin is not found
        } catch (Exception e) {
            throw new RuntimeException("Error parsing user admin status: " + e.getMessage(), e);
        }
    }

}
