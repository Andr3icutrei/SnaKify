package com.example.finalsnakespotify.Controllers;

import com.example.finalsnakespotify.HelloApplication;
import com.example.finalsnakespotify.Models.Game;
import com.example.finalsnakespotify.Models.Playlist;
import com.example.finalsnakespotify.Models.Snake;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private AtomicBoolean fetchedSongsData=new AtomicBoolean(false);

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

    public void runSnakeGame(ActionEvent event,String link)throws IOException{
        stage.setTitle("Snake");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        setKeybinds();

        timeline = new Timeline(new KeyFrame(Duration.millis(frameDuration), e -> controller.runGame(game,timeline,VOLUME)));
        timeline.setCycleCount(Animation.INDEFINITE);

        timeline.play();
    }

    void createInvalidLinkPopUp() {
        if (stage != null) {
            Popup invalidLinkPopUp = new Popup();

            Label invalidLinkLabel = new Label("Invalid Link");
            invalidLinkLabel.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px;");

            invalidLinkPopUp.getContent().add(invalidLinkLabel);

            invalidLinkPopUp.show(stage, stage.getX() + stage.getWidth() / 2 - 50, stage.getY() + stage.getHeight() / 2 - 20);

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> invalidLinkPopUp.hide());
            delay.play();
        } else {
            System.out.println("Stage is null. Cannot display popup.");
        }
    }

    public void pressButton(ActionEvent event) throws IOException {
        String link=playlistLinkTextField.getText();
        stage=(Stage) playButton.getScene().getWindow();
        if(!Playlist.isPlaylistLinkValid(link))
        {
            createInvalidLinkPopUp();
            return;
        }
        fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("snakeGameScene.fxml"));
        scene = new Scene(fxmlLoader.load());
        controller = fxmlLoader.getController();

        Slider volumeSlider=controller.GetVolumeSlider();

        volumeSlider.setValue(VOLUME);

        game=controller.initialize(link);

        runSnakeGame(event,link);
    }

    public void restartGame(snakeGameController controller) throws IOException{
        timeline.stop();
        game.GetPlaylist().stopPreviousSong();
        controller.clearCanvas();
        game = new Game(controller.GetGraphicsContext(),playlistLinkTextField.getText(),true);
        timeline = new Timeline(new KeyFrame(Duration.millis(frameDuration), e -> controller.runGame(game,timeline,VOLUME)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public static Timeline getTimeline() {
        return timeline;
    }
}


