package com.neurondigital.nudge;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public class Instance {
	public float x, y, speedx = 0, speedy = 0, accelerationx = 0, accelerationy = 0;
	public Sprite sprite;
	Screen screen;
	Physics physics = new Physics();
	public boolean world = true;
	public int tag = 0;

	/**
	 * Create new instance
	 * 
	 * @param sprite
	 *            sprite to draw on screen
	 * @param x
	 *            x-coordinate to draw instance
	 * @param y
	 *            y-coordinate to draw instance
	 * @param screen
	 *            A reference to the main nudge engine screen instance
	 * @param world
	 *            true if you wish to draw the instance relative to the camera or false if you wish to draw it relative to screen
	 */
	public Instance(Sprite sprite, float x, float y, Screen screen, boolean world) {
		this.sprite = sprite;
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.world = world;
	}

	public Instance(Sprite sprite, float x, float y, Screen screen, boolean world, int tag) {
		this.sprite = sprite;
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.world = world;
		this.tag = tag;
	}

	//update the Object
	public void Update() {
		x += speedx;
		y += speedy;
		speedx += accelerationx;
		speedy += accelerationy;
	}

	public void rotate(float direction) {
		sprite.rotate(direction);
	}

	public float getDirection() {
		return sprite.getDirection();
	}

	public int getHeight() {
		return sprite.getHeight();
	}

	public int getWidth() {
		return sprite.getWidth();
	}

	//draw the sprite to screen
	public void draw(Canvas canvas) {
		//draw image
		if (world)
			sprite.draw(canvas, screen.ScreenX((int) x), screen.ScreenY((int) y));
		else
			sprite.draw(canvas, x, y);

		if (screen.debug_mode)
			physics.drawDebug(canvas);
	}

	//draw the sprite to screen
	public void draw(Canvas canvas, Paint paint) {
		//draw image
		if (world)
			sprite.draw(canvas, screen.ScreenX((int) x), screen.ScreenY((int) y), paint);
		else
			sprite.draw(canvas, x, y, paint);

		if (screen.debug_mode)
			physics.drawDebug(canvas);
	}

	public boolean isTouched(MotionEvent event) {
		if (world)
			return physics.intersect(screen.ScreenX((int) x), screen.ScreenY((int) y), sprite.getWidth(), (int) sprite.getHeight(), (int) event.getX(), (int) event.getY());
		else
			return physics.intersect((int) x, (int) y, sprite.getWidth(), (int) sprite.getHeight(), (int) event.getX(), (int) event.getY());
	}

	public boolean CollidedWith(Instance b) {
		if (world) {
			if (b.world)
				return physics.intersect(screen.ScreenX((int) x), screen.ScreenY((int) y), sprite.getWidth(), (int) sprite.getHeight(), screen.ScreenX((int) b.x), screen.ScreenY((int) b.y), b.sprite.getWidth(), (int) b.sprite.getHeight());
			else
				return physics.intersect(screen.ScreenX((int) x), screen.ScreenY((int) y), sprite.getWidth(), (int) sprite.getHeight(), (int) b.x, (int) b.y, b.sprite.getWidth(), (int) b.sprite.getHeight());

		} else {
			if (b.world)
				return physics.intersect((int) x, (int) y, sprite.getWidth(), (int) sprite.getHeight(), screen.ScreenX((int) b.x), screen.ScreenY((int) b.y), b.sprite.getWidth(), (int) b.sprite.getHeight());
			else
				return physics.intersect((int) x, (int) y, sprite.getWidth(), (int) sprite.getHeight(), (int) b.x, (int) b.y, b.sprite.getWidth(), (int) b.sprite.getHeight());

		}
	}

	public boolean CollidedWith(int x, int y, int width, int height, boolean worldCoordinates) {
		if (world) {
			if (worldCoordinates)
				return physics.intersect(screen.ScreenX((int) this.x), screen.ScreenY((int) this.y), sprite.getWidth(), (int) sprite.getHeight(), screen.ScreenX(x), screen.ScreenY(y), width, height);
			else
				return physics.intersect(screen.ScreenX((int) this.x), screen.ScreenY((int) this.y), sprite.getWidth(), (int) sprite.getHeight(), x, y, width, height);

		} else {
			if (worldCoordinates)
				return physics.intersect((int) this.x, (int) this.y, sprite.getWidth(), (int) sprite.getHeight(), screen.ScreenX(x), screen.ScreenY(y), width, height);
			else
				return physics.intersect((int) this.x, (int) this.y, sprite.getWidth(), (int) sprite.getHeight(), x, y, width, height);

		}
	}

	public boolean inScreen() {
		if (world)
			return physics.intersect(screen.ScreenX((int) x), screen.ScreenY((int) y), sprite.getWidth(), (int) sprite.getHeight(), 0, 0, screen.ScreenWidth(), screen.ScreenHeight());
		else
			return physics.intersect((int) x, (int) y, sprite.getWidth(), (int) sprite.getHeight(), 0, 0, screen.ScreenWidth(), screen.ScreenHeight());
	}

	public Instance Clone() {
		Instance clone = new Instance(this.sprite, this.accelerationx, this.accelerationy, screen, world);
		clone.speedx = this.speedx;
		clone.speedy = this.speedy;
		clone.x = this.x;
		clone.y = this.y;
		clone.tag = this.tag;
		return clone;
	}
}
