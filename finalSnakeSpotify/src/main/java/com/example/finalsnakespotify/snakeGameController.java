package com.example.finalsnakespotify;

import com.example.finalsnakespotify.Models.Board;
import com.example.finalsnakespotify.Models.Game;
import com.example.finalsnakespotify.Models.Playlist;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.security.PublicKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class snakeGameController {
    @FXML
    private Canvas regularGameCanvas;
    @FXML
    private AnchorPane regularGamePane;
    @FXML
    private Label secondsLeftLabel;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ImageView albumCoverImageView;
    @FXML
    private Label nowPlayingLabel;
    @FXML
    private Button pauseButton;

    GraphicsContext gc;

    private static final int FONT_SIZE = 60;
    private final static int songDuration=30;//seconds
    private static int frameCounter = 0;
    private static int framesPerSecond=1000/welcomeController.frameDuration;//1000ms=1s

    Game initialize(String link) throws IOException, ParseException, SpotifyWebApiException {
        gc = regularGameCanvas.getGraphicsContext2D();
        Group root = new Group();
        regularGameCanvas= new Canvas(Board.GetWidth(), Board.GetHeight());
        root.getChildren().add(regularGameCanvas);
        Game game=new Game(gc,link);
        nowPlayingLabel.setText("Now Playing : "+game.GetCurrentSongTitle()+" by "+game.GetCurrentSongArtist());
        return game;
    }

    public void runGame(Game game, Timeline timeline,double VOLUME) {
        nowPlayingLabel.setText("Now Playing : "+game.GetCurrentSongTitle()+" by "+game.GetCurrentSongArtist());
        frameCounter++;

        int elapsedSeconds = frameCounter / framesPerSecond;

        int secondsLeft = songDuration - elapsedSeconds;

        secondsLeftLabel.setText("Seconds left: " + secondsLeft);

        if (secondsLeft <= 0) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Digital-7", FONT_SIZE));
            gc.fillText("Time's Up! Game Over\nPress 'r' to restart", Board.GetWidth() / 5, Board.GetHeight() / 5);
            frameCounter = 0;
            timeline.stop();
            return;
        }

        if(game.gameOver()){
            gc.setFill(Color.RED);
            gc.setFont(new Font("Digital-7", FONT_SIZE));
            gc.fillText("Game Over\nPress 'r' to restart", Board.GetWidth() / 5, Board.GetHeight()/5);
            frameCounter=0;
            timeline.stop();
            return;
        }

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            welcomeController.VOLUME = newValue.doubleValue();
        });
        if(game.GetPlaylist()!=null)
            game.GetPlaylist().changeVolume(welcomeController.VOLUME);

        game.drawSnake(gc);
        game.moveSnake();

        if(game.eatFood())
        {
            game.stopPreviousSong();
            game.GetPlaylist().playNextSong(VOLUME);
            game.generateNewApple();
            game.drawApple(gc);
            nowPlayingLabel.setText("Now Playing : "+game.GetCurrentSongTitle()+" by "+game.GetCurrentSongArtist());
            frameCounter=0;
        }
    }

    public void clearCanvas() {
        gc.clearRect(0, 0, Board.GetWidth(), Board.GetHeight());
    }
    public Canvas GetRegularGameCanvas() {
        return regularGameCanvas;
    }
    public GraphicsContext GetGraphicsContext() {
        return gc;
    }
    public Slider GetVolumeSlider(){
        return volumeSlider;
    }
    public void SetVolumeSlider(Slider s){
        volumeSlider=s;
    }

    public void pressPauseButton(javafx.event.ActionEvent actionEvent) {
        if(pauseButton.getText().equals("Pause")){
            welcomeController.getTimeline().pause();
            pauseButton.setText("Continue");
            Playlist.GetMediaPlayer().pause();
        }
        else {
            pauseButton.setText("Pause");
            welcomeController.getTimeline().play();
            Playlist.GetMediaPlayer().play();
        }
    }
}
