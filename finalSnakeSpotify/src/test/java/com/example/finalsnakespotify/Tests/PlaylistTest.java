package com.example.finalsnakespotify.Tests;

import com.example.finalsnakespotify.Models.Game;
import com.example.finalsnakespotify.Models.Playlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistTest {

    @Test
    void isPlaylistLinkValid() {
        String validUrl="https://www.jamendo.com/playlist/500608900/indie";
        Playlist playlist=new Playlist();
        assertEquals(true,Playlist.isPlaylistLinkValid(validUrl));
    }

    @Test
    void extractPlaylistIdFromUrl() {
        String validUrl="https://www.jamendo.com/playlist/500608900/indie";
        Playlist playlist=new Playlist();
        assertEquals("500608900",playlist.extractPlaylistIdFromUrl(validUrl));
    }
}