package com.lms.informationservice.service;

import com.lms.informationservice.matches.Matches;
import com.lms.informationservice.repository.MatchesRepository;
import com.lms.informationservice.repository.TeamRepository;
import com.lms.informationservice.service.InformationService;
import com.lms.informationservice.team.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Unit tests for the InformationService class
 * @author Caoimhe Cahill
 */
class InformationServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private MatchesRepository matchesRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private ResponseSpec responseSpec;

    @InjectMocks
    private InformationService informationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void testApiCallGetTeams_SuccessfulResponse() {
        // Mocking response data
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("teams", List.of(
                Map.of("id", 1, "name", "Team A", "tla", "TA"),
                Map.of("id", 2, "name", "Team B", "tla", "TB")
        ));

        // WebClient Mock
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri("/teams");
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Mono.just(mockResponse)).when(responseSpec)
                .bodyToMono(any(ParameterizedTypeReference.class));

        // Creating instances of Team
        Team team1 = new Team();
        team1.setTeamID(1);
        team1.setTeamName("Team A");
        team1.setTla("TA");

        Team team2 = new Team();
        team2.setTeamID(2);
        team2.setTeamName("Team B");
        team2.setTla("TB");

        List<Team> savedTeams = List.of(team1, team2);

        // Mock repository behavior
        when(teamRepository.saveAll(anyList())).thenReturn(savedTeams);
        when(teamRepository.findAll()).thenReturn(savedTeams);

        // Call the method
        List<Team> teams = informationService.apiCallGetTeams();

        assertEquals(2, teams.size());
        verify(teamRepository, times(1)).saveAll(anyList());
        verify(teamRepository, times(1)).findAll();
    }

    @Test
    void testApiCallGetMatches_SuccessfulResponse() {
        // Mocking response data
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("matches", List.of(
                Map.of(
                        "id", 1,
                        "homeTeam", Map.of("id", 1, "name", "Home Team A"),
                        "awayTeam", Map.of("id", 2, "name", "Away Team B"),
                        "utcDate", "2023-11-07T20:00:00Z",
                        "score", Map.of("winner", "HOME_TEAM")
                )
        ));

        // Mock WebClient
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri("/matches");
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Mono.just(mockResponse)).when(responseSpec)
                .bodyToMono(any(ParameterizedTypeReference.class));

        // Creating an instance of Matches
        Matches match = new Matches();
        match.setGameID(1);
        match.setHomeTeamID(1);
        match.setHomeTeamName("Home Team A");
        match.setAwayTeamID(2);
        match.setAwayTeamName("Away Team B");
        match.setGameDate(Date.from(Instant.parse("2023-11-07T20:00:00Z")));
        match.setResult("Home Team A");

        List<Matches> savedMatches = List.of(match);

        // Mock repository behavior
        when(matchesRepository.saveAll(anyList())).thenReturn(savedMatches);
        when(matchesRepository.findAll()).thenReturn(savedMatches);

        // Call the method
        List<Matches> matches = informationService.apiCallGetMatches();

        assertEquals(1, matches.size()); // Expect 1 match saved
        assertEquals("Home Team A", matches.get(0).getResult());
        verify(matchesRepository, times(1)).saveAll(anyList());
        verify(matchesRepository, times(1)).findAll();
    }
}