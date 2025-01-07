package com.example.finalsnakespotify.Interfaces;

import com.example.finalsnakespotify.Models.Apple;
import com.example.finalsnakespotify.Models.Board;
import com.example.finalsnakespotify.Models.Playlist;
import com.example.finalsnakespotify.Models.Snake;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public interface IGame {

    // Game state methods
    boolean gameOver();
    boolean eatFood(Image coverImage);

    // Drawing methods
    void drawSnake(GraphicsContext gc);
    void drawApple(GraphicsContext gc);
    void deleteOldApple(GraphicsContext gc);

    // Movement and song control
    void moveSnake();
    void stopPreviousSong();
    void generateNewApple();

    // Getters for game elements
    String GetCurrentSongTitle();
    Image GetCurrentSongImage();
    String GetCurrentSongArtist();
    Board GetBoard();
    Snake GetSnake();
    Apple GetApple();
    Playlist GetPlaylist();

    // Setter for Snake direction and Playlist
    void SetSnakeDirection(Snake.direction direction);
    void SetPlaylist(Playlist m_playlist);
}
