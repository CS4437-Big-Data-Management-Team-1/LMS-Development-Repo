package com.lms.gameservice.service;

import com.lms.gameservice.model.Round;
import com.lms.gameservice.repository.PlayerPickRepository;
import com.lms.gameservice.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoundService {
    private final PlayerRepository playerRepository;
    private final PlayerPickRepository playerPickRepository;

    @Autowired
    public RoundService(PlayerRepository playerRepository, PlayerPickRepository playerPickRepository) {
        this.playerRepository = playerRepository;
        this.playerPickRepository = playerPickRepository;
    }

    public void processRoundResults(Round round, List<Long> winningTeamIds) {
        // TODO find game that are active , process if they through to next round, process to kick them out


        }
    }

