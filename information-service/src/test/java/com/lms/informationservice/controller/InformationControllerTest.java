package com.lms.informationservice.controller;

import com.lms.informationservice.matches.Matches;
import com.lms.informationservice.service.InformationService;
import com.lms.informationservice.team.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the InformationController class
 * @author Caoimhe Cahill
 */
class InformationControllerTest {

    @Mock
    private InformationService informationService;

    @InjectMocks
    private InformationController informationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchTeams() {
        Team team1 = new Team();
        team1.setTeamID(1);
        team1.setTeamName("Team 1");
        team1.setTla("T1");

        Team team2 = new Team();
        team1.setTeamID(2);
        team1.setTeamName("Team 2");
        team1.setTla("T2");

        List<Team> mockTeams = Arrays.asList(team1, team2);

        when(informationService.apiCallGetTeams()).thenReturn(mockTeams);

        ResponseEntity<List<Team>> response = informationController.fetchTeams();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockTeams, response.getBody());
    }

    @Test
    void testFetchMatches() throws ParseException {
        Matches match1 = new Matches();
        match1.setGameID(1);
        match1.setHomeTeamID(123);
        match1.setHomeTeamName("Liverpool");
        match1.setAwayTeamID(456);
        match1.setAwayTeamName("Manchester");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date gameDate = dateFormat.parse("2023-11-01");
        match1.setGameDate(gameDate);

        match1.setResult("Liverpool");

        Matches match2 = new Matches();
        match2.setGameID(2);
        match2.setHomeTeamID(12345);
        match2.setHomeTeamName("Limerick");
        match2.setAwayTeamID(67890);
        match2.setAwayTeamName("Dublin");

        Date gameDate2 = dateFormat.parse("2023-11-02");
        match1.setGameDate(gameDate2);

        match2.setResult("Limerick");

        List<Matches> mockMatches = Arrays.asList(match1, match2);

        when(informationService.apiCallGetMatches()).thenReturn(mockMatches);

        ResponseEntity<List<Matches>> response = informationController.fetchMatches();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockMatches, response.getBody());
    }
}

