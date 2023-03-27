package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class Player {

    private Vector2 lastMousePosition;
    private Random rand;
    private Vector2 location;
    private Vector2 velocity;
    private float rotationVelocity;
    private boolean isGrounded;

    private Score score;
    private Array<Texture> characterTextures;
    public enum playerState {
        NORMAL, TETHERED, HITSTUN, FLING
    }
    private playerState state;

    private int tetherCooldown;

    private int jumpsAvailable;
    private int jumpsUsed;
    private int maxFallSpeed;
    public Sprite drawInfo;
    private Sound thrown;
    public Player(Array<Texture> ct, Score s, Sound thrownSound) {
        lastMousePosition = new Vector2(Vector2.Zero);
        rand = new Random();
        characterTextures = ct;
        location = new Vector2(400, 480);
        velocity = new Vector2(0,0);
        jumpsAvailable = 1;
        jumpsUsed = 0;
        drawInfo = new Sprite(ct.random(), 0, 0, 112,112);
        drawInfo.setPosition(400, 480);
        drawInfo.setSize(48,48);
        drawInfo.setOrigin(24,24);
        state = playerState.NORMAL;
        maxFallSpeed = -10;
        tetherCooldown = 0;
        score = s;
        thrown = thrownSound;
    }

    public void update(float mx, float my) {



        if (isGrounded && !(location.x > 145 && location.x < 615) && state != playerState.TETHERED) {
            isGrounded = false;
        }

        if(!isGrounded && velocity.y > maxFallSpeed) {
            velocity.y -= 0.1;
        }

        if (Math.abs(rotationVelocity) > 3) {
            rotationVelocity *= 0.99;
        }

        if (Math.abs(velocity.x) > 1 && state != playerState.NORMAL) {
            velocity.x *= 0.99;
        }

        if (Math.abs(velocity.y) > 3 && state != playerState.NORMAL) {
            velocity.y *= 0.99;
        }

        drawInfo.rotate(rotationVelocity);

        switch (state)
        {
            case NORMAL:
                if ((location.y > 180 && location.y + velocity.y < 180) && (location.x > 145 && location.x < 615)) {
                    makeGrounded();
                }
                location.add(velocity);
                if(location.x > mx - 24) {
                    location.x -= 1.5;
                }
                if(location.x <  mx - 24) {
                    location.x += 1.5;
                }

                if (Math.abs(location.x + 24 - mx) < 24 && Math.abs(location.y + 24 - my) < 24 && tetherCooldown == 0) {
                    makeTethered();
                }
                break;
            case TETHERED:
                location.x = mx - 24;
                location.y = my - 24;
                break;
            case HITSTUN:
                location.add(velocity);
                if ((location.y > 180 && location.y + velocity.y < 180) && (location.x > 145 && location.x < 615)) {
                    if (Math.abs(rotationVelocity) > 4 && Math.abs(velocity.y) > 4) {
                        velocity.y *= -1;
                        jumpsUsed = 0;
                    }
                    else {
                        makeGrounded();
                        makeNormal();
                    }
                }
                if (Math.abs(location.x + 24 - mx) < 10 && Math.abs(location.y + 24 - my) < 10 && tetherCooldown == 0) {
                    makeTethered();
                    break;
                }
                break;
            case FLING:
                location.add(velocity);

                if ((location.y > 180 && location.y + velocity.y < 180) && (location.x > 145 && location.x < 615)) {
                    if (Math.abs(rotationVelocity) > 4 && Math.abs(velocity.y) > 4) {
                        velocity.y *= -1;
                        jumpsUsed = 0;
                    }
                    else {
                        makeGrounded();
                        makeNormal();
                    }
                }
                if (Math.abs(location.x + 24 - mx) < 24 && Math.abs(location.y + 24 - my) < 24 && tetherCooldown == 0) {
                    makeTethered();
                    break;
                }
                drawInfo.setRotation(drawInfo.getRotation()%360);
                if (Math.abs(rotationVelocity) < 5 && Math.abs(drawInfo.getRotation()) < 25) {
                    makeNormal();
                    break;
                }
                break;
        }

        if (location.y + 24 < 0 || location.x + 24 < 0 || location.x + 24 > 800) {
            if (score.stocksRemaining > 0) {
                jumpsUsed = 0;
                location = new Vector2(400, 480);
                velocity.x = 0;
                velocity.y = 0;
                state = playerState.FLING;
                drawInfo.setTexture(characterTextures.random());
                score.stocksRemaining -= 1;
            }
            else {
                score.gameOver = true;
            }
        }

        if(tetherCooldown > 0) {
            tetherCooldown -= 1;
        }
        if(tetherCooldown < 0) {
            tetherCooldown = 0;
        }

        drawInfo.setX(location.x);
        drawInfo.setY(location.y);
        lastMousePosition = new Vector2(mx, my);
    }

    public void jump() {
        if (isGrounded || jumpsUsed < jumpsAvailable) {
            velocity.y = 5;
            if (!isGrounded) jumpsUsed += 1;
            isGrounded = false;
        }
    }

    public void makeNormal() {
        state = playerState.NORMAL;
        velocity = new Vector2(Vector2.Zero);
        rotationVelocity = 0;
        drawInfo.setRotation(0);
    }

    public void makeGrounded() {
        isGrounded = true;
        jumpsUsed = 0;
        velocity.y = 0;
        velocity.x = 0;
        location.y = 180;
    }

    public void fling(float mx, float my) {
        isGrounded = false;
        tetherCooldown = 60;

        float xv = mx - lastMousePosition.x;
        float yv = (my - lastMousePosition.y);

        velocity.x = xv;
        //if (rand.nextFloat()>0.5) velocity.x *= -1;

        velocity.y = yv;

        rotationVelocity = Math.max(Math.abs(xv + yv) *10, 10);
        if (rand.nextFloat()>0.5) rotationVelocity *= -1;
        state = playerState.FLING;
        thrown.play();
    }

    public void makeTethered() {
        isGrounded = true;
        state = playerState.TETHERED;
        velocity = new Vector2(Vector2.Zero);
        jumpsUsed = 0;
        rotationVelocity = 0;
        drawInfo.setRotation(0);
    }

    public void putInHitstun() {
        state = playerState.HITSTUN;
        velocity = new Vector2(Vector2.Zero);
        rotationVelocity = 30;
        tetherCooldown = 15;
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    public Vector2 getLocation() {
        return location;
    }

    public float getX() {
        return location.x;
    }

    public float getY() {
        return location.y;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public playerState getState() {
        return state;
    }

    public void updateTexture(Texture t) {
        drawInfo.setTexture(t);
    }

    public void setVelocity(float x, float y) {
        velocity = new Vector2(x,y);
    }
    public void setVelocityY(float y) {
        velocity.y = y;
    }
    public void setVelocityX(float x) {
        velocity.x = x;
    }
    public float getRotationVelocity() {
        return rotationVelocity;
    }
}
