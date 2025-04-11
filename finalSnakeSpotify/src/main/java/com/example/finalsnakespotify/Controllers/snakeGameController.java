package com.example.finalsnakespotify.Controllers;

import com.example.finalsnakespotify.Models.Board;
import com.example.finalsnakespotify.Models.Game;
import com.example.finalsnakespotify.Models.Playlist;
import com.example.finalsnakespotify.Models.Snake;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.awt.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private Game m_currentGame;

    private static final int FONT_SIZE = 60;
    private final static int songDuration = 30;//seconds
    public static int secondsLeft;
    private static int frameCounter = 0;
    private static int framesPerSecond=1000/ welcomeController.frameDuration;

    public void initialize(String link) throws IOException{
        gc = regularGameCanvas.getGraphicsContext2D();

        Group root = new Group();
        regularGameCanvas= new Canvas(Board.getWidth(), Board.getHeight());
        root.getChildren().add(regularGameCanvas);

        m_currentGame = new Game(link,false);
        nowPlayingLabel.setText("Now Playing : "+ m_currentGame.getCurrentSongTitle() + " by " + m_currentGame.getCurrentSongArtist());

        drawBackground();
        drawApple();

    }

    public void drawBackground(){
        int rows = Board.getNumberOfRows();
        int columns = Board.getNumberOfColumns();
        int cellSize = Board.getCellSize();
        for(int i = 0;i < rows; i++){
            for(int j = 0; j < columns; j++){ ///start with height from 2*cellsize to not start from top left
                if ((i + j) % 2 == 0) {
                    gc.setFill(Color.web("AAD751"));
                } else {
                    gc.setFill(Color.web("A2D149"));
                }
                gc.fillRect(i * cellSize, (j+2)*cellSize , cellSize, cellSize);
            }
        }
    }

    public void drawApple(){
        gc.drawImage(m_currentGame.getCurrentSongImage(), /// image
                    m_currentGame.getApple().getRow() * Board.getCellSize(), ///  xpos
                    m_currentGame.getApple().getColumn() * Board.getCellSize(), ///  ypos
                    Board.getCellSize(),Board.getCellSize()); /// dimensions
    }

    public void drawSnake(){
        Snake snake = m_currentGame.getSnake();
        Snake.direction direction = snake.getCurrentDirection();
        gc.setFill(javafx.scene.paint.Color.LIGHTBLUE);
        Point head = snake.getSnakeHead();
        double headX = head.x * Board.getCellSize();
        double headY = head.y * Board.getCellSize();

        double[] xPoints = new double[3];
        double[] yPoints = new double[3];

        switch (direction) {
            case UP:
                // Pointing upwards
                xPoints[0] = headX + Board.getCellSize() / 2;
                yPoints[0] = headY;
                xPoints[1] = headX; // Bottom left point
                yPoints[1] = headY + Board.getCellSize();
                xPoints[2] = headX + Board.getCellSize();
                yPoints[2] = headY + Board.getCellSize();
                break;
            case DOWN:
                // Pointing downwards
                xPoints[0] = headX + Board.getCellSize() / 2;
                yPoints[0] = headY + Board.getCellSize();
                xPoints[1] = headX; // Top left point
                yPoints[1] = headY;
                xPoints[2] = headX + Board.getCellSize();
                yPoints[2] = headY;
                break;
            case LEFT:
                // Pointing left
                xPoints[0] = headX; // Left point
                yPoints[0] = headY + Board.getCellSize() / 2;
                xPoints[1] = headX + Board.getCellSize();
                yPoints[1] = headY;
                xPoints[2] = headX + Board.getCellSize();
                yPoints[2] = headY + Board.getCellSize();
                break;
            case RIGHT:
                // Pointing right
                xPoints[0] = headX + Board.getCellSize();
                yPoints[0] = headY + Board.getCellSize() / 2;
                xPoints[1] = headX;
                yPoints[1] = headY;
                xPoints[2] = headX;
                yPoints[2] = headY + Board.getCellSize();
                break;
        }
        gc.fillPolygon(xPoints, yPoints, 3);

        Iterator<Point> iteratorVals = snake.getSnakeBody().iterator();
        Iterator<javafx.scene.image.Image> iteratorImages = snake.getBodyImages().iterator();
        Point tail = snake.getSnakeBody().getLast();

        if (iteratorVals.hasNext() && iteratorImages.hasNext()) {
            iteratorVals.next();
            iteratorImages.next();
        }

        while (iteratorVals.hasNext() && iteratorImages.hasNext()) {
            Point snakeBodyCell = iteratorVals.next();
            javafx.scene.image.Image snakeBodyImage = iteratorImages.next();

            gc.drawImage(snakeBodyImage,
                    snakeBodyCell.x*Board.getCellSize(),snakeBodyCell.y*Board.getCellSize(),
                    Board.getCellSize(),Board.getCellSize());
        }

        if ((tail.x + tail.y+2) % 2 == 0) {
            gc.setFill(javafx.scene.paint.Color.web("AAD751"));
        } else {
            gc.setFill(Color.web("A2D149"));
        }

        gc.fillRect(tail.x * Board.getCellSize(), tail.y * Board.getCellSize(), Board.getCellSize(), Board.getCellSize());
    }

    public void runGame(Timeline timeline,double VOLUME) {

        nowPlayingLabel.setText("Now Playing : "+m_currentGame.getCurrentSongTitle()+" by "+m_currentGame.getCurrentSongArtist());
        frameCounter++;

        int elapsedSeconds = frameCounter / framesPerSecond;

        secondsLeft = songDuration - elapsedSeconds;

        secondsLeftLabel.setText("Seconds left: " + secondsLeft);

        if (secondsLeft <= 0) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Digital-7", FONT_SIZE));
            gc.fillText("Time's Up!\nPress 'r' to restart", Board.getWidth() / 5, Board.getHeight() / 5);
            frameCounter = 0;
            timeline.stop();
            return;
        }

        if(m_currentGame.gameOver()){
            gc.setFill(Color.RED);
            gc.setFont(new Font("Digital-7", FONT_SIZE));
            gc.fillText("Game Over\nPress 'r' to restart", Board.getWidth() / 5, Board.getHeight()/5);
            frameCounter=0;
            timeline.stop();
            return;
        }

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            welcomeController.VOLUME = newValue.doubleValue();
        });
        if(m_currentGame.getPlaylist()!=null)
            m_currentGame.getPlaylist().changeVolume(welcomeController.VOLUME);

        drawSnake();
        m_currentGame.moveSnake();

        if(m_currentGame.eatFood(m_currentGame.getCurrentSongImage()))
        {
            m_currentGame.stopPreviousSong();
            m_currentGame.getPlaylist().playNextSong(VOLUME);
            m_currentGame.generateNewApple();
            drawApple();
            nowPlayingLabel.setText("Now Playing : "+m_currentGame.getCurrentSongTitle()+" by "+m_currentGame.getCurrentSongArtist());
            frameCounter=0;
        }
    }

    public void clearCanvas() {
        gc.clearRect(0, 0, Board.getWidth(), Board.getHeight());
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
            Playlist.getMediaPlayer().pause();
        }
        else {
            pauseButton.setText("Pause");
            welcomeController.getTimeline().play();
            Playlist.getMediaPlayer().play();
        }
    }

    public Game getCurrentGame() { return m_currentGame; }

    public void setCurrentGame(Game game) { m_currentGame = game; }
}
