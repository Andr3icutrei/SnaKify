package com.example.finalsnakespotify.Models;


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

public class Snake {

    private Deque<Point> m_snakeBody;
    private Point m_snakeHead;
    private direction m_currentDirection=direction.LEFT;
    private Deque<javafx.scene.image.Image> m_bodyImages;
    public enum direction {
        UP, DOWN, LEFT, RIGHT;
    }

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

    public void drawSnake() {
        Point head = m_snakeHead;
        double headX = head.x * Board.getCellSize();
        double headY = head.y * Board.getCellSize();

        double[] xPoints = new double[3];
        double[] yPoints = new double[3];

        switch (m_currentDirection) {
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

        Iterator<Point> iteratorVals = m_snakeBody.iterator();
        Iterator<javafx.scene.image.Image> iteratorImages = m_bodyImages.iterator();
        Point tail = m_snakeBody.getLast();
        if (iteratorVals.hasNext() && iteratorImages.hasNext()) {
            iteratorVals.next();
            iteratorImages.next();
        }
        while (iteratorVals.hasNext() && iteratorImages.hasNext()) {
            Point snakeBodyCell = iteratorVals.next();
            javafx.scene.image.Image snakeBodyImage = iteratorImages.next();
        }
    }

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

    public void moveRight() {
        m_snakeHead.x+=1;
    }
    public void moveLeft() {
        m_snakeHead.x-=1;
    }
    public void moveUp() {
        m_snakeHead.y-=1;
    }
    public void moveDown() {
        m_snakeHead.y+=1;
    }

    public Deque<Point> getSnakeBody(){
        return m_snakeBody;
    }
    public void setSnakeBody(Deque<Point> s){
        m_snakeBody=s;
    }
    public direction getCurrentDirection(){
        return m_currentDirection;
    }
    public void setCurrentDirection(direction currentDirection){
        m_currentDirection = currentDirection;
    }
    public Point getSnakeHead(){
        return m_snakeHead;
    }
    public Deque<javafx.scene.image.Image> getBodyImages(){return m_bodyImages;}
    public void setBodyImages(Deque<javafx.scene.image.Image> s){m_bodyImages=s;}
    public void setSnakeHead(int x,int y){
        m_snakeHead.x=x;
        m_snakeHead.y=y;
    }
}
