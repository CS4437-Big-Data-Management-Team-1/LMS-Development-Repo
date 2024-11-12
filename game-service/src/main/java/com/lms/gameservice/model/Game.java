package com.lms.gameservice.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    private String name;
    private String status;
    private LocalDateTime startDate;
    private BigDecimal entryFee;

    @OneToMany(mappedBy = "game")
    private List<Round> rounds;

    // Getters and Setters
    public Long getGameId() { return gameId; }

    public void setGameId(Long gameId) { this.gameId = gameId; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartDate() { return startDate; }

    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public BigDecimal getEntryFee() { return entryFee; }

    public void setEntryFee(BigDecimal entryFee) { this.entryFee = entryFee; }

    public List<Round> getRounds() { return rounds; }

    public void setRounds(List<Round> rounds) { this.rounds = rounds; }
}
