package com.example.finalsnakespotify.Models;

import com.example.finalsnakespotify.Controllers.welcomeController;
import com.example.finalsnakespotify.Interfaces.IGame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.finalsnakespotify.Models.Playlist.GetCurrentIndex;

public class Game implements IGame {
    private Board m_board;
    private Snake m_snake;
    private Apple m_apple;
    private Playlist m_playlist;

    private double VOLUME=welcomeController.VOLUME;

    private String nowPlayingText;

    public Game(GraphicsContext gc, String link, boolean fetchedSongsData) throws IOException {
        m_board = new Board(gc);

        m_playlist = new Playlist();
        m_playlist.SetPlaylistURL(link);
        if (fetchedSongsData==false) {
            if (m_playlist.GetsongURLs().isEmpty()) {
                m_playlist.fetchPlaylistData(m_playlist.GetPlaylistURL());
                m_playlist.savePlaylistDataToJson();
            }
        }
        else {
            m_playlist.loadPlaylistDataFromJson();
        }
        m_playlist.playNextSong(VOLUME);

        m_snake=new Snake(Board.GetNumberOfRows(),Board.GetNumberOfColumns(),GetCurrentSongImage());
        m_apple=new Apple(Board.GetNumberOfRows(),Board.GetNumberOfColumns(),m_snake.GetSnakeBody());

        m_snake.drawSnake(gc);
        m_apple.drawApple(gc,m_playlist.GetImages().get(m_playlist.GetsongURLs().get(GetCurrentIndex())));
    }

    @Override
    public boolean gameOver() {
        if (m_snake.GetSnakeHead().x <0 || m_snake.GetSnakeHead().y < 2 || m_snake.GetSnakeHead().x >=Board.GetNumberOfRows() || m_snake.GetSnakeHead().y  >= Board.GetNumberOfColumns()+2) {
            return true;
        }

        Iterator iterator = m_snake.GetSnakeBody().iterator();
        iterator.next();
        while(iterator.hasNext()){
            Point snakeBodyCell = (Point) iterator.next();
            if(snakeBodyCell.x==m_snake.GetSnakeHead().x && snakeBodyCell.y==m_snake.GetSnakeHead().y){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eatFood(Image coverImage)
    {
        if (m_snake.GetSnakeHead().x == m_apple.GetRow() && m_snake.GetSnakeHead().y == m_apple.GetColumn()) {
            Deque<Point> newSnakeBody=m_snake.GetSnakeBody();
            newSnakeBody.addLast(new Point(-1, -1));
            Deque<Image> newSnakeBodyImages=m_snake.GetBodyImages();
            newSnakeBodyImages.addLast(coverImage);
            m_snake.SetSnakeBody(newSnakeBody);
            m_snake.SetBodyImages(newSnakeBodyImages);
            m_apple=new Apple(Board.GetNumberOfRows(), Board.GetNumberOfColumns(),m_snake.GetSnakeBody());
            return true;
        }
        return false;
    }

    @Override
    public void drawSnake(GraphicsContext gc){
        m_snake.drawSnake(gc);
    }

    @Override
    public void drawApple(GraphicsContext gc){
        if(m_playlist!=null) {
            m_apple.drawApple(gc, m_playlist.GetImages().get(m_playlist.GetsongURLs().get(GetCurrentIndex())));
        }
    }

    @Override
    public void moveSnake(){
        m_snake.moveSnake();
        m_snake.goDirection();
    }

    @Override
    public void stopPreviousSong(){
        if(m_playlist!=null)
            m_playlist.stopPreviousSong();
    }

    @Override
    public void deleteOldApple(GraphicsContext gc) {
        gc.clearRect(m_apple.GetRow()*m_board.GetCellSize(),m_apple.GetColumn()*m_board.GetCellSize(),
                m_board.GetCellSize(),m_board.GetCellSize());
    }

    @Override
    public void generateNewApple() {
        m_apple=new Apple(Board.GetNumberOfRows(),Board.GetNumberOfColumns(),m_snake.GetSnakeBody());
    }
    @Override
    public String GetCurrentSongTitle() {
        return m_playlist.GetSongNames().get(GetCurrentIndex());
    }
    @Override
    public Image GetCurrentSongImage() {
        return m_playlist.GetImages().get(m_playlist.GetCurrentTrackUrl());
    }
    @Override
    public String GetCurrentSongArtist() {
        return m_playlist.GetArtists().get(GetCurrentIndex());
    }
    @Override
    public Board GetBoard(){
        return m_board;
    }
    @Override
    public Snake GetSnake(){
        return m_snake;
    }
    @Override
    public Apple GetApple(){
        return m_apple;
    }
    @Override
    public void SetSnakeDirection(Snake.direction direction){
        m_snake.SetCurrentDirection(direction);
    }
    @Override
    public Playlist GetPlaylist() {
        return m_playlist;
    }
    @Override
    public void SetPlaylist(Playlist m_playlist) {
        this.m_playlist = m_playlist;
    }
}
