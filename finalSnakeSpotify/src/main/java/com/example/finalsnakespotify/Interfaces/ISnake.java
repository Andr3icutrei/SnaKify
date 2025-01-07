package com.example.finalsnakespotify.Interfaces;

import com.example.finalsnakespotify.Models.Snake;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.awt.*;
import java.util.Deque;

public interface ISnake {
    boolean trySpawnHead(int row,int column,int maxRows,int maxColumns);

    void moveSnake();

    void goDirection();

    void drawSnake(GraphicsContext gc);

    Deque<Point> GetSnakeBody();

    void SetSnakeBody(Deque<Point> body);

    Snake.direction GetCurrentDirection();

    void SetCurrentDirection(Snake.direction direction);

    Point GetSnakeHead();

    void SetSnakeHead(int x, int y);

    Deque<Image> GetBodyImages();

    void SetBodyImages(Deque<Image> images);

    void moveRight();
    void moveLeft();
    void moveUp();
    void moveDown();
}
