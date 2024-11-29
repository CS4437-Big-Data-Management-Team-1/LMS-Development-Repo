package com.lms.informationservice.integration;

import com.lms.informationservice.repository.MatchesRepository;
import com.lms.informationservice.repository.TeamRepository;
import com.lms.informationservice.service.InformationService;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for User Service
 * @author Caoimhe Cahill
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class InformationServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InformationService informationService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchesRepository matchesRepository;

    @BeforeAll
    static void validateEnvironment() {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("FOOTBALL_API_BASE_URL", dotenv.get("FOOTBALL_API_BASE_URL"));
        System.setProperty("FOOTBALL_API_TOKEN", dotenv.get("FOOTBALL_API_TOKEN"));
    }

    @Test
    void testFetchTeamsEndpoint() throws Exception {
        mockMvc.perform(get("/api/information/teams/fetch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));
    }

    @Test
    void testGetTeamsEndpoint() throws Exception {
        mockMvc.perform(get("/api/information/teams/get-teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));
    }

    @Test
    void testFetchMatchesEndpoint() throws Exception {
        mockMvc.perform(get("/api/information/matches/fetch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));
    }

}
