package com.example.finalsnakespotify.Models;

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

public class Apple {
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

    public boolean isEaten(int currRow, int currColumn){
        return currRow==m_row && currColumn==m_column;
    }

    public int getRow() {
        return m_row;
    }
    public void setRow(int row) {
        this.m_row = row;
    }
    public int getColumn() {
        return m_column;
    }
    public void setColumn(int column) {
        this.m_column = column;
    }
}
