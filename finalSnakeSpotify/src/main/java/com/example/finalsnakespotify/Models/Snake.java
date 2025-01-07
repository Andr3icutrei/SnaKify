package com.example.finalsnakespotify.Models;


import com.example.finalsnakespotify.Interfaces.ISnake;
import javafx.event.ActionEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Random;

import static com.example.finalsnakespotify.Models.Board.*;
import static com.example.finalsnakespotify.Models.Apple.*;

public class Snake implements ISnake {

    private Deque<Point> m_snakeBody;
    private Point m_snakeHead;
    private direction m_currentDirection=direction.LEFT;
    private Deque<javafx.scene.image.Image> m_bodyImages;
    public enum direction {
        UP, DOWN, LEFT, RIGHT;
    }

    @Override
    public boolean trySpawnHead(int row,int column,int maxRows,int maxColumns){
        return (row>=0 && row<maxRows && column>=0 && column<maxColumns);
    }

    public Snake(int rows, int columns, Image firstSongImage){
        Random rand = new Random();
        int bodyPartX = rand.nextInt(rows);
        int bodyPartY = rand.nextInt(columns-2)+2;
        m_snakeBody = new ArrayDeque<>();
        m_bodyImages = new ArrayDeque<>();
        m_snakeBody.addFirst(new Point(bodyPartX,bodyPartY));
        m_bodyImages.addFirst(firstSongImage);
        m_snakeHead=m_snakeBody.peekFirst();

        if(trySpawnHead(bodyPartX-1,bodyPartY,rows,columns))
            m_snakeBody.addLast(new Point(bodyPartX-1,bodyPartY));
        else if(trySpawnHead(bodyPartX+1,bodyPartY,rows,columns))
            m_snakeBody.addLast(new Point(bodyPartX+1,bodyPartY));
        else if(trySpawnHead(bodyPartX,bodyPartY-1,rows,columns))
            m_snakeBody.addLast(new Point(bodyPartX,bodyPartY-1));
        else if(trySpawnHead(bodyPartX,bodyPartY+1,rows,columns))
            m_snakeBody.addLast(new Point(bodyPartX,bodyPartY+1));
    }

    @Override
    public void moveSnake(){
        Iterator<Point> itPrevPoint = m_snakeBody.descendingIterator();
        Iterator<Point> itCurrentPoint=m_snakeBody.descendingIterator();

        itPrevPoint.next();
        while (itCurrentPoint.hasNext()) {
            Point point1 = itCurrentPoint.next();
            Point point2;
            if( itPrevPoint.hasNext()){
                point2 = itPrevPoint.next();
                point1.x=point2.x;
                point1.y=point2.y;
            }
        }

        Iterator<javafx.scene.image.Image> itPrevImage = m_bodyImages.descendingIterator();
        Iterator<javafx.scene.image.Image> itCurrentImage=m_bodyImages.descendingIterator();

        itPrevImage.next();
        while (itCurrentImage.hasNext()) {
            javafx.scene.image.Image image1 = itCurrentImage.next();
            javafx.scene.image.Image image2;
            if( itPrevImage.hasNext()){
                image2 = itPrevImage.next();
                image1=image2;
            }
        }
    }

    @Override
    public void goDirection(){
        switch (m_currentDirection){
            case UP:
                moveUp();
                break;
            case DOWN:
                moveDown();
                break;
            case LEFT:
                moveLeft();
                break;
            case RIGHT:
                moveRight();
                break;
        }
    }

