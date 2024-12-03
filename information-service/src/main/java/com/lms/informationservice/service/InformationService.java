package com.lms.informationservice.service;

import io.github.cdimascio.dotenv.Dotenv;
import com.lms.informationservice.matches.Matches;
import com.lms.informationservice.repository.MatchesRepository;
import com.lms.informationservice.repository.TeamRepository;
import com.lms.informationservice.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing external-api operations and calls.
 * This class provides methods for retrieving teams and matches from an external api
 * (https://www.football-data.org/)
 * All methods in this class delegate database operations to the {@link TeamRepository} and the {@link MatchesRepository}.
 *
 * @author Caoimhe Cahill
 */

@Service
public class InformationService {

    @Autowired
    private final TeamRepository teamRepository;

    @Autowired
    private final MatchesRepository matchesRepository;

    // WebClient instance for making HTTP calls to the external API
    private final WebClient webClient;

    /**
     * Constructor for InformationService.
     * Initialises the TeamRepository & MatchesRepository, sets up WebClient with environment variables for
     * the API base URL and authentication token.
     *
     * @param teamRepository Repository for handling team data in the database.
     */
    public InformationService(TeamRepository teamRepository, MatchesRepository matchesRepository) {
        this.teamRepository = teamRepository;
        this.matchesRepository = matchesRepository;
        Dotenv dotenv = Dotenv.load();
        this.webClient = WebClient.builder()
                .baseUrl("FOOTBALL_API_BASE_URL")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Auth-Token", "FOOTBALL_API_TOKEN")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(512 * 1024))
                .build();
    }

    /**
     * Scheduled method to retrieve and save teams from the external API.
     * Runs every day at midnight.
     *
     * This method fetches data from the "/teams" endpoint, maps the response to Team objects,
     * and saves them in the database. The method returns a list of all teams after updating.
     *
     * @return List<Team> List of all teams in the database.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public List<Team> apiCallGetTeams() {
        String url = "/teams";

        List<Team> teamList = this.webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(responseBody -> {
                    if (responseBody != null && responseBody.containsKey("teams")) {
                        List<Map<String, Object>> teams = (List<Map<String, Object>>) responseBody.get("teams");

                        List<Team> teamsData = new ArrayList<>();
                        for (Map<String, Object> teamData : teams) {
                            Team team = new Team();
                            team.setTeamID((Integer) teamData.get("id"));
                            team.setTeamName((String) teamData.get("name"));
                            team.setTla((String) teamData.get("tla"));

                            teamsData.add(team);
                        }
                        return teamsData;
                    }
                    return new ArrayList<Team>();
                })
                .block();

        if (teamList != null && !teamList.isEmpty()) {
            teamRepository.saveAll(teamList);
        }
        return teamRepository.findAll();
    }



    /**
     * Scheduled method to retrieve and save matches from the external API.
     * Runs every day at midnight.
     *
     * This method fetches data from the "/matches" endpoint, maps the response to Matches objects,
     * and saves them in the database. The method returns a list of all matches after updating.
     *
     * @return List<Matches> List of all matches in the database.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public List<Matches> apiCallGetMatches(){
        String url = "/matches";

        List<Matches> matchesList = this.webClient.get()
                .uri("/matches")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(responseBody -> {
                    if (responseBody != null && responseBody.containsKey("matches")) {

                        List<Map<String, Object>> matches = (List<Map<String, Object>>) responseBody.get("matches");
                        List<Matches> matchesData = new ArrayList<>();

                        for (Map<String, Object> matchData : matches) {

                            Integer matchId = (Integer) matchData.get("id");

                            Map<String, Object> homeTeam = (Map<String, Object>) matchData.get("homeTeam");
                            Integer homeTeamId = (Integer) homeTeam.get("id");
                            String homeTeamName = (String) homeTeam.get("name");

                            Map<String, Object> awayTeam = (Map<String, Object>) matchData.get("awayTeam");
                            Integer awayTeamId = (Integer) awayTeam.get("id");
                            String awayTeamName = (String) awayTeam.get("name");

                            String utcDateStr = (String) matchData.get("utcDate");
                            Date gameDate = Date.from(Instant.parse(utcDateStr));

                            Map<String, Object> score = (Map<String, Object>) matchData.get("score");
                            String winner = (String) score.get("winner");

                            String result;
                            if ("HOME_TEAM".equals(winner)) {
                                result = homeTeamName;
                            } else if ("AWAY_TEAM".equals(winner)) {
                                result = awayTeamName;
                            } else {
                                result = "Draw";
                            }

                            Matches match = new Matches();
                            match.setGameID(matchId);
                            match.setHomeTeamID(homeTeamId);
                            match.setHomeTeamName(homeTeamName);
                            match.setAwayTeamID(awayTeamId);
                            match.setAwayTeamName(awayTeamName);
                            match.setGameDate(gameDate);
                            match.setResult(result);

                            matchesData.add(match);
                        }
                        return matchesData;
                    }
                    return new ArrayList<Matches>();
                })
                .block();

        if (matchesList != null && !matchesList.isEmpty()) {
            matchesRepository.saveAll(matchesList);
        }
        return matchesRepository.findAll();
    }

}

