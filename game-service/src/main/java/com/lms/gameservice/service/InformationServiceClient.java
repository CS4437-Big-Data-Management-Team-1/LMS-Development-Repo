package com.lms.gameservice.service;

import com.lms.gameservice.matches.MatchesDTO;
import com.lms.gameservice.team.TeamDTO;
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
     * @return List<TeamDTO> containing a list of teams.
     */
    public List<TeamDTO> fetchTeams() {
        System.out.println("Base URL: " + baseUrl);
        String url = baseUrl + "/teams/fetch";
        try {
            ResponseEntity<List<TeamDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<TeamDTO>>() {}
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
     * @return List<TeamDTO> containing teams stored in the database.
     */
    public List<TeamDTO> getTeamsFromDatabase() {
        try {
            ResponseEntity<List<TeamDTO>> response = restTemplate.exchange(
                    baseUrl + "/teams/get-teams",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<TeamDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error retrieving teams from database: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Retrieves all matches from the information-service.
     * @return List<MatchesDTO> containing a list of matches.
     */
    public List<MatchesDTO> fetchMatches() {
        String url = baseUrl + "/matches/fetch";
        try {
            ResponseEntity<List<MatchesDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<MatchesDTO>>() {}
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
     * @return List<MatchesDTO> containing matches from the start date onward.
     */
    public List<MatchesDTO> fetchMatchesFromDate(String startDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date filterDate = dateFormat.parse(startDate);

            List<MatchesDTO> allMatches = fetchMatches();

            return allMatches.stream()
                    .filter(match -> match.getGameDate() != null && !match.getGameDate().before(filterDate))
                    .collect(Collectors.toList());
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Retrieves matches within a specific date range.
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @return List<MatchesDTO> containing matches within the date range.
     */
    public List<MatchesDTO> fetchMatchesWithinDateRange(String startDate, String endDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date filterStartDate = dateFormat.parse(startDate);
            Date filterEndDate = dateFormat.parse(endDate);
    
            List<MatchesDTO> allMatches = fetchMatches();
    
            return allMatches.stream()
                    .filter(match -> {
                        Date gameDate = match.getGameDate();
                        return gameDate != null && !gameDate.before(filterStartDate) && !gameDate.after(filterEndDate);
                    })
                    .collect(Collectors.toList());
        } catch (ParseException e) {
            System.err.println("Error parsing dates: " + e.getMessage());
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
            List<MatchesDTO> matches = fetchMatches();
            return matches.stream()
                    .filter(match -> match.getGameID() == matchId)
                    .findFirst()
                    .map(MatchesDTO::getResult);
        } catch (Exception e) {
            System.err.println("Error retrieving match result: " + e.getMessage());
            return Optional.empty();
        }
    }
}
