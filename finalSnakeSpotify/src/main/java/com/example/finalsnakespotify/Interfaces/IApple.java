package com.example.finalsnakespotify.Interfaces;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public interface IApple {
    abstract void drawApple(GraphicsContext gc, Image albumCover);

    abstract boolean isEaten(int currRow, int currColumn);

    abstract int GetRow();
    abstract void SetRow(int row);

    abstract int GetColumn();
    abstract void SetColumn(int column);
}
