package com.lms.gameservice.gamerequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GameRequestDTO {

    private String name;
    private BigDecimal entryFee;
    private LocalDateTime startDate;

    public GameRequestDTO(String name, BigDecimal entryFee, LocalDateTime startDate){
        this.name = name;
        this.entryFee = entryFee;
        this.startDate = startDate;
    }

    public String getName() {
        return name;
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
}