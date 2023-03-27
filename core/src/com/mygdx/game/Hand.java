package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class Hand {

    private Random rand;
    private Vector2 location;
    private Vector2 handVelocity;
    public float rotationVelocity;
    private Array<Texture> handTextures;
    private int punchTimer;
    public int currentFrame;
    private int timeSinceLastAnimationUpdate;
    public enum handState {
        IDLE, PUNCH, GUN, DEATH
    }
    private handState state;

    private int invincibilityTimer;
    public float health;

    private Score score;
    public Sprite drawInfo;
    private Bullet myBullet;
    private int deathTimer;

    private Sound laugh;
    private Sound death;
    private Sound punch;
    private Sound yell;

    private int handNum;
    public Hand(Array<Texture> ht, Bullet bullet, Score s, int hn, Sound laughs, Sound deaths, Sound punchs, Sound sadyells) {
        rand = new Random();
        location = new Vector2(-100, 340);
        handVelocity = new Vector2(0,0);
        drawInfo = new Sprite(ht.first(), 0, 0, 64,64);
        drawInfo.setPosition(740, 480);
        drawInfo.setSize(128,128);
        drawInfo.setOrigin(32,32);
        drawInfo.setFlip(true, false);
        state = handState.IDLE;
        handTextures = ht;
        currentFrame = 0;
        timeSinceLastAnimationUpdate = 0;
        health = 250;
        punchTimer = 0;
        myBullet = bullet;
        deathTimer = 0;
        score = s;
        handNum = hn;
        laugh = laughs;
        death = deaths;
        punch = punchs;
        yell = sadyells;
    }
    public void update(float mx, float my) {
        if (((handNum == 2 && score.handsDefeated > 2) || handNum == 1) && score.gameStarted) {
            switch (state) {
                case IDLE:
                    location.add(handVelocity);
                    if (currentFrame > 7) {
                        currentFrame = 0;
                        timeSinceLastAnimationUpdate = 0;
                    } else {
                        if (currentFrame == 0) {
                            if (timeSinceLastAnimationUpdate > 10) {
                                currentFrame = (currentFrame + 1) % 8;
                                timeSinceLastAnimationUpdate = 0;
                            }
                        } else {
                            if (timeSinceLastAnimationUpdate > 6) {
                                currentFrame = (currentFrame + 1) % 8;
                                timeSinceLastAnimationUpdate = 0;
                            }
                        }
                    }


                    // Fix position
                    if (location.x < 25) {
                        drawInfo.setFlip(true, false);
                        handVelocity.x = 3;
                    }
                    if (location.x > 675) {
                        drawInfo.setFlip(false, false);
                        handVelocity.x = -3;
                    }
                    if (location.x > 100 && location.x < 400) {
                        drawInfo.setFlip(true, false);
                        handVelocity.x = -3;
                    }
                    if (location.x > 400 && location.x < 660) {
                        drawInfo.setFlip(false, false);
                        handVelocity.x = 3;
                    }

                    // In position
                    if (location.x > 25 && location.x < 100) {
                        handVelocity = new Vector2(Vector2.Zero);
                        drawInfo.setFlip(true, false);
                        if (rand.nextFloat() < 0.002 * (score.handsDefeated + 1)) {
                            state = handState.PUNCH;
                        } else if (rand.nextFloat() < 0.002 * (score.handsDefeated + 1)) {
                            state = handState.GUN;
                        }
                    }
                    if (location.x < 675 && location.x > 660) {
                        handVelocity = new Vector2(Vector2.Zero);
                        drawInfo.setFlip(false, false);
                        if (rand.nextFloat() < 0.002 * (score.handsDefeated + 1)) {
                            state = handState.PUNCH;
                        } else if (rand.nextFloat() < 0.002 * (score.handsDefeated + 1)) {
                            state = handState.GUN;
                        }
                    }

                    break;
                case PUNCH:
                    location.add(handVelocity);
                    if (currentFrame < 8 || currentFrame > 13) {
                        currentFrame = 8;
                        timeSinceLastAnimationUpdate = 0;
                    } else {
                        if (currentFrame < 11) {
                            if (timeSinceLastAnimationUpdate > 10) {
                                currentFrame = (currentFrame + 1);
                                timeSinceLastAnimationUpdate = 0;
                            }
                        } else if (currentFrame > 11) {
                            if (timeSinceLastAnimationUpdate > 10) {
                                currentFrame = (currentFrame + 1);
                                timeSinceLastAnimationUpdate = 0;
                                if (currentFrame == 14) {
                                    currentFrame = 0;
                                    state = handState.IDLE;
                                }
                            }
                        } else if (currentFrame == 11) {
                            if (handVelocity.x == 0) {
                                //Start punching
                                punch.play();
                                if (location.x < 400) {
                                    handVelocity.x = 12;
                                } else {
                                    handVelocity.x = -12;
                                }
                                punchTimer = rand.nextInt(75) + 25;
                            }
                            if (punchTimer == 0) {
                                if (location.x < -10 || location.x > 800) {
                                    state = handState.IDLE;
                                    location.y = rand.nextInt(400) + 50;
                                } else {
                                    currentFrame += 1;
                                    timeSinceLastAnimationUpdate = 0;
                                }
                            } else {
                                punchTimer -= 1;
                            }
                        }
                    }
                    break;
                case GUN:
                    if (currentFrame < 14) {
                        currentFrame = 14;
                        timeSinceLastAnimationUpdate = 0;
                    } else {
                        if (currentFrame == 14) {
                            if (timeSinceLastAnimationUpdate > 10) {
                                currentFrame = (currentFrame + 1);
                                timeSinceLastAnimationUpdate = 0;
                            }
                        } else if (currentFrame == 15) {
                            if (timeSinceLastAnimationUpdate > 60) {
                                currentFrame = (currentFrame + 1);
                                timeSinceLastAnimationUpdate = 0;
                                int direction;
                                if (location.x > 400) direction = -1;
                                else direction = 1;
                                Vector2 bulletLocation = new Vector2(location.x, location.y + 55);
                                myBullet.fireBullet(bulletLocation, direction);
                            }
                        } else {
                            if (timeSinceLastAnimationUpdate > 10) {
                                currentFrame = (currentFrame + 1);
                                timeSinceLastAnimationUpdate = 0;
                                if (currentFrame == 20) {
                                    currentFrame = 0;
                                    state = handState.IDLE;
                                }
                            }
                        }
                    }
                    break;
                case DEATH:
                    drawInfo.setRotation(drawInfo.getRotation() + rotationVelocity);
                    location.add(handVelocity);
                    handVelocity.y -= 0.1;
                    deathTimer -= 1;
                    if (deathTimer < 0) {
                        handVelocity = new Vector2(Vector2.Zero);
                        health = score.handsDefeated * 125 + 25;
                        location = new Vector2(-100, 340);
                        state = handState.IDLE;
                        drawInfo.setRotation(0);
                        laugh.play();
                    }
                    break;
            }


            if (isInvincible()) {
                drawInfo.setColor(new Color(Color.SALMON));
            } else drawInfo.setColor(new Color(Color.WHITE));

            timeSinceLastAnimationUpdate += 1;
            if (invincibilityTimer > 0) invincibilityTimer -= 1;
            drawInfo.setX(location.x);
            drawInfo.setY(location.y);
            drawInfo.setTexture(handTextures.get(currentFrame));
        }
    }

    public void getHit(float playerSpeed) {
        if (invincibilityTimer == 0) {
            health -= Math.min(Math.abs(playerSpeed), 100);
            invincibilityTimer = 35;
            System.out.println("Health: " + health);
        }
        else {
            //TODO: tink sfx
        }
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

    public Vector2 getHandVelocity() {
        return handVelocity;
    }

    public handState getState() {
        return state;
    }

    public void updateTexture(Texture t) {
        drawInfo.setTexture(t);
    }

    public void killHand() {
        rotationVelocity = rand.nextInt(45);
        if (location.x < 400) {
            handVelocity = new Vector2(3, 7);
        }
        else {
            handVelocity = new Vector2(-3, 7);
        }
        state = handState.DEATH;
        score.handsDefeated += 1;
        deathTimer = 300;
        death.play();
        yell.play();
    }

    public boolean isInvincible() {
        return invincibilityTimer > 0;
    }
}
