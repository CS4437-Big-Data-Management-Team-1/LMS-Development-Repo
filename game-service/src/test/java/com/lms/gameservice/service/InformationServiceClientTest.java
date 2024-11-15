package com.lms.gameservice.service;

import com.lms.gameservice.matches.MatchesDTO;
import com.lms.gameservice.team.TeamDTO;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Unit tests for the InformationServiceClient class
 * @author Caoimhe Cahill
 */
@ExtendWith(MockitoExtension.class)
class InformationServiceClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    private InformationServiceClient informationServiceClient;

    @BeforeEach
    void setUp() {
        // Load the environment variables from the test .env file
        Dotenv dotenv = Dotenv.configure()
                .directory("src/test/resources")
                .filename(".env")
                .ignoreIfMissing()
                .load();

        String baseUrl = dotenv.get("INFORMATION_SERVICE_BASE_URL");
        System.setProperty("INFORMATION_SERVICE_BASE_URL", baseUrl);

        // Initialize RestTemplate
        restTemplate = new RestTemplate();

        // Spy on InformationServiceClient
        informationServiceClient = Mockito.spy(new InformationServiceClient(restTemplate));

        // Inject the baseUrl using ReflectionTestUtils
        ReflectionTestUtils.setField(informationServiceClient, "baseUrl", baseUrl);

        // Set up MockRestServiceServer
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }


    @Test
    void testFetchMatches() {
        String fakeResponse = "[{\"gameID\":1, \"result\":\"Team A won\"}, {\"gameID\":2, \"result\":\"Team B won\"}]";

        // Mock the endpoint response
        mockServer.expect(requestTo("http://mock-api.com/matches/fetch"))
                .andRespond(withSuccess(fakeResponse, org.springframework.http.MediaType.APPLICATION_JSON));

        List<MatchesDTO> result = informationServiceClient.fetchMatches();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Team A won", result.get(0).getResult());
    }

    @Test
    void testFetchTeams() {
        String fakeResponse = "[{\"teamID\":1, \"teamName\":\"Team A\", \"tla\":\"TLA\"}, {\"teamID\":2, \"teamName\":\"Team B\", \"tla\":\"TLB\"}]";

        // Mock the endpoint response
        mockServer.expect(requestTo("http://mock-api.com/teams/fetch"))
                .andRespond(withSuccess(fakeResponse, org.springframework.http.MediaType.APPLICATION_JSON));

        List<TeamDTO> result = informationServiceClient.fetchTeams();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Team A", result.get(0).getTeamName());
    }

    @Test
    void testFetchTeamsFromDatabase() {
        // Arrange
        String fakeResponse = "[{\"teamID\":1, \"teamName\":\"Team A\", \"tla\":\"TLA\"}, {\"teamID\":2, \"teamName\":\"Team B\", \"tla\":\"TLB\"}]";

        mockServer.expect(requestTo("http://mock-api.com/teams/get-teams"))
                .andRespond(withSuccess(fakeResponse, org.springframework.http.MediaType.APPLICATION_JSON));

        List<TeamDTO> result = informationServiceClient.getTeamsFromDatabase();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Team A", result.get(0).getTeamName());
        assertEquals("TLA", result.get(0).getTla());
    }

    @Test
    void testFetchMatchesFromDate() throws ParseException {
        String fakeResponse = "[{\"gameID\":1, \"result\":\"Team A won\", \"gameDate\":\"2024-11-10T00:00:00Z\"},"
                + "{\"gameID\":2, \"result\":\"Team B won\", \"gameDate\":\"2024-11-15T00:00:00Z\"}]";

        mockServer.expect(requestTo("http://mock-api.com/matches/fetch"))
                .andRespond(withSuccess(fakeResponse, org.springframework.http.MediaType.APPLICATION_JSON));

        String filterDate = "2024-11-12";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(filterDate);

        List<MatchesDTO> result = informationServiceClient.fetchMatchesFromDate(filterDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Team B won", result.get(0).getResult());
        assertTrue(result.get(0).getGameDate().after(date));
    }

    @Test
    void testGetMatchResultById() {
        String fakeResponse = "[{\"gameID\":123, \"result\":\"Team A won\"}]";

        mockServer.expect(requestTo("http://mock-api.com/matches/fetch"))
                .andRespond(withSuccess(fakeResponse, org.springframework.http.MediaType.APPLICATION_JSON));

        Optional<String> result = informationServiceClient.getMatchResultById(123);

        assertTrue(result.isPresent());
        assertEquals("Team A won", result.get());
    }

    @Test
    void testFetchTeams_HandlesException() {
        mockServer.expect(requestTo("http://mock-api.com/teams/fetch"))
                .andRespond(withServerError());

        List<TeamDTO> result = informationServiceClient.fetchTeams();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTeamsFromDatabase_HandlesException() {
        mockServer.expect(requestTo("http://mock-api.com/teams/get-teams"))
                .andRespond(withServerError());

        List<TeamDTO> result = informationServiceClient.getTeamsFromDatabase();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchMatches_HandlesException() {
        mockServer.expect(requestTo("http://mock-api.com/matches/fetch"))
                .andRespond(withServerError());

        List<MatchesDTO> result = informationServiceClient.fetchMatches();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchMatchesFromDate_HandlesException() {
        mockServer.expect(requestTo("http://mock-api.com/matches/fetch"))
                .andRespond(withServerError());

        List<MatchesDTO> result = informationServiceClient.fetchMatchesFromDate("2024-11-12");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchMatchesFromDate_InvalidDateFormat() {
        List<MatchesDTO> result = informationServiceClient.fetchMatchesFromDate("invalid-date");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetMatchResultById_HandlesException() {
        mockServer.expect(requestTo("http://mock-api.com/matches/fetch"))
                .andRespond(withServerError());

        Optional<String> result = informationServiceClient.getMatchResultById(123);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetMatchResultById_NotFound() {
        String fakeResponse = "[]";

        mockServer.expect(requestTo("http://mock-api.com/matches/fetch"))
                .andRespond(withSuccess(fakeResponse, org.springframework.http.MediaType.APPLICATION_JSON));

        Optional<String> result = informationServiceClient.getMatchResultById(999);

        assertFalse(result.isPresent());
    }
}
