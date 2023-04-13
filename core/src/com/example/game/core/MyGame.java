package com.example.game.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;


import com.badlogic.gdx.Input.Keys;

public class MyGame extends ApplicationAdapter {
	private Texture dropImg;
	private Texture bucketImg, background;
	private Sound drop_sound;
	private Music rain_music;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long last_drop_time;

	@Override
	public void create() {
		dropImg = new Texture(Gdx.files.internal("droplet.png"));
		bucketImg = new Texture(Gdx.files.internal("bucket.png"));
		background = new Texture(Gdx.files.internal("background.jpg"));

		drop_sound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rain_music = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		rain_music.setLooping(true);
		rain_music.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		bucket = new Rectangle();
		bucket.x = 800 / 2f - 64 / 2f;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		raindrops = new Array<>();
		spawnRaindrop();
	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		last_drop_time = TimeUtils.nanoTime();
	}

	@Override
	public void render() {

		ScreenUtils.clear(0, 0, 0.2f, 1);

		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.draw(background, 0 , 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		batch.begin();
		batch.draw(bucketImg, bucket.x, bucket.y);
		for(Rectangle raindrop: raindrops) {
			batch.draw(dropImg, raindrop.x, raindrop.y);
		}
		batch.end();

		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2f;
		}
		if(Gdx.input.isKeyPressed(Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

		if(bucket.x < 0) bucket.x = 0;
		if(bucket.x > 800 - 64) bucket.x = 800 - 64;

		if(TimeUtils.nanoTime() - last_drop_time > 1000000000) spawnRaindrop();

		for (Iterator<Rectangle> it = raindrops.iterator(); it.hasNext(); ) {
			Rectangle raindrop = it.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0) it.remove();
			if(raindrop.overlaps(bucket)) {
				drop_sound.play();
				it.remove();
			}
		}
	}

	@Override
	public void dispose() {
		dropImg.dispose();
		bucketImg.dispose();
		background.dispose();
		drop_sound.dispose();
		rain_music.dispose();
		batch.dispose();
	}
}
