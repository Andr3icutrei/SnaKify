package com.example.finalsnakespotify.Models;

import com.example.finalsnakespotify.Interfaces.IApple;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.Deque;
import java.util.Random;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Apple implements IApple {
    private int m_row;
    private int m_column;

    Apple(int row, int column) {
        this.m_row = row;
        this.m_column = column;
    }

    public Apple(int rows, int columns, Deque<Point> snake)
    {
        Random rand = new Random();
        boolean validPosition=false;
        while(validPosition==false) {
            int currRow = rand.nextInt(rows);
            int currColumn = rand.nextInt(columns-2)+2;
            if (snake.stream().noneMatch(segment -> segment.x == currRow && segment.y == currColumn)) {
                validPosition = true;
                m_row = currRow;
                m_column = currColumn;
            }
        }
    }

    @Override
    public void drawApple(GraphicsContext gc,Image albumCover){
        new Thread(() -> {
            try {
                Platform.runLater(() -> {
                    gc.drawImage(albumCover, m_row * Board.GetCellSize(), m_column * Board.GetCellSize(), Board.GetCellSize(), Board.GetCellSize());
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public boolean isEaten(int currRow, int currColumn){
        return currRow==m_row && currColumn==m_column;
    }

    @Override
    public int GetRow() {
        return m_row;
    }

    @Override
    public void SetRow(int row) {
        this.m_row = row;
    }

    @Override
    public int GetColumn() {
        return m_column;
    }

    @Override
    public void SetColumn(int column) {
        this.m_column = column;
    }
}
