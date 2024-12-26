package com.example.finalsnakespotify.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PlaylistData {
    @JsonProperty("playlist_url")
    private String playlistUrl;

    @JsonProperty("song_urls")
    private List<String> songUrls;

    @JsonProperty("song_names")
    private List<String> songNames;

    @JsonProperty("album_cover_urls")
    private List<String> albumCoverUrls;

    public List<String> artistNames() {
        return artistNames;
    }

    public PlaylistData setArtistNames(List<String> artistNames) {
        this.artistNames = artistNames;
        return this;
    }

    @JsonProperty("artist_names")
    private List<String> artistNames;

    // Getters and Setters

    public String getPlaylistUrl() {
        return playlistUrl;
    }

    public void setPlaylistUrl(String playlistUrl) {
        this.playlistUrl = playlistUrl;
    }

    public List<String> getSongUrls() {
        return songUrls;
    }

    public void setSongUrls(List<String> songUrls) {
        this.songUrls = songUrls;
    }

    public List<String> getSongNames() {
        return songNames;
    }

    public void setSongNames(List<String> songNames) {
        this.songNames = songNames;
    }

    public List<String> getAlbumCoverUrls() {
        return albumCoverUrls;
    }

    public void setAlbumCoverUrls(List<String> albumCoverUrls) {
        this.albumCoverUrls = albumCoverUrls;
    }

    public List<String> getArtistNames() {
        return this.artistNames;
    }
}
