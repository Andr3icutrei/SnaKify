package com.example.finalsnakespotify.Models;

import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Playlist {
    private static final String CLIENT_ID = "9bf36a3d56874701801369607a9e6aa8";
    private static final String CLIENT_SECRET = "4467a69dcc5a48c880ded31e12ae22c1";
    private static SpotifyApi spotifyApi;
    private static int m_index=0;
    private static Media media;
    private static MediaPlayer mediaPlayer;

    private String m_playlistID;

    private List<String> m_ImageURLs;

    private static List<Track> tracks;


    // Constructor to initialize the Spotify API client
    public Playlist() {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(CLIENT_ID)
                .setClientSecret(CLIENT_SECRET)
                .build();
        if(tracks!=null)
            tracks.clear();
        this.tracks = new ArrayList<>();
        this.m_ImageURLs=new ArrayList<>();
    }

    // This method handles the authentication and authorization with Spotify
    public void authenticate() throws IOException, ParseException, SpotifyWebApiException {
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        ClientCredentials clientCredentials = clientCredentialsRequest.execute();
        spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        System.out.println("Authentication successful! Access token obtained.");
    }

    public void fetchAlbumCovers(){
        try {
            m_ImageURLs = new ArrayList<>();
            GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi.getPlaylistsItems(m_playlistID).build();
            Paging<PlaylistTrack> playlistTrackPaging = getPlaylistsItemsRequest.execute();

            for (PlaylistTrack playlistTrack : playlistTrackPaging.getItems()) {
                Track track = (Track) playlistTrack.getTrack();
                if (track != null && track.getAlbum() != null) {
                    Image[] albumImages = track.getAlbum().getImages();

                    // Select the first image URL (highest resolution)
                    if (albumImages.length > 0) {
                        m_ImageURLs.add(albumImages[0].getUrl());
                    }
                }
            }
        } catch (IOException | se.michaelthelin.spotify.exceptions.SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            System.out.println("Error fetching album cover URLs: " + e.getMessage());
        }
    }

    // This method generates and stores the songs from the given playlist
    public static String generateSongsFromPlaylist(String playlistId) throws IOException, ParseException, SpotifyWebApiException {
        // Clear previous tracks
        if(tracks!=null)
            tracks.clear();
        // Fetch playlist tracks using the Spotify API
        GetPlaylistRequest getPlaylistRequest;
        try{
            getPlaylistRequest = spotifyApi.getPlaylist(playlistId).build();
        }
        catch (Exception e){
            return "invalid link";
        }

        Paging<PlaylistTrack> playlistTracks = getPlaylistRequest.execute().getTracks();

        for (PlaylistTrack playlistTrack : playlistTracks.getItems()) {
            if (playlistTrack.getTrack() instanceof Track) {
                Track track = (Track) playlistTrack.getTrack();
                tracks.add(track);
            }
        }

        return "valid link";
    }

    private static void printTrackDetails() {
        for (Track track : tracks) {
            String trackName = track.getName();
            System.out.println("Track: " + trackName);
        }
    }

    public static String findPlaylistId(String link)
    {
        String regex=new String("^https:\\/\\/open[.]spotify[.]com\\/playlist\\/[a-zA-z0-9]{22}\\?si=[a-zA-z0-9]{16}$");

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(link);

        if(matcher.find())
            return link.substring(34,56);
        else
            return null;
    }

    public Playlist GetPlaylistForApple(String playlistId) throws IOException, ParseException, SpotifyWebApiException {
        Playlist playlist = new Playlist();
        playlist.authenticate();
        if(playlistId!=null){
            if(playlist.generateSongsFromPlaylist(playlistId)=="valid link") {
                return playlist;
            }
            else
                return null;
        }
        return null;
    }

    public boolean playCurrentTrack(double VOLUME) {
        if (m_index < 0 || m_index >= tracks.size()) {
            return false;
        }
        String previewUrl = tracks.get(m_index).getPreviewUrl();

        if (previewUrl != null) {
            media = new Media(previewUrl);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setCycleCount(1);

            mediaPlayer.setVolume(VOLUME);

            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Preview playback finished for track: " + tracks.get(m_index).getName());
                m_index = (m_index + 1) % tracks.size();
            });

            mediaPlayer.play();

            System.out.println("Playing preview for track: " + tracks.get(m_index).getName());
            return true;
        }
        else {
            System.out.println("No preview available for track: " + tracks.get(m_index).getName());
            return false;
        }
    }

    public void stopPreviousSong()
    {
        if (mediaPlayer != null)
            mediaPlayer.stop();
    }

    public void increaseIndex(){

        if(tracks.size()!=0)
            m_index= ThreadLocalRandom.current().nextInt(0, 1000)%tracks.size();
    }

    public static boolean isValidLink(String link) throws IOException, ParseException, SpotifyWebApiException {
        if(link.equals("")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Enter something");
            alert.showAndWait();
            return false;
        }

        if(generateSongsFromPlaylist(link)==null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid link");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    public void changeVolume(double volume){
        if(mediaPlayer!=null)
            mediaPlayer.setVolume(volume/100.0);
    }

    public void playNextSong(double VOLUME) {
        do{
            increaseIndex();
        }while(playCurrentTrack(VOLUME)==false);
    }
    public static void SetMediaPlayer(MediaPlayer mediaPlayer) {
        mediaPlayer = mediaPlayer;
    }
    public static MediaPlayer GetMediaPlayer() {
        return mediaPlayer;
    }
    public int GetCurrentIndex() {
        return m_index;
    }

    public void SetCurrentIndex(int currentIndex) {
        m_index=currentIndex;
    }
    public List<String> GetImagesURLs() {
        return m_ImageURLs;
    }
    public void SetImagesURLs(List<String> imagesURLs) {
        m_ImageURLs = imagesURLs;
    }
    public Track GetCurrentTrack() {
        return tracks.get(m_index);
    }

    public List<Track> GetTracks() {
        return tracks;
    }
    public String GetPlaylistID() {
        return m_playlistID;
    }

    public void SetPlaylistID(String playlistID) {
        m_playlistID = playlistID;
    }
}