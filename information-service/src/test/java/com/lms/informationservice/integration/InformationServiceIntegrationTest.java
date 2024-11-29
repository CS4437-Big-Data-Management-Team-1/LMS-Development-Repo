package com.lms.informationservice.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.lms.informationservice.matches.Matches;
import com.lms.informationservice.service.InformationService;
import com.lms.informationservice.team.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class InformationServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InformationService informationService;

    @LocalServerPort
    private int port;

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Configure H2 in-memory database
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MYSQL");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");

        // Configure mock API endpoints
        registry.add("FOOTBALL_API_BASE_URL", () -> "http://localhost:" + wireMock.getPort());
        registry.add("FOOTBALL_API_TOKEN", () -> "test-token");
    }

    @BeforeEach
    void setupMocks() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/teams"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"teams\": [{ \"id\": 1, \"name\": \"Team A\", \"tla\": \"TA\" }] }")));

        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/matches"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"matches\": [{ \"id\": 101, \"homeTeam\": {\"id\": 1, \"name\": \"Team A\"}, \"awayTeam\": {\"id\": 2, \"name\": \"Team B\"}, \"utcDate\": \"2023-11-29T12:00:00Z\", \"score\": {\"winner\": \"HOME_TEAM\"} }] }")));
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

    @Test
    void testApiCallGetTeams() {
        List<Team> teams = informationService.apiCallGetTeams();
        assertFalse(teams.isEmpty());
    }

    @Test
    void testApiCallGetMatches() {
        List<Matches> matches = informationService.apiCallGetMatches();
        assertFalse(matches.isEmpty());
    }
}
