package com.example.finalsnakespotify.Interfaces;

import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface IPlaylist {

    void savePlaylistDataToJson() throws IOException;
    void loadPlaylistDataFromJson() throws IOException;
    void clearPlaylistDataJson() throws IOException;
    void fetchPlaylistData(String playlistUrl) throws IOException;

    boolean playCurrentTrack(double VOLUME);
    void stopPreviousSong();
    void playNextSong(double VOLUME);
    void increaseIndex();

    void changeVolume(double volume);
    void loadImagesFromJson() throws IOException;

    String GetPlaylistID();
    void SetPlaylistID(String playlistID);
    String GetPlaylistURL();
    void SetPlaylistURL(String playlistURL);
    List<String> GetImagesURLs();
    List<String> GetsongURLs();
    void SetsongURLs(List<String> songURLs);
    String GetCurrentTrackUrl();

    void SetSongNames(List<String> songNames);

    void SetArtists(List<String> artists);

    List<String> GetSongNames();
    List<String> GetArtists();

    HashMap<String, Image> GetImages();
    void SetImages(HashMap<String, Image> images);
    List<String> GetImageURLs();
    void SetImageURLs(List<String> imageURLs);
}

