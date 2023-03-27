package com.mygdx.game;

public class Score {

    public int stocksRemaining;
    public int handsDefeated;
    public boolean gameOver;
    public boolean gameStarted;

    public Score() {
        stocksRemaining = 50;
        handsDefeated = 0;
        gameOver = false;
        gameStarted = false;
    }
}