    @Override
    public void drawSnake(GraphicsContext gc) {

        gc.setFill(javafx.scene.paint.Color.LIGHTBLUE);
        Point head = m_snakeHead;
        double headX = head.x * Board.GetCellSize();
        double headY = head.y * Board.GetCellSize();

        double[] xPoints = new double[3];
        double[] yPoints = new double[3];

        switch (m_currentDirection) {
            case UP:
                // Pointing upwards
                xPoints[0] = headX + Board.GetCellSize() / 2;
                yPoints[0] = headY;
                xPoints[1] = headX; // Bottom left point
                yPoints[1] = headY + Board.GetCellSize();
                xPoints[2] = headX + Board.GetCellSize();
                yPoints[2] = headY + Board.GetCellSize();
                break;
            case DOWN:
                // Pointing downwards
                xPoints[0] = headX + Board.GetCellSize() / 2;
                yPoints[0] = headY + Board.GetCellSize();
                xPoints[1] = headX; // Top left point
                yPoints[1] = headY;
                xPoints[2] = headX + Board.GetCellSize();
                yPoints[2] = headY;
                break;
            case LEFT:
                // Pointing left
                xPoints[0] = headX; // Left point
                yPoints[0] = headY + Board.GetCellSize() / 2;
                xPoints[1] = headX + Board.GetCellSize();
                yPoints[1] = headY;
                xPoints[2] = headX + Board.GetCellSize();
                yPoints[2] = headY + Board.GetCellSize();
                break;
            case RIGHT:
                // Pointing right
                xPoints[0] = headX + Board.GetCellSize();
                yPoints[0] = headY + Board.GetCellSize() / 2;
                xPoints[1] = headX;
                yPoints[1] = headY;
                xPoints[2] = headX;
                yPoints[2] = headY + Board.GetCellSize();
                break;
        }

        gc.fillPolygon(xPoints, yPoints, 3);

        Iterator<Point> iteratorVals = m_snakeBody.iterator();
        Iterator<javafx.scene.image.Image> iteratorImages = m_bodyImages.iterator();
        Point tail = m_snakeBody.getLast();
        if (iteratorVals.hasNext() && iteratorImages.hasNext()) {
            iteratorVals.next();
            iteratorImages.next();
        }
        int index=0;
        while (iteratorVals.hasNext() && iteratorImages.hasNext()) {
            Point snakeBodyCell = iteratorVals.next();
            javafx.scene.image.Image snakeBodyImage = iteratorImages.next();

            gc.drawImage(snakeBodyImage,
                    snakeBodyCell.x*Board.GetCellSize(),snakeBodyCell.y*Board.GetCellSize(),
                       Board.GetCellSize(),Board.GetCellSize());
        }

        if ((tail.x + tail.y+2) % 2 == 0) {
            gc.setFill(javafx.scene.paint.Color.web("AAD751"));
        } else {
            gc.setFill(Color.web("A2D149"));
        }

        gc.fillRect(tail.x * Board.GetCellSize(), tail.y * Board.GetCellSize(), Board.GetCellSize(), Board.GetCellSize());
    }

    @Override
    public void moveRight() {
        m_snakeHead.x+=1;
    }

    @Override
    public void moveLeft() {
        m_snakeHead.x-=1;
    }

    @Override
    public void moveUp() {
        m_snakeHead.y-=1;
    }

    @Override
    public void moveDown() {
        m_snakeHead.y+=1;
    }
    @Override
    public Deque<Point> GetSnakeBody(){
        return m_snakeBody;
    }
    @Override
    public void SetSnakeBody(Deque<Point> s){
        m_snakeBody=s;
    }
    @Override
    public direction GetCurrentDirection(){
        return m_currentDirection;
    }
    @Override
    public void SetCurrentDirection(direction currentDirection){
        m_currentDirection = currentDirection;
    }
    @Override
    public Point GetSnakeHead(){
        return m_snakeHead;
    }
    @Override
    public Deque<javafx.scene.image.Image> GetBodyImages(){return m_bodyImages;}
    @Override
    public void SetBodyImages(Deque<javafx.scene.image.Image> s){m_bodyImages=s;}
    @Override
    public void SetSnakeHead(int x,int y){
        m_snakeHead.x=x;
        m_snakeHead.y=y;
    }
}
