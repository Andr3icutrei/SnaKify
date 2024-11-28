package com.example.finalsnakespotify.Models;

import com.example.finalsnakespotify.snakeGameController;
import com.example.finalsnakespotify.welcomeController;
import com.sun.jdi.PrimitiveValue;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

import java.awt.*;
import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;

public class Game {
    private Board m_board;
    private Snake m_snake;
    private Apple m_apple;
    private Playlist m_playlist;
    private Timeline m_timeline;

    private double VOLUME=welcomeController.VOLUME;

    private String nowPlayingText;

    public Game(GraphicsContext gc,String link) throws IOException, ParseException, SpotifyWebApiException {
        m_board = new Board(gc);
        m_snake=new Snake(Board.GetNumberOfRows(),Board.GetNumberOfColumns());
        m_apple=new Apple(Board.GetNumberOfRows(),Board.GetNumberOfColumns(),m_snake.GetSnakeBody());

        m_playlist=new Playlist();
        m_playlist.SetPlaylistID(m_playlist.findPlaylistId(link));
        m_playlist.GetPlaylistForApple(m_playlist.GetPlaylistID());

        if(m_playlist==null)
            return;
        m_playlist.fetchAlbumCovers();
        m_playlist.playNextSong(VOLUME);

        m_snake.drawSnake(gc);
        m_apple.drawApple(gc,m_playlist.GetImagesURLs().get(m_playlist.GetCurrentIndex()));

    }
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

    public boolean eatFood()
    {
        if (m_snake.GetSnakeHead().x == m_apple.GetRow() && m_snake.GetSnakeHead().y == m_apple.GetColumn()) {
            Deque<Point> newSnakeBody=m_snake.GetSnakeBody();
            newSnakeBody.addLast(new Point(-1, -1));
            m_snake.SetSnakeBody(newSnakeBody);
            m_apple=new Apple(Board.GetNumberOfRows(), Board.GetNumberOfColumns(),m_snake.GetSnakeBody());
            return true;
        }
        return false;
    }

    public void drawSnake(GraphicsContext gc){
        m_snake.drawSnake(gc);
    }

    public void drawApple(GraphicsContext gc){
        if(m_playlist!=null) {
            m_apple.drawApple(gc, m_playlist.GetImagesURLs().get(m_playlist.GetCurrentIndex()));
        }
    }

    public void moveSnake(){
        m_snake.moveSnake();
        m_snake.goDirection();
    }

    public void stopPreviousSong(){
        if(m_playlist!=null)
            m_playlist.stopPreviousSong();
    }
    public void deleteOldApple(GraphicsContext gc) {
        gc.clearRect(m_apple.GetRow()*m_board.GetCellSize(),m_apple.GetColumn()*m_board.GetCellSize(),
                m_board.GetCellSize(),m_board.GetCellSize());
    }

    public void generateNewApple() {
        m_apple=new Apple(Board.GetNumberOfRows(),Board.GetNumberOfColumns(),m_snake.GetSnakeBody());
    }

    ///getters+setters
    public String GetCurrentSongTitle() {
        return m_playlist.GetTracks().get(m_playlist.GetCurrentIndex()).getName();
    }

    public String GetCurrentSongArtist() {
        String artistsString=new String();
        for(ArtistSimplified artist: m_playlist.GetTracks().get(m_playlist.GetCurrentIndex()).getArtists())
            artistsString+=artist.getName()+" ";
        return artistsString;
    }
    public Board GetBoard(){
        return m_board;
    }
    public Snake GetSnake(){
        return m_snake;
    }
    public Apple GetApple(){
        return m_apple;
    }
    public void SetSnakeDirection(Snake.direction direction){
        m_snake.SetCurrentDirection(direction);
    }
    public Playlist GetPlaylist() {
        return m_playlist;
    }
    public void SetPlaylist(Playlist m_playlist) {
        this.m_playlist = m_playlist;
    }

}
