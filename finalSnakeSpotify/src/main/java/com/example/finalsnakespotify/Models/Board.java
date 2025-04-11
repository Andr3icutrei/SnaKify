package com.example.finalsnakespotify.Models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Board {

    private static final int m_width=600;
    private static final int m_height=900;
    private static final int m_numberOfRows=13;
    private static final int m_numberOfColumns=13;
    private static final int m_cellSize=m_width/m_numberOfRows;

    public static int getWidth(){return m_width;}
    public static int getHeight(){
        return m_height;
    }
    public static int getCellSize(){return m_cellSize;}
    public static int getNumberOfRows(){return m_numberOfRows;}
    public static int getNumberOfColumns(){return m_numberOfColumns;}
}
