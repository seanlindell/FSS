package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;



public class Bullet {
    private Vector2 location;
    private Vector2 velocity;
    private Array<Texture> textures;
    public Sprite drawInfo;
    int currentFrame;
    float timeSinceLastAnimationUpdate;
    public int myDirection;
    Sound shoot;

    public Bullet(Array<Texture> bt, Sound shoots) {
        location = new Vector2(850, 340);
        velocity = new Vector2(0,0);
        drawInfo = new Sprite(bt.first(), 0, 0, 48,32);
        drawInfo.setPosition(740, 480);
        drawInfo.setSize(48,32);
        drawInfo.setOrigin(32,32);
        drawInfo.setFlip(true, false);
        textures = bt;
        currentFrame = 0;
        timeSinceLastAnimationUpdate = 0;
        myDirection = 1;
        //fireBullet(new Vector2(400,240), 1);
        shoot = shoots;
    }

    public void update() {
        location.add(velocity);
        if (timeSinceLastAnimationUpdate == 6) {
            currentFrame = (currentFrame + 1) % 4;
            drawInfo.setTexture(textures.get(currentFrame));
            timeSinceLastAnimationUpdate = 0;
        }
        timeSinceLastAnimationUpdate += 1;

        drawInfo.setX(location.x);
        drawInfo.setY(location.y);
    }

    public void fireBullet(Vector2 spawnAt, int direction) {
        shoot.play();
        myDirection = direction;
        location = new Vector2(spawnAt);
        velocity.x = 9 * direction;
        if (direction == 1) {
            drawInfo.setFlip(true, false);
        }
        else {
            drawInfo.setFlip(false, false);
        }
    }
}
