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
    public void testFetchLyrics_ValidSong() throws Exception {
        //Song and title
        String artist = "Oasis";
        String song = "Wonderwall";
        String mockApiResponse = "{\"lyrics\": \"Today is gonna be the day that they're gonna throw it back to you...\"}";

        // Mocking the RestTemplate to return the mock API response
        when(restTemplate.getForObject("https://api.lyrics.ovh/v1/" + artist + "/" + song, String.class))
                .thenReturn(mockApiResponse);

        // Calling the controller
        ObjectNode responseBody = musicFinderController.getSongDetails(artist, song);

        
        ResponseEntity<ObjectNode> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        
        assertEquals(200, responseEntity.getStatusCode().value());

        // Assert that the response body contains the correct song details
        assertEquals(artist, responseBody.get("artist").asText());
        assertEquals(song, responseBody.get("song").asText());
        assertTrue(responseBody.get("lyrics").asText().contains("Today is gonna be the day"));

        // Optional: Additional assertions
        assertTrue(responseBody.get("youtubeSearch").asText().contains("Oasis Wonderwall"));
    }

    @Test
    public void testFetchLyrics_InvalidSong() throws Exception {
        // Mock data for an invalid artist and song
        String artist = "Unknown Artist";
        String song = "Unknown Song";

        // Mocking the RestTemplate to throw an exception for an invalid request
        when(restTemplate.getForObject("https://api.lyrics.ovh/v1/" + artist + "/" + song, String.class))
                .thenThrow(new RuntimeException("404 Not Found"));

        // call controller for response 
        String responseBody = musicFinderController.getFormattedLyrics(artist, song);

        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson = objectMapper.readTree(responseBody);

        // error message 
        assertNotNull(responseJson.get("error"), "Expected an 'error' field in the response body");
        assertEquals("Lyrics not found", responseJson.get("error").asText());
    }
}


