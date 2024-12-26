package com.example.finalsnakespotify.Models;

import javafx.event.ActionEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light;
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
    public enum direction {
        UP, DOWN, LEFT, RIGHT;
    }

    public boolean trySpawnHead(int row,int column,int maxRows,int maxColumns){
        return (row>=0 && row<maxRows && column>=0 && column<maxColumns);
    }

    public Snake(int rows,int columns){
        Random rand = new Random();
        int bodyPartX = rand.nextInt(rows);
        int bodyPartY = rand.nextInt(columns-2)+2;
        m_snakeBody = new ArrayDeque<>();
        m_snakeBody.addFirst(new Point(bodyPartX,bodyPartY));
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
        Iterator<Point> iterator1 = m_snakeBody.descendingIterator();
        Iterator<Point> iterator2=m_snakeBody.descendingIterator();

        iterator2.next();
        while (iterator1.hasNext()) {
            Point point1 = iterator1.next();
            Point point2;
            if( iterator2.hasNext()){
                point2 = iterator2.next();
                point1.x=point2.x;
                point1.y=point2.y;
            }
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

        gc.setFill(javafx.scene.paint.Color.web("4674E9"));
        Iterator<Point> iteratorVals = m_snakeBody.iterator();
        Point tail = m_snakeBody.getLast();
        if (iteratorVals.hasNext()) {
            iteratorVals.next();
        }
        while (iteratorVals.hasNext()) {
            Point snakeBodyCell = iteratorVals.next();
            gc.fillRoundRect(snakeBodyCell.x * Board.GetCellSize(), snakeBodyCell.y * Board.GetCellSize(),
                    Board.GetCellSize() - 1, Board.GetCellSize() - 1, 20, 20);
        }

        if ((tail.x + tail.y+2) % 2 == 0) {
            gc.setFill(javafx.scene.paint.Color.web("AAD751"));
        } else {
            gc.setFill(Color.web("A2D149"));
        }

        gc.fillRect(tail.x * Board.GetCellSize(), tail.y * Board.GetCellSize(), Board.GetCellSize(), Board.GetCellSize());
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

    public Deque<Point> GetSnakeBody(){
        return m_snakeBody;
    }
    public void SetSnakeBody(Deque<Point> s){
        m_snakeBody=s;
    }

    public direction GetCurrentDirection(){
        return m_currentDirection;
    }

    public void SetCurrentDirection(direction currentDirection){
        m_currentDirection = currentDirection;
    }

    public Point GetSnakeHead(){
        return m_snakeHead;
    }

    public void SetSnakeHead(int x,int y){
        m_snakeHead.x=x;
        m_snakeHead.y=y;
    }
}
