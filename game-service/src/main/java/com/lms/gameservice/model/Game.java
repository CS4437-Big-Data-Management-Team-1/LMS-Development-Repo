package com.lms.gameservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private BigDecimal entryFee;
    private LocalDateTime startDate;
    private String status;
    private BigDecimal totalPot = BigDecimal.ZERO;


    public BigDecimal getTotalPot() {
        return totalPot;
    }

    public void setTotalPot(BigDecimal totalPot) {
        this.totalPot = totalPot;
    }

    // Getters and setters

    public int getId(){
        return id;
    }


    public String getName() {
        return name;
    }
    public void setId(int id){
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(BigDecimal entryFee) {
        this.entryFee = entryFee;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
