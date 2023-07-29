package com.al.ivan.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class LevelScreen extends BaseScreen{

    private Turtle turtle;
    protected Touchpad touchpad;
    @Override
    public void initialize() {
        BaseActor ocean = new BaseActor(0,0, mainStage);
        ocean.loadTexture( "water-border.jpg" );
        ocean.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        BaseActor.setWorldBounds(ocean);
        turtle = new Turtle(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, mainStage);
        turtle.setSize(turtle.getWidth() * 1.5f, turtle.getHeight() * 1.5f);

        // отвечает за изображение джойстика
        // Чтобы джойстик работал необходимо в класс BaseScreen добавить следующие методы
        /*
         public void show()
        {
            InputMultiplexer im = (InputMultiplexer)Gdx.input.getInputProcessor();
            im.addProcessor(this);
            im.addProcessor(uiStage);
            im.addProcessor(mainStage);
        }
        public void hide()
        {
            InputMultiplexer im = (InputMultiplexer)Gdx.input.getInputProcessor();
            im.removeProcessor(this);
            im.removeProcessor(uiStage);
            im.removeProcessor(mainStage);
        }
        // И в класcе BaseGame в методе create() добавить следующие строки:
        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor(im);
        */
        Gdx.graphics.setWindowedMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Touchpad.TouchpadStyle touchStyle = new Touchpad.TouchpadStyle();
        Texture padKnobTex = new Texture(Gdx.files.internal("joystick-knob.png"));
        TextureRegion padKnobReg = new TextureRegion(padKnobTex);
        touchStyle.knob = new TextureRegionDrawable(padKnobReg);
        Texture padBackTex = new Texture(Gdx.files.internal("joystick-background-new.png"));
        TextureRegion padBackReg = new TextureRegion(padBackTex);
        touchStyle.background = new TextureRegionDrawable(padBackReg);
        touchpad = new Touchpad(5, touchStyle);
        touchpad.setSize(500, 500);
        uiStage.addActor(touchpad);
        // отвечает за изображение джойстика (конец)
    }

    @Override
    public void update(float dt) {
        // Этод метод заставляет двигаться звездолет с помощью джойстика
        Vector2 direction = new Vector2(this.touchpad.getKnobPercentX(), this.touchpad.getKnobPercentY());
        //Vector2 direction = new Vector2(Gdx.input.getDeltaX(), -Gdx.input.getDeltaY());
        float length = direction.len();
        if (length > 0.0F) {
            this.turtle.setSpeed(700.0F * length);
            this.turtle.setDeceleration(500);
            this.turtle.setMotionAngle(direction.angle());
        }
        // Этод метод заставляет двигаться звездолет с помощью джойстика(конец)
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
