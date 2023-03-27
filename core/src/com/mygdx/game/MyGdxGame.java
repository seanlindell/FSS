package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	private Rectangle bucket;
	private Array<Texture> characters;
	private Array<Texture> handTextures;

	private Array<Texture> bulletTextures;
	private Texture currentCharacterTexture;
	private Player player;
	private Hand hand;
	private Hand hand2;

	private Bullet bullet;
	private Bullet bullet2;
	private Texture arrow;
	private Texture fd;
	private Sound dropSound;

	private Sound hit1;
	private Sound hit2;
	private Sound hit3;
	private Sound thrown;
	private Sound shoot;
	private Sound handDeath;
	private Sound sadyell;
	private Sound laugh1;
	private Sound laugh2;
	private Sound punch;


	private Music rainMusic;
	private Music fdr;

	private Score score;

	private int timeLeftInHitpause;
	private int hitpauseCooldown;

	private Texture howto;
	private BitmapFont font;
	private Random random;
	
	@Override
	public void create () {
		random = new Random();
		hit1 = Gdx.audio.newSound(Gdx.files.internal("hit1.wav"));
		hit2 = Gdx.audio.newSound(Gdx.files.internal("hit2.wav"));
		hit3 = Gdx.audio.newSound(Gdx.files.internal("hit3.wav"));
		thrown = Gdx.audio.newSound(Gdx.files.internal("throw.wav"));
		shoot = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));
		handDeath = Gdx.audio.newSound(Gdx.files.internal("handdeath.wav"));
		sadyell = Gdx.audio.newSound(Gdx.files.internal("sadyell.wav"));
		laugh1 = Gdx.audio.newSound(Gdx.files.internal("laugh1.wav"));
		laugh2 = Gdx.audio.newSound(Gdx.files.internal("laugh2.wav"));
		punch = Gdx.audio.newSound(Gdx.files.internal("punch.wav"));

		howto = new Texture(Gdx.files.internal("howto.png"));
		font = new BitmapFont();
		score = new Score();

		String[] charFileNames = new String[]{"MARIO", "DONKEYKONG", "LINK", "SAMUS", "YOSHI", "KIRBY", "FOX", "PIKACHU", "LUIGI", "NESS", "CAPTAINFALCON", "JIGGLYPUFF", "PEACH", "BOWSER", "ICECLIMBERS", "SHEIK", "ZELDA", "DRMARIO", "PICHU", "FALCO", "MARTH", "YOUNGLINK", "GANONDORF", "MEWTWO", "ROY", "MRGAMEWATCH", "METAKNIGHT", "PIT", "ZEROSUITSAMUS", "WARIO", "SNAKE", "IKE", "POKEMONTRAINER", "DIDDYKONG", "LUCAS", "SONIC", "KINGDEDEDE", "OLIMAR", "LUCARIO", "ROB", "TOONLINK", "WOLF", "VILLAGER", "MEGAMAN", "WIIFITTRAINER", "ROSALINALUMA", "LITTLEMAC", "GRENINJA", "MIIBRAWLER", "MIISWORDFIGHTER", "MIIGUNNER", "PALUTENA", "DARKPIT", "PACMAN", "LUCINA", "ROBIN", "SHULK", "BOWSERJR", "DUCKHUNT", "RYU", "CLOUD", "CORRIN", "BAYONETTA", "INKLING", "DAISY", "RIDLEY", "SIMON", "RICHTER", "CHROM", "DARKSAMUS", "KINGKROOL", "ISABELLE", "KEN", "INCINEROAR", "PIRANHAPLANT", "JOKER", "HERO", "BANJOKAZOOIE", "TERRY", "BYLETH", "MINMIN", "STEVE", "SEPHIROTH", "PYRAMYTHRA", "KAZUYA", "SORA"};
		characters = new Array<Texture>();
		for (int i = 0; i < charFileNames.length; i++) {
			characters.add(new Texture(Gdx.files.internal(charFileNames[i] + ".png")));
		}
		currentCharacterTexture = characters.random();
		player = new Player(characters, score, thrown);


		String[] bulletFileNames = new String[]{"fireball1", "fireball2", "fireball3", "fireball4"};
		bulletTextures = new Array<Texture>();
		for (int i = 0; i < bulletFileNames.length; i++) {
			bulletTextures.add(new Texture(Gdx.files.internal(bulletFileNames[i] + ".png")));
		}
		bullet = new Bullet(bulletTextures, shoot);
		bullet2 = new Bullet(bulletTextures, shoot);

		String[] handFileNames = new String[]{"hand_idle1", "hand_idle2", "hand_idle3", "hand_idle4", "hand_idle5", "hand_idle6", "hand_idle7", "hand_idle8", "hand_fist1", "hand_fist2", "hand_fist3", "hand_fist4", "hand_fist5", "hand_fist6", "hand_shoot1", "hand_shoot2", "hand_shoot3", "hand_shoot4", "hand_shoot5", "hand_shoot6"};
		handTextures = new Array<Texture>();
		for (int i = 0; i < handFileNames.length; i++) {
			handTextures.add(new Texture(Gdx.files.internal(handFileNames[i] + ".png")));
		}
		hand = new Hand(handTextures, bullet, score, 1, laugh1, handDeath, punch, sadyell);
		hand2 = new Hand(handTextures, bullet2, score, 2, laugh2, handDeath, punch, sadyell);



		fd = new Texture((Gdx.files.internal("fd.png")));
		arrow = new Texture(Gdx.files.internal("arrow.png"));

		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));



		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		fdr = Gdx.audio.newMusic(Gdx.files.internal("fdr.mp3"));

		// start the playback of the background music immediately
