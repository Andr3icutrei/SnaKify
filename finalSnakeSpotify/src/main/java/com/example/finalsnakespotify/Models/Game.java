package com.example.finalsnakespotify.Models;

import com.example.finalsnakespotify.Controllers.welcomeController;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.finalsnakespotify.Models.Playlist.getCurrentIndex;

public class Game {
    private Board m_board;
    private Snake m_snake;
    private Apple m_apple;
    private Playlist m_playlist;

    private double VOLUME = welcomeController.VOLUME;

    private String nowPlayingText;

    public Game(String link, boolean fetchedSongsData) throws IOException {
        m_playlist = new Playlist();
        m_playlist.setPlaylistURL(link);
        if (fetchedSongsData==false) {
            if (m_playlist.getsongURLs().isEmpty()) {
                m_playlist.fetchPlaylistData(m_playlist.getPlaylistURL());
                m_playlist.savePlaylistDataToJson();
            }
        }
        else {
            m_playlist.loadPlaylistDataFromJson();
        }
        m_playlist.playNextSong(VOLUME);

        m_snake=new Snake(Board.getNumberOfRows(),Board.getNumberOfColumns(),getCurrentSongImage());
        m_apple=new Apple(Board.getNumberOfRows(),Board.getNumberOfColumns(),m_snake.getSnakeBody());
    }


    public boolean gameOver() {
        if (m_snake.getSnakeHead().x <0 || m_snake.getSnakeHead().y < 2 ||
                m_snake.getSnakeHead().x >=Board.getNumberOfRows() ||
                m_snake.getSnakeHead().y  >= Board.getNumberOfColumns()+2) {
            return true;
        }

        Iterator iterator = m_snake.getSnakeBody().iterator();
        iterator.next();
        while(iterator.hasNext()){
            Point snakeBodyCell = (Point) iterator.next();
            if(snakeBodyCell.x==m_snake.getSnakeHead().x && snakeBodyCell.y==m_snake.getSnakeHead().y){
                return true;
            }
        }
        return false;
    }


    public boolean eatFood(Image coverImage)
    {
        if (m_snake.getSnakeHead().x == m_apple.getRow() && m_snake.getSnakeHead().y == m_apple.getColumn()) {
            Deque<Point> newSnakeBody=m_snake.getSnakeBody();
            newSnakeBody.addLast(new Point(-1, -1));
            Deque<Image> newSnakeBodyImages=m_snake.getBodyImages();
            newSnakeBodyImages.addLast(coverImage);
            m_snake.setSnakeBody(newSnakeBody);
            m_snake.setBodyImages(newSnakeBodyImages);
            m_apple=new Apple(Board.getNumberOfRows(), Board.getNumberOfColumns(),m_snake.getSnakeBody());
            return true;
        }
        return false;
    }

    public void moveSnake(){
        m_snake.moveSnake();
        m_snake.goDirection();
    }

    public void stopPreviousSong(){
        if(m_playlist!=null)
            m_playlist.stopPreviousSong();
    }


    public void generateNewApple() {
        m_apple=new Apple(Board.getNumberOfRows(),Board.getNumberOfColumns(),m_snake.getSnakeBody());
    }
    public String getCurrentSongTitle() {
        return m_playlist.getSongNames().get(getCurrentIndex());
    }
    public Image getCurrentSongImage() {
        return m_playlist.getImages().get(m_playlist.getCurrentTrackUrl());
    }
    public String getCurrentSongArtist() {
        return m_playlist.getArtists().get(getCurrentIndex());
    }
    public Board getBoard(){
        return m_board;
    }
    public Snake getSnake(){
        return m_snake;
    }
    public Apple getApple(){
        return m_apple;
    }
    public void setSnakeDirection(Snake.direction direction){
        m_snake.setCurrentDirection(direction);
    }
    public Playlist getPlaylist() {
        return m_playlist;
    }
    public void setPlaylist(Playlist m_playlist) {
        this.m_playlist = m_playlist;
    }
}
