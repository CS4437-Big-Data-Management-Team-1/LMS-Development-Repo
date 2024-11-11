package com.lms.gameservice.service;

import com.lms.informationservice.matches.Matches;
import com.lms.informationservice.team.Team;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Optional;

@Service
public class InformationServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public InformationServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        // Load environment variables using dotenv
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        baseUrl = dotenv.get("INFORMATION_SERVICE_BASE_URL");
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new RuntimeException("INFORMATION_SERVICE_BASE_URL not set in .env file");
        }
    }

    /**
     * Fetches all teams from the information-service.
     * @return List<Team> containing a list of teams.
     */
    public List<Team> fetchTeams() {
        try {
            ResponseEntity<List<Team>> response = restTemplate.exchange(
                    baseUrl + "/teams/fetch",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Team>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error fetching teams: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Fetches all matches from the information-service.
     * @return List<Matches> containing a list of matches.
     */
    public List<Matches> fetchMatches() {
        try {
            ResponseEntity<List<Matches>> response = restTemplate.exchange(
                    baseUrl + "/matches/fetch",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Matches>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error fetching matches: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Fetches the result of a specific match by its ID.
     * @param matchId The ID of the match to fetch.
     * @return Optional<String> containing the result of the match.
     */
    public Optional<String> getMatchResultById(int matchId) {
        try {
            List<Matches> matches = fetchMatches();
            return matches.stream()
                    .filter(match -> match.getId() == matchId)
                    .findFirst()
                    .map(Matches::getResult);
        } catch (Exception e) {
            System.err.println("Error fetching match result: " + e.getMessage());
            return Optional.empty();
        }
    }
}
