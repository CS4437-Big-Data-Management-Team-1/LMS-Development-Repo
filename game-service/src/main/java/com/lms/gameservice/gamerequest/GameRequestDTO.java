package com.lms.gameservice.gamerequest;

import java.math.BigDecimal;

public class GameRequestDTO {

    private String name;
    private BigDecimal entryFee;
    private int weeksTillStartDate;

    public GameRequestDTO(){}
    public GameRequestDTO(String name, BigDecimal entryFee, int weeksTillStartDate){
        this.name = name;
        this.entryFee = entryFee;
        this.weeksTillStartDate = weeksTillStartDate;
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

    public int getWeeksTillStartDate() {
        return weeksTillStartDate;
    }

    public void setWeeksTillStartDate(int weeksTillStartDate) {
        this.weeksTillStartDate = weeksTillStartDate;
    }
}