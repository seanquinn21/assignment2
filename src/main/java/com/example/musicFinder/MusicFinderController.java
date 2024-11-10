package com.example.musicFinder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class MusicFinderController {

    // ObjectMapper to help with JSON formatting
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Basic status endpoint
    @GetMapping("/status")
    public String getStatus() {
        return "{\"status\":\"Application is running\"}";
    }

    // Fetch lyrics from Lyrics.ovh API and clean newline characters
public String getFormattedLyrics(String artist, String song) {
    // Use UriComponentsBuilder to construct a secure URL with query parameters
    String apiUrl = UriComponentsBuilder.fromHttpUrl("https://api.lyrics.ovh/v1")
            .pathSegment(artist)
            .pathSegment(song)
            .toUriString();

    RestTemplate restTemplate = new RestTemplate();
    try {
        // Fetch the raw JSON response
        String rawJson = restTemplate.getForObject(apiUrl, String.class);

        // Parse the JSON to extract the lyrics
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(rawJson);
        String rawLyrics = jsonNode.get("lyrics").asText();

        // Step 1: Remove carriage returns (\r)
        String formattedLyrics = rawLyrics.replaceAll("\\r", "");

        // Step 2: Replace single newlines (\n) with a single <br>
        formattedLyrics = formattedLyrics.replaceAll("\\n+", "<br>");

        // Step 3: Return the formatted lyrics
        return formattedLyrics.trim();
    } catch (Exception e) {
        return "{\"error\":\"Lyrics not found\"}";
    }
}

    
    
    
    // Generate YouTube search link based on artist and song
    private String getYouTubeSearchUrl(String artist, String song) {
        String searchQuery = artist.replace(" ", "+") + "+" + song.replace(" ", "+");
        return "https://www.youtube.com/results?search_query=" + searchQuery;
    }

    // Fetch song details, YouTube search link, and formatted lyrics
    @GetMapping("/song/{artist}/{name}")
    public ObjectNode getSongDetails(@PathVariable String artist, @PathVariable String name) {
        // Get the YouTube search link
        String youtubeSearchUrl = getYouTubeSearchUrl(artist, name);

        // Get the formatted song lyrics
        String lyrics = getFormattedLyrics(artist, name);

        // Build a JSON response with the song and artist details
        ObjectNode response = objectMapper.createObjectNode();
        response.put("song", name);
        response.put("artist", artist);
        response.put("youtubeSearch", youtubeSearchUrl);
        response.put("lyrics", lyrics);

        // Return the JSON response
        return response;
    }
}
