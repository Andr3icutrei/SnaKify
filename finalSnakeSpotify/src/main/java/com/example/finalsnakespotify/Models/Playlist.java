package com.example.finalsnakespotify.Models;

import com.example.finalsnakespotify.Interfaces.IPlaylist;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Playlist implements IPlaylist {
    private static final String JAMENDO_API_URL = "https://api.jamendo.com/v3.0/";
    private static final String CLIENT_ID = "1d442a0a";

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

    public Playlist() {
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.m_songURLs = new ArrayList<>();
        this.m_ImageURLs = new ArrayList<>();
        this.m_songNames = new ArrayList<>();
        this.m_artists = new ArrayList<>();
        this.m_images = new HashMap<>();
    }

    @Override
    public void clearPlaylistDataJson() throws IOException {
        ObjectMapper objectMapper=new ObjectMapper();
        File file =new File(CURRENT_PLAYLISTDATA_PATH);
        if(file.exists()) {
            objectMapper.writeValue(file, new PlaylistData());
        }
    }

    @Override
    public void savePlaylistDataToJson() throws IOException {
        clearPlaylistDataJson();
        ObjectMapper objectMapper = new ObjectMapper();
        PlaylistData playlistData = new PlaylistData();

        playlistData.setPlaylistUrl(this.GetPlaylistURL());
        playlistData.setSongUrls(this.GetsongURLs());
        playlistData.setSongNames(this.GetSongNames());
        playlistData.setAlbumCoverUrls(this.GetImageURLs());
        playlistData.setArtistNames(this.GetArtists());

        objectMapper.writeValue(new File(CURRENT_PLAYLISTDATA_PATH), playlistData);
    }

    @Override
    public void loadPlaylistDataFromJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        File file = new File(CURRENT_PLAYLISTDATA_PATH);

        if (file.exists() && file.length() > 0) {
            try {
                PlaylistData playlistData = objectMapper.readValue(file, PlaylistData.class);

                this.SetPlaylistURL(playlistData.getPlaylistUrl());
                this.SetsongURLs(playlistData.getSongUrls());
                this.SetSongNames(playlistData.getSongNames());
                this.SetImageURLs(playlistData.getAlbumCoverUrls());
                this.SetArtists(playlistData.getArtistNames());
                System.out.println("Playlist data loaded from: " + CURRENT_PLAYLISTDATA_PATH);
            } catch (Exception e) {
                System.out.println("Error reading playlist data: " + e.getMessage());
            }
        } else {
            System.out.println("No saved playlist data found or file is empty!");
        }
        loadImagesFromJson();
    }

    @Override
    public void loadImagesFromJson() throws IOException {
        for(int i=0;i<m_songURLs.size();++i) {
            Image originalImage = new Image(m_ImageURLs.get(i));

            ImageView imageView = new ImageView(originalImage);
            imageView.setFitWidth(Board.GetCellSize());
            imageView.setFitHeight(Board.GetCellSize());
            imageView.setPreserveRatio(true);

            m_images.put(m_songURLs.get(i), imageView.getImage());
        }
    }

    @Override
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
                            imageView.setFitWidth(Board.GetCellSize());
                            imageView.setFitHeight(Board.GetCellSize());
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

    @Override
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

    @Override
    public void stopPreviousSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void increaseIndex() {
        if (m_songURLs.size() != 0) {
            m_index = ThreadLocalRandom.current().nextInt(0, 1000) % m_songURLs.size();
        }
    }

    @Override
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

    @Override
    public void changeVolume(double volume){
        if(mediaPlayer!=null)
            mediaPlayer.setVolume(volume/100.0);
    }

    @Override
    public List<String> GetImagesURLs() {
        return m_ImageURLs;
    }
    @Override
    public List<String> GetsongURLs() {
        return m_songURLs;
    }
    @Override
    public void SetsongURLs(List<String> songURLs) {
        m_songURLs = songURLs;
    }
    @Override
    public String GetCurrentTrackUrl() {
        return m_songURLs.get(m_index);
    }
    @Override
    public String GetPlaylistID() {
        return m_playlistID;
    }
    @Override
    public void SetPlaylistID(String playlistID) {
        m_playlistID= playlistID;
    }
    @Override
    public String GetPlaylistURL() {
        return m_playlistURL;
    }
    @Override
    public void SetPlaylistURL(String playlistURL) {
        m_playlistURL = playlistURL;
    }
    public static int GetCurrentIndex(){
        return m_index;
    }
    public static void SetCurrentIndex(int index){
        m_index = index;
    }
    @Override
    public void SetSongNames(List<String> songNames) {
        m_songNames = songNames;
    }
    @Override
    public void SetArtists(List<String> artists) {
        m_artists = artists;
    }
    @Override
    public List<String> GetSongNames(){
        return m_songNames;
    }
    @Override
    public List<String> GetArtists(){
        return m_artists;
    }
    public static MediaPlayer GetMediaPlayer() {
        return mediaPlayer;
    }
    @Override
    public void SetImages(HashMap<String,Image> images) {
        m_images = images;
    }
    @Override
    public HashMap<String,Image> GetImages() {
        return m_images;
    }
    @Override
    public void SetImageURLs(List<String>listImages){
        m_ImageURLs = listImages;
    }
    @Override
    public List<String> GetImageURLs(){
        return m_ImageURLs;
    }
}
