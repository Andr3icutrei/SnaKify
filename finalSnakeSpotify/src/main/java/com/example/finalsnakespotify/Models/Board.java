package com.example.finalsnakespotify.Models;

import com.example.finalsnakespotify.Interfaces.IBoard;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Board implements IBoard {

    private static final int m_width=600;
    private static final int m_height=900;
    private static final int m_numberOfRows=13;
    private static final int m_numberOfColumns=13;
    private static final int m_cellSize=m_width/m_numberOfRows;

    public Board(GraphicsContext gc) {
        drawBackground(gc);
    }

    @Override
    public void drawBackground(GraphicsContext gc){
        for(int i=0;i<m_numberOfRows;i++){
            for(int j=0;j<m_numberOfColumns;j++){///start with height from 2*cellsize to not start from top left
                    if ((i + j) % 2 == 0) {
                        gc.setFill(Color.web("AAD751"));
                    } else {
                        gc.setFill(Color.web("A2D149"));
                    }
                gc.fillRect(i * m_cellSize, (j+2)*m_cellSize , m_cellSize, m_cellSize);
            }
        }
    }
    public static int GetWidth(){return m_width;}
    public static int GetHeight(){
        return m_height;
    }
    public static int GetCellSize(){return m_cellSize;}
    public static int GetNumberOfRows(){return m_numberOfRows;}
    public static int GetNumberOfColumns(){return m_numberOfColumns;}
}
