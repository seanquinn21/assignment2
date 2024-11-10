package com.example.musicFinder.Controller;

import com.example.musicFinder.MusicFinderController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SongControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MusicFinderController musicFinderController;

    @Test
    public void testRetrieveLyrics_ValidSong() {
        String band = "Oasis";
        String track = "Wonderwall";
        String mockApiResponse = "{\"lyrics\":\"Today is gonna be the day that they gonna throw it back to you\"}";

        // Simulating the RestTemplate to return a mock response
        when(restTemplate.getForObject("https://api.lyrics.ovh/v1/" + band + "/" + track, String.class))
                .thenReturn(mockApiResponse);

        // Invoking the controller method to retrieve the response
        ObjectNode responseBody = musicFinderController.getSongDetails(band, track);

        // Creating a ResponseEntity to contain the response data and status
        ResponseEntity<ObjectNode> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        // Verifying that the response status is HTTP 200 OK
        assertEquals(200, responseEntity.getStatusCodeValue());

        // Verifying that the response contains the correct details
        assertEquals(band, responseBody.get("artist").asText());
        assertEquals(track, responseBody.get("song").asText());
        assertTrue(responseBody.get("lyrics").asText().contains("Today is gonna be the day"));
        assertTrue(responseBody.get("youtubeSearch").asText().contains("Oasis+Wonderwall"));
    }

    @Test
    public void testRetrieveLyrics_InvalidSong() throws Exception {
        String band = "Unknown Band";
        String track = "Unknown Track";

        // Simulating the RestTemplate to throw an exception for an invalid song request
        when(restTemplate.getForObject("https://api.lyrics.ovh/v1/" + band + "/" + track, String.class))
                .thenThrow(new RuntimeException("404 Not Found"));

        // Invoking the controller method to retrieve the response for an invalid song
        String responseBody = musicFinderController.getFormattedLyrics(band, track);

        // Parsing the response to confirm the error message
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson = objectMapper.readTree(responseBody);

        // Verifying that the response contains an "error" field with the appropriate message
        assertNotNull(responseJson.get("error"), "Expected an 'error' field in the response");
        assertEquals("Lyrics not found", responseJson.get("error").asText());
    }
}



