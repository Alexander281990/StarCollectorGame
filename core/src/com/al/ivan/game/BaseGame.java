package com.al.ivan.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public abstract class BaseGame extends Game {
    private static BaseGame game;

    public BaseGame() {
        game = this;
    }

    public static void setActiveScreen(BaseScreen s) {
        game.setScreen(s);
    }

    public void create() {
        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor(im);
    }
}