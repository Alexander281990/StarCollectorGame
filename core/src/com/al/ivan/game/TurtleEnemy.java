package com.al.ivan.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class TurtleEnemy extends BaseActor{

    public TurtleEnemy(float x, float y, Stage s) {
        super(x, y, s);
        String[] filenames = {"turtle-1.png","turtle-2.png", "turtle-3.png",
                "turtle-4.png", "turtle-5.png", "turtle-6.png"};
        loadAnimationFromFiles(filenames, 0.1f, true);
        setAcceleration(100);
        setMaxSpeed(1000);
        setDeceleration(400);
        setBoundaryPolygon(8);
        rotateBy(0);
    }

    public void act(float dt) {
        super.act(dt);
        applyPhysics(dt);
        setAnimationPaused(!isMoving());
        //setSpeed(50);
        freeMovementInFourDirections();
        boundToWorld();
    }
}
