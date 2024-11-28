package com.example.finalsnakespotify;

import com.example.finalsnakespotify.Models.Board;
import com.example.finalsnakespotify.Models.Game;
import com.example.finalsnakespotify.Models.Playlist;
import com.example.finalsnakespotify.Models.Snake;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

public class welcomeController {
    @FXML
    private Button playButton;

    @FXML
    private TextField playlistLinkTextField;

    private static Timeline timeline;

    public final static int frameDuration=210;
    public static double VOLUME=50.0;

    private Game game;
    private Scene scene;
    private snakeGameController controller;
    private Stage stage;
    private FXMLLoader fxmlLoader;

    public void setKeybinds()
    {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if(code==KeyCode.R) {
                    if(game.gameOver()==true){
                        try {
                            restartGame(controller);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        } catch (SpotifyWebApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if (code == KeyCode.RIGHT || code == KeyCode.D) {
                    if (game.GetSnake().GetCurrentDirection() != Snake.direction.LEFT) {
                        game.SetSnakeDirection(Snake.direction.RIGHT);
                    }
                } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                    if (game.GetSnake().GetCurrentDirection() != Snake.direction.RIGHT) {
                        game.SetSnakeDirection(Snake.direction.LEFT);
                    }
                } else if (code == KeyCode.UP || code == KeyCode.W) {
                    if (game.GetSnake().GetCurrentDirection() != Snake.direction.DOWN) {
                        game.SetSnakeDirection(Snake.direction.UP);
                    }
                } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                    if (game.GetSnake().GetCurrentDirection() != Snake.direction.UP) {
                        game.SetSnakeDirection(Snake.direction.DOWN);
                    }
                }
            }
        });
    }

    public void runSnakeGame(ActionEvent event,String link)throws IOException, ParseException, SpotifyWebApiException {
        stage.setTitle("Snake");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        setKeybinds();

        timeline = new Timeline(new KeyFrame(Duration.millis(frameDuration), e -> controller.runGame(game,timeline,VOLUME)));
        timeline.setCycleCount(Animation.INDEFINITE);

        timeline.play();
    }

    public boolean isValidLink(String link) throws IOException, ParseException, SpotifyWebApiException {
        if(!playlistLinkTextField.getText().equals(""))
            link=new String(playlistLinkTextField.getText());
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Enter something");
            alert.showAndWait();
            return false;
        }

        if(Playlist.findPlaylistId(link)==null || Playlist.generateSongsFromPlaylist(link)==null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid link");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    public void pressButton(ActionEvent event) throws IOException, ParseException, SpotifyWebApiException {
        stage=(Stage) playButton.getScene().getWindow();
        fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("snakeGameScene.fxml"));
        scene = new Scene(fxmlLoader.load());
        controller = fxmlLoader.getController();

        Slider volumeSlider=controller.GetVolumeSlider();

        volumeSlider.setValue(VOLUME);

        String link=playlistLinkTextField.getText();

        if(!isValidLink(link))
            return;

        game=controller.initialize(link);

        runSnakeGame(event,link);
    }

    public void restartGame(snakeGameController controller) throws IOException, ParseException, SpotifyWebApiException {
        timeline.stop();
        game.GetPlaylist().stopPreviousSong();
        controller.clearCanvas();
        game = new Game(controller.GetGraphicsContext(),playlistLinkTextField.getText());
        timeline = new Timeline(new KeyFrame(Duration.millis(frameDuration), e -> controller.runGame(game,timeline,VOLUME)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    public static Timeline getTimeline() {
        return timeline;
    }
}


