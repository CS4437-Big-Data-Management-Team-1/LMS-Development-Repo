package com.lms.informationservice.service;

import com.lms.informationservice.fixture.Fixture;
import com.lms.informationservice.model.Information;
import com.lms.informationservice.repository.FixtureRepository;
import com.lms.informationservice.repository.InformationRepository;
import com.lms.informationservice.repository.StandingRepository;
import com.lms.informationservice.repository.TeamRepository;
import com.lms.informationservice.standing.Standing;
import com.lms.informationservice.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InformationService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private FixtureRepository fixtureRepository;

    @Autowired
    private StandingRepository standingRepository;

    @Autowired
    private InformationRepository informationRepository;

    private final String BASE_URL = "https://api.football-data.org/v4/competitions/PL";
    private final String API_TOKEN = "8e72a89f030d4e7782991ae42fdb8192";
    private RestTemplate restTemplate = new RestTemplate();

    // 1. Fetches teams from an external API and saves them in the database
    public List<Team> apiCallGetTeams() {
        String url = BASE_URL + "/teams";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", API_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("teams")) {
            List<Map<String, Object>> teams = (List<Map<String, Object>>) responseBody.get("teams");

            List<Team> teamList = new ArrayList<>();
            for (Map<String, Object> teamData : teams) {
                Team team = new Team();
                team.setTeamID((Integer) teamData.get("id"));
                team.setTeamName((String) teamData.get("name"));
                team.setTla((String) teamData.get("tla"));
                team.setTeamColour((String) teamData.get("clubColors"));
                team.setTeamLogo((String) teamData.get("crest"));

                teamList.add(team);
            }

            // Save the extracted teams to the database
            teamRepository.saveAll(teamList);
        }
        return teamRepository.findAll();
    }


    // 2. Fetches fixtures for the season and saves them in the database
    public List<Fixture> apiCallGetFixtures() {
        String url = BASE_URL + "/matches";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", API_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("matches")) {
            // Extract the matches array from the response
            List<Map<String, Object>> matches = (List<Map<String, Object>>) responseBody.get("matches");

            // Create a list to hold the Fixture entities
            List<Fixture> fixtureList = new ArrayList<>();

            // Iterate over the matches array
            for (Map<String, Object> matchData : matches) {
                // Extract match id
                Integer matchId = (Integer) matchData.get("id");

                // Extract home team id
                Map<String, Object> homeTeam = (Map<String, Object>) matchData.get("homeTeam");
                Integer homeTeamId = (Integer) homeTeam.get("id");

                // Extract away team id
                Map<String, Object> awayTeam = (Map<String, Object>) matchData.get("awayTeam");
                Integer awayTeamId = (Integer) awayTeam.get("id");

                // Create a new Fixture entity and set its properties
                Fixture fixture = new Fixture();
                fixture.setId(matchId);
                fixture.setHomeTeamID(homeTeamId);
                fixture.setAwayTeamID(awayTeamId);

                // Add the fixture to the list
                fixtureList.add(fixture);
            }

            // Save all the fixtures to the database
            fixtureRepository.saveAll(fixtureList);
        }

        return fixtureRepository.findAll();
    }


}

