package com.al.ivan.game;

public class StarCollectorGame extends BaseGame {
    @Override
    public void create() {
        super.create();
        setActiveScreen(new LevelScreen());
    }
}