//		rainMusic.setLooping(true);
//		rainMusic.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		batch = new SpriteBatch();
		//batch.enableBlending();
		//batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA);

		timeLeftInHitpause = 0;
		hitpauseCooldown = 0;
	}

	@Override
	public void render () {
		if (hitpauseCooldown > 0) {
			hitpauseCooldown -= 1;
		}
		if (timeLeftInHitpause > 0) {
			if (hitpauseCooldown > 0) {
				timeLeftInHitpause = 0;
			}
			else {
				timeLeftInHitpause -= 1;
				if (timeLeftInHitpause == 0) {
					hitpauseCooldown = 45;
					float rn = random.nextFloat();
					if (rn > 0.66) {
						hit1.play();
					}
					else if (rn < 0.33) {
						hit2.play();
					}
					else {
						hit3.play();
					}
				}
				return;
			}
		}

		ScreenUtils.clear(0, 0, 0.2f, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(fd, 0, -50, 800, 530);
		if (!score.gameStarted) batch.draw(howto, 200,200, 400, 200);
		hand.drawInfo.draw(batch);
		batch.setColor(new Color(Color.WHITE));
		hand2.drawInfo.draw(batch);
		batch.setColor(new Color(Color.WHITE));
		bullet.drawInfo.draw(batch);
		bullet2.drawInfo.draw(batch);
		player.drawInfo.draw(batch);
		if (player.getY()>480) {
			batch.draw(arrow, player.getX(), 465);
		}

		font.draw(batch, "Hands Defeated: " + score.handsDefeated, 10, 20);
		font.draw(batch, "Stocks Remaining: " + score.stocksRemaining, 638, 20);
		if (score.gameOver) {
			font.setColor(Color.RED);
			font.draw(batch, "GAME OVER" , 270, 340, 250, 1, false);
		}
		batch.end();

		if (player.drawInfo.getBoundingRectangle().overlaps(hand.drawInfo.getBoundingRectangle()) && hand.getState() != Hand.handState.DEATH) {
			if (player.getState() == Player.playerState.FLING) {
				if (player.getVelocity().y < 0) {
					player.setVelocityY(player.getVelocity().y * -1);
				}
				if (player.getLocation().x + 24 > 400) {
					player.setVelocityX(Math.abs(player.getVelocity().x) * -1);
				}
				if (player.getLocation().x + 24 < 400) {
					player.setVelocityX(Math.abs(player.getVelocity().x));
				}
				hand.getHit(player.getRotationVelocity());
			}
			if (hand.currentFrame == 11 && Math.abs(hand.getHandVelocity().x) > 0) {
				player.putInHitstun();
				player.setVelocityX(hand.getHandVelocity().x * 0.75f);
			}
			timeLeftInHitpause = 7;
		}
		if (player.drawInfo.getBoundingRectangle().overlaps(hand2.drawInfo.getBoundingRectangle()) && hand2.getState() != Hand.handState.DEATH) {
			if (player.getState() == Player.playerState.FLING) {
				if (player.getVelocity().y < 0) {
					player.setVelocityY(player.getVelocity().y * -1);
				}
				if (player.getLocation().x + 24 > 400) {
					player.setVelocityX(Math.abs(player.getVelocity().x) * -1);
				}
				if (player.getLocation().x + 24 < 400) {
					player.setVelocityX(Math.abs(player.getVelocity().x));
				}
				hand2.getHit(player.getRotationVelocity());
			}
			if (hand2.currentFrame == 11 && Math.abs(hand2.getHandVelocity().x) > 0) {
				player.putInHitstun();
				player.setVelocityX(hand2.getHandVelocity().x * 0.75f);
			}
			timeLeftInHitpause = 7;
		}

		if (player.drawInfo.getBoundingRectangle().overlaps(bullet.drawInfo.getBoundingRectangle())) {
			player.putInHitstun();
			player.setVelocityX(20 * bullet.myDirection);
			timeLeftInHitpause = 7;
		}
		if (player.drawInfo.getBoundingRectangle().overlaps(bullet2.drawInfo.getBoundingRectangle())) {
			player.putInHitstun();
			player.setVelocityX(20 * bullet2.myDirection);
			timeLeftInHitpause = 7;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && score.gameStarted == false) {
			score.gameStarted = true;
			fdr.setLooping(true);
			fdr.play();
		}
		Vector3 mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		if(Gdx.input.justTouched()) {
			switch (player.getState()) {
				case NORMAL:
					player.jump();
					break;
				case TETHERED:
					player.fling(mouse.x, mouse.y);
					break;
				case HITSTUN:
					break;
				case FLING:
					if (Math.abs(player.getRotationVelocity())<18) {
						player.makeNormal();
						player.jump();
					}
					break;
			}

		}


		player.update(mouse.x, mouse.y);
		bullet.update();
		bullet2.update();

		if (hand.health < 0) {
			hand.killHand();
			hitpauseCooldown = 0;
			timeLeftInHitpause = 40;
			hand.health = 900;
		}

		if (hand2.health < 0) {
			hand2.killHand();
			hitpauseCooldown = 0;
			timeLeftInHitpause = 40;
			hand2.health = 900;
		}

		hand.update(mouse.x, mouse.y);
		hand2.update(mouse.x, mouse.y);

	}

	@Override
	public void dispose() {
		currentCharacterTexture.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}

}
