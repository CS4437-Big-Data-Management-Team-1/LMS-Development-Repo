package com.lms.informationservice.service;

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

@Service
public class InformationService {

    @Autowired
    private final TeamRepository teamRepository;

    @Autowired
    private MatchesRepository matchesRepository;


    private final String BASE_URL = "https://api.football-data.org/v4/competitions/PL";
    private final String API_TOKEN = "8e72a89f030d4e7782991ae42fdb8192";

    private WebClient webClient;

    public InformationService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Auth-Token", API_TOKEN)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(512 * 1024))
                .build();
    }

    // 1. Fetches teams from an external API and saves them in the database
    @Scheduled(cron = "0 0 0 * * SAT")
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

        //Save the extracted teams to the database
        if (teamList != null && !teamList.isEmpty()) {
            teamRepository.saveAll(teamList);
        }
        return teamRepository.findAll();
    }



    // 2. Fetches matches for the season and saves them in the database
    @Scheduled(cron = "0 0 0 * * SAT")
    public List<Matches> apiCallGetMatches(){
        String url = "/matches";

        List<Matches> matchesList = this.webClient.get()
                .uri("/matches")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(responseBody -> {
                    if (responseBody != null && responseBody.containsKey("matches")) {

                        // Extract the Matches array from the response
                        List<Map<String, Object>> matches = (List<Map<String, Object>>) responseBody.get("matches");

                        // Create a list to hold the Matches entities
                        List<Matches> matchesData = new ArrayList<>();

                        // Iterate over the Matches array
                        for (Map<String, Object> matchData : matches) {

                            Integer matchId = (Integer) matchData.get("id");

                            Map<String, Object> homeTeam = (Map<String, Object>) matchData.get("homeTeam");
                            Integer homeTeamId = (Integer) homeTeam.get("id");
                            String homeTeamName = (String) homeTeam.get("name");

                            Map<String, Object> awayTeam = (Map<String, Object>) matchData.get("awayTeam");
                            Integer awayTeamId = (Integer) awayTeam.get("id");
                            String awayTeamName = (String) awayTeam.get("name");

                            // Extract the game date and convert it to Date format
                            String utcDateStr = (String) matchData.get("utcDate");
                            Date gameDate = Date.from(Instant.parse(utcDateStr));

                            // Determine the winner from the score object
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
                            match.setAwayTeamID(awayTeamId);
                            match.setGameDate(gameDate);
                            match.setResult(result);

                            matchesData.add(match);
                        }
                        return matchesData;
                    }
                    return new ArrayList<Matches>();
                })
                .block();

        // Save all the fixtures to the database
        if (matchesList != null && !matchesList.isEmpty()) {
            matchesRepository.saveAll(matchesList);
        }
        return matchesRepository.findAll();
    }

}

