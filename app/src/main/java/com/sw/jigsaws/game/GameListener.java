package com.sw.jigsaws.game;

public interface GameListener {
    void nextLevel(int nextLevel);

    void timechanged(int currentTime);

    void gameover();
}