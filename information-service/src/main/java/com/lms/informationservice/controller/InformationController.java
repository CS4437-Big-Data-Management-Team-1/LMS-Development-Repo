package com.lms.informationservice.controller;

import com.lms.informationservice.matches.Matches;
import com.lms.informationservice.service.InformationService;
import com.lms.informationservice.team.Team;
import com.lms.informationservice.database.InformationDatabaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing information retrieval and storage.
 * This class provides endpoints to manually trigger the fetching and saving of team and match data
 * from an external API.
 *
 * Each endpoint interacts with the {@link InformationService} to perform the actual data retrieval
 * and saving operations.
 *
 * @author  Caoimhe Cahill
 */

@RestController
@RequestMapping("/api/information")
public class InformationController {
    InformationDatabaseController db = new InformationDatabaseController();

    @Autowired
    private final InformationService informationService;

    /**
     * Constructor for InformationController.
     * Initialises the InformationService for handling data retrieval.
     *
     * @param informationService Service for handling team and match data operations.
     */
    public InformationController(InformationService informationService) {
        db.connectToDB();
        this.informationService = informationService;
    }

    /**
     * Endpoint to fetch and store all teams from the external API.
     *
     * Retrieve teams from the API, storing the retrieved teams in the database, and returning a list of all teams.
     *
     * @return ResponseEntity<List<Team>> Response entity containing a list of all teams in the API.
     */
    @GetMapping("/teams/fetch")
    public ResponseEntity<List<Team>> fetchTeams() {
        List<Team> teams = informationService.apiCallGetTeams();
        for(Team t : teams) {
            db.addTeamsToDB(t);
        }
        return ResponseEntity.ok(teams);
    }

    /**
     * Endpoint to get all team data from the database and provide it to the user
     * It differs from the fetch endpoint in that it does not interact with the API, only the database.
     * This'll be called whenever a user wants to see information about teams in the league etc
     * while the fetch method will be called to update the teams ranking at the start of and during the course of a league
     *
     *
     * @return ResponseEntity<List<Teams>> Response entity containing a list of all teams in the database.
     */
    @GetMapping("/teams/get-teams")
    public ResponseEntity<List<Team>> getTeams(){
        List<Team> teams = db.getTeamsFromDB();
        return ResponseEntity.ok(teams);
    }
    /**
     * Endpoint to fetch and store all match fixtures from the external API.
     *
     * Retrieve matches from the API, storing the retrieved matches in the database, and returning a list of all matches.
     *
     * @return ResponseEntity<List<Matches>> Response entity containing a list of all matches in the database.
     */
    @GetMapping("/matches/fetch")
    public ResponseEntity<List<Matches>> fetchMatches() {
        List<Matches> matches = informationService.apiCallGetMatches();
        return ResponseEntity.ok(matches);
    }
}

