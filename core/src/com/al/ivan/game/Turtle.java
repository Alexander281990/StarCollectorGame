package com.al.ivan.game;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Turtle extends BaseActor{
    public Turtle(float x, float y, Stage s) {
        super(x, y, s);
        String[] filenames = {"turtle-1.png","turtle-2.png", "turtle-3.png",
                "turtle-4.png", "turtle-5.png", "turtle-6.png"};
        loadAnimationFromFiles(filenames, 0.1f, true);
        setAcceleration(100);
        setMaxSpeed(2000);
        setDeceleration(400);
        setBoundaryPolygon(8);
    }
    public void act(float dt) {
        super.act(dt);

        applyPhysics(dt);
        setAnimationPaused(!isMoving());

        if (getSpeed() > 0) {
            setRotation(getMotionAngle());
        }
        boundToWorld();
        alignCamera();
    }
}
