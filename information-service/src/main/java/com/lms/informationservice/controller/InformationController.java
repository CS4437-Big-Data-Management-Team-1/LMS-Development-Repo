package com.lms.informationservice.controller;

import com.lms.informationservice.matches.Matches;
import com.lms.informationservice.service.InformationService;
import com.lms.informationservice.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/information")
public class InformationController {

    @Autowired
    private final InformationService informationService;

    public InformationController(InformationService informationService) {
        this.informationService = informationService;
    }

    // 1. Fetch and store all teams from external API (GET)
    @GetMapping("/teams/fetch")
    public ResponseEntity<List<Team>> fetchTeams() {
        List<Team> teams = informationService.apiCallGetTeams();
        return ResponseEntity.ok(teams);
    }

    // 2. Fetch and store all fixtures from external API (GET)
    @GetMapping("/fixtures/fetch")
    public ResponseEntity<List<Matches>> fetchFixtures() {
        List<Matches> matches = informationService.apiCallGetFixtures();
        return ResponseEntity.ok(matches);
    }
}

