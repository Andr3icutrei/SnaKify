package com.example.finalsnakespotify.Models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Playlist {
    private static final String JAMENDO_API_URL = "https://api.jamendo.com/v3.0/";
    private static final String CLIENT_ID;
    static {
        try {
            CLIENT_ID = GetApiKey();
            System.out.println(CLIENT_ID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int m_index = 0;
    private static MediaPlayer mediaPlayer;

    private String m_playlistID;
    private String m_playlistURL;

    private List<String> m_ImageURLs;
    private List<String> m_songURLs;
    private List<String> m_songNames;
    private List<String> m_artists;
    private HashMap<String,Image> m_images;
    public static final String CURRENT_PLAYLISTDATA_PATH=Playlist.class.getResource("/JSONs/currentPlaylist.json").getPath();

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public Playlist() throws IOException {
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.m_songURLs = new ArrayList<>();
        this.m_ImageURLs = new ArrayList<>();
        this.m_songNames = new ArrayList<>();
        this.m_artists = new ArrayList<>();
        this.m_images = new HashMap<>();
    }

    private static String GetApiKey() throws IOException {
        Properties properties = new Properties();

        try (InputStream input = Playlist.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Config file not found");
            }
            properties.load(input);
            return properties.getProperty("api.key");
        }
    }

    public void clearPlaylistDataJson() throws IOException {
        ObjectMapper objectMapper=new ObjectMapper();
        File file =new File(CURRENT_PLAYLISTDATA_PATH);
        if(file.exists()) {
            objectMapper.writeValue(file, new PlaylistData());
        }
    }

    public void savePlaylistDataToJson() throws IOException {
        clearPlaylistDataJson();
        ObjectMapper objectMapper = new ObjectMapper();
        PlaylistData playlistData = new PlaylistData();

        playlistData.setPlaylistUrl(this.getPlaylistURL());
        playlistData.setSongUrls(this.getsongURLs());
        playlistData.setSongNames(this.getSongNames());
        playlistData.setAlbumCoverUrls(this.getImageURLs());
        playlistData.setArtistNames(this.getArtists());

        objectMapper.writeValue(new File(CURRENT_PLAYLISTDATA_PATH), playlistData);
    }

    public void loadPlaylistDataFromJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        File file = new File(CURRENT_PLAYLISTDATA_PATH);

        if (file.exists() && file.length() > 0) {
            try {
                PlaylistData playlistData = objectMapper.readValue(file, PlaylistData.class);

                this.setPlaylistURL(playlistData.getPlaylistUrl());
                this.setsongURLs(playlistData.getSongUrls());
                this.setSongNames(playlistData.getSongNames());
                this.setImageURLs(playlistData.getAlbumCoverUrls());
                this.setArtists(playlistData.getArtistNames());
                System.out.println("Playlist data loaded from: " + CURRENT_PLAYLISTDATA_PATH);
            } catch (Exception e) {
                System.out.println("Error reading playlist data: " + e.getMessage());
            }
        } else {
            System.out.println("No saved playlist data found or file is empty!");
        }
        loadImagesFromJson();
    }

    public void loadImagesFromJson() throws IOException {
        for(int i=0;i<m_songURLs.size();++i) {
            Image originalImage = new Image(m_ImageURLs.get(i));

            ImageView imageView = new ImageView(originalImage);
            imageView.setFitWidth(Board.getCellSize());
            imageView.setFitHeight(Board.getCellSize());
            imageView.setPreserveRatio(true);

            m_images.put(m_songURLs.get(i), imageView.getImage());
        }
    }

    public void fetchPlaylistData(String playlistUrl) throws IOException {
        String playlistId = extractPlaylistIdFromUrl(playlistUrl);
        if (playlistId == null) {
            System.out.println("Invalid playlist URL");
            return;
        }

        int limit = 50;
        int offset = 0;
        boolean hasMoreSongs = true;

        while (hasMoreSongs) {
            String apiUrl = JAMENDO_API_URL + "playlists/tracks?client_id=" + CLIENT_ID + "&id=" + playlistId + "&limit=" + limit + "&offset=" + offset;
            System.out.println("Fetching: " + apiUrl);

            Request request = new Request.Builder().url(apiUrl).build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("Error fetching playlist data: " + response.message());
                    return;
                }

                JsonNode root = objectMapper.readTree(response.body().string());
                JsonNode results = root.get("results");

                if (results.isArray() && results.size() > 0) {
                    JsonNode tracks = results.get(0).get("tracks");
                    if (tracks != null && tracks.isArray()) {
                        for (JsonNode trackNode : tracks) {
                            String songUrl = trackNode.get("audio").asText(null);
                            String songName = trackNode.get("name").asText(null);
                            String artist = trackNode.get("artist_name").asText(null);
                            String albumCoverUrl = trackNode.get("album_image").asText(null);

                            // Add data to respective lists
                            if (songUrl != null && !songUrl.isEmpty()) {
                                m_songURLs.add(songUrl);
                            }
                            if (songName != null && !songName.isEmpty()) {
                                m_songNames.add(songName);
                            }
                            if (artist != null && !artist.isEmpty()) {
                                m_artists.add(artist);
                            }
                            if (albumCoverUrl != null && !albumCoverUrl.isEmpty()) {
                                m_ImageURLs.add(albumCoverUrl);
                            }
                            Image originalImage = new Image(albumCoverUrl);

                            ImageView imageView = new ImageView(originalImage);
                            imageView.setFitWidth(Board.getCellSize());
                            imageView.setFitHeight(Board.getCellSize());
                            imageView.setPreserveRatio(true);

                            m_images.put(songUrl, imageView.getImage());
                        }
                    }
                }
                if (results.size() < limit) {
                    hasMoreSongs = false;
                } else {
                    offset += limit;
                }
            } catch (IOException e) {
                System.out.println("Error fetching data: " + e.getMessage());
                hasMoreSongs = false;
            }
        }

        System.out.println("Total songs fetched: " + m_songURLs.size());
    }

    public boolean playCurrentTrack(double VOLUME) {
        if (m_index < 0 || m_index >= m_songURLs.size()) {
            return false;
        }

        String audioUrl = m_songURLs.get(m_index);
        if (audioUrl != null) {
            Media media = new Media(audioUrl);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setCycleCount(1);

            mediaPlayer.setVolume(VOLUME);

            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Playback finished for track at index: " + m_index);
                m_index = (m_index + 1) % m_songURLs.size();
            });

            mediaPlayer.play();
            System.out.println("Playing track at index: " + m_index);
            return true;
        } else {
            System.out.println("No audio URL available for track at index: " + m_index);
            return false;
        }
    }

    public void stopPreviousSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void increaseIndex() {
        if (m_songURLs.size() != 0) {
            m_index = ThreadLocalRandom.current().nextInt(0, 1000) % m_songURLs.size();
        }
    }

    public void playNextSong(double VOLUME) {
        do {
            increaseIndex();
        } while (!playCurrentTrack(VOLUME));
    }

    public static boolean isPlaylistLinkValid(String link){
        String playlistId = extractPlaylistIdFromUrl(link);
        if (playlistId == null) {
            System.out.println("Invalid playlist URL format.");
            return false;
        }
        OkHttpClient client = new OkHttpClient();
        String apiUrl = JAMENDO_API_URL + "playlists?client_id=" + CLIENT_ID + "&id=" + playlistId;

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("API request failed: " + response.message());
                return false;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.body().string());
            JsonNode results = root.get("results");

            if (results != null && results.isArray() && results.size() > 0) {
                System.out.println("Valid playlist.");
                return true;
            } else {
                System.out.println("Invalid playlist or no tracks found.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error validating playlist: " + e.getMessage());
            return false;
        }
    }

    public static String extractPlaylistIdFromUrl(String url) {
        String regex = "\\d{9}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group();
        } else {
            System.out.println("Invalid Jamendo URL");
            return null;
        }
    }

    public void changeVolume(double volume){
        if(mediaPlayer!=null)
            mediaPlayer.setVolume(volume/100.0);
    }

    public List<String> getImagesURLs() {
        return m_ImageURLs;
    }
    public List<String> getsongURLs() {
        return m_songURLs;
    }
    public void setsongURLs(List<String> songURLs) {
        m_songURLs = songURLs;
    }
    public String getCurrentTrackUrl() {
        return m_songURLs.get(m_index);
    }
    public String getPlaylistID() {
        return m_playlistID;
    }
    public void setPlaylistID(String playlistID) {
        m_playlistID= playlistID;
    }
    public String getPlaylistURL() {
        return m_playlistURL;
    }
    public void setPlaylistURL(String playlistURL) {
        m_playlistURL = playlistURL;
    }
    public static int getCurrentIndex(){
        return m_index;
    }
    public static void setCurrentIndex(int index){
        m_index = index;
    }
    public void setSongNames(List<String> songNames) {
        m_songNames = songNames;
    }
    public void setArtists(List<String> artists) {
        m_artists = artists;
    }
    public List<String> getSongNames(){
        return m_songNames;
    }
    public List<String> getArtists(){
        return m_artists;
    }
    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    public void setImages(HashMap<String,Image> images) {
        m_images = images;
    }
    public HashMap<String,Image> getImages() {
        return m_images;
    }
    public void setImageURLs(List<String>listImages){
        m_ImageURLs = listImages;
    }
    public List<String> getImageURLs(){
        return m_ImageURLs;
    }
}
