package com.lms.gameservice.service;

import com.lms.informationservice.matches.Matches;
import com.lms.informationservice.team.Team;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for communicating with the information-service.
 *
 * @author Caoimhe Cahill
 */
@Service
public class InformationServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public InformationServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        baseUrl = dotenv.get("INFORMATION_SERVICE_BASE_URL");
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new RuntimeException("INFORMATION_SERVICE_BASE_URL not set in .env file");
        }
    }

    /**
     * Retrieves all teams from the information-service.
     * @return List<Team> containing a list of teams.
     */
    public List<Team> fetchTeams() {
        String url = baseUrl + "/teams/fetch";
        try {
            ResponseEntity<List<Team>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Team>>() {}
            );
            return response.getBody() != null ? response.getBody() : List.of();
        } catch (Exception e) {
            System.err.println("Error retrieving teams: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Retrieve all teams from the database via the information-service.
     * This does not interact with the external API.
     *
     * @return List<Team> containing teams stored in the database.
     */
    public List<Team> getTeamsFromDatabase() {
        try {
            ResponseEntity<List<Team>> response = restTemplate.exchange(
                    baseUrl + "/teams/get-teams",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Team>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error retrieving teams from database: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Retrieves all matches from the information-service.
     * @return List<Matches> containing a list of matches.
     */
    public List<Matches> fetchMatches() {
        String url = baseUrl + "/matches/fetch";
        try {
            ResponseEntity<List<Matches>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Matches>>() {}
            );
            return response.getBody() != null ? response.getBody() : List.of();
        } catch (Exception e) {
            System.err.println("Error retrieving matches: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Retrieves matches from a specific start date onward.
     * @param startDate The start date to filter matches.
     * @return List<Matches> containing matches from the start date onward.
     */
    public List<Matches> fetchMatchesFromDate(String startDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date filterDate = dateFormat.parse(startDate);

            List<Matches> allMatches = fetchMatches();

            return allMatches.stream()
                    .filter(match -> match.getGameDate() != null && !match.getGameDate().before(filterDate))
                    .collect(Collectors.toList());
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Retrieves the result of a specific match by its ID.
     * @param matchId The ID of the match to get.
     * @return Optional<String> containing the result of the match.
     */
    public Optional<String> getMatchResultById(int matchId) {
        try {
            List<Matches> matches = fetchMatches();
            return matches.stream()
                    .filter(match -> match.getGameID() == matchId)
                    .findFirst()
                    .map(Matches::getResult);
        } catch (Exception e) {
            System.err.println("Error retrieving match result: " + e.getMessage());
            return Optional.empty();
        }
    }
}
