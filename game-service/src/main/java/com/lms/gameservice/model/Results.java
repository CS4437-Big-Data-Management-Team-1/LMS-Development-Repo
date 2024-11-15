package com.lms.gameservice.model;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.*;

@Entity
@Table(name = "results")
public class Results {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // You can adjust the generation strategy if needed
    private Long id;
    
    @Lob
    @Column(name = "winners")
    private String winners;

    private static final ObjectMapper objectMapper = new ObjectMapper();


    public ArrayList<String> getWinners() {
        try {
            return objectMapper.readValue(winners, ArrayList.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void setWinners(ArrayList<String> winners) {
        try {
            this.winners = objectMapper.writeValueAsString(winners);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}