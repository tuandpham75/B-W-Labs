package com.neurondigital.blackandwhite;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.neurondigital.nudge.Instance;
import com.neurondigital.nudge.Physics;
import com.neurondigital.nudge.Screen;

public class Block {

	public float x, y, speedx = 0, speedy = 0, accelerationx = 0, accelerationy = 0;
	Screen screen;
	Physics physics = new Physics();
	public int type = 0;
	Paint[] block_paint = new Paint[4];
	static int Black = 0, White = 1, Red = 2, Grey = 3;

	//animate
	int TargetY, animSpeed;
	boolean animateY = false;

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

	public Block(float x, float y, Screen screen, int type) {

		this.screen = screen;
		this.x = x;
		this.TargetY = (int) (this.y = y);
		this.type = type;

		//set paint
		block_paint[Black] = new Paint();
		block_paint[Black].setColor(screen.getResources().getColor(R.color.black));
		block_paint[White] = new Paint();
		block_paint[White].setColor(screen.getResources().getColor(R.color.white));
		block_paint[Red] = new Paint();
		block_paint[Red].setColor(screen.getResources().getColor(R.color.red));
		block_paint[Grey] = new Paint();
		block_paint[Grey].setColor(screen.getResources().getColor(R.color.grey));
	}

	//update the Object
	public void Update() {
		x += speedx;
		y += speedy;
		speedx += accelerationx;
		speedy += accelerationy;

		if (y > TargetY) {
			y -= animSpeed;
		} else if (y < TargetY) {
			y += animSpeed;
		}
		if (Math.abs(y - TargetY) <= animSpeed) {
			y = TargetY;
		}
	}

	public int getHeight() {
		return screen.ScreenHeight() / screen.getResources().getInteger(R.integer.blocks_per_row);
	}

	public int getWidth() {
		return screen.ScreenWidth() / screen.getResources().getInteger(R.integer.blocks_per_row);
	}

	public void lowLight() {
		type = Grey;
	}

	public void ChangeRed() {
		type = Red;
	}

	//draw the sprite to screen
	public void draw(Canvas canvas) {
		//draw image
		for (int i = 0; i < block_paint.length; i++) {
			if (i == type)
				canvas.drawRect(x, y, x + (screen.ScreenWidth() / 4), y + getHeight(), block_paint[i]);
		}

		if (screen.debug_mode)
			physics.drawDebug(canvas);
	}

	public boolean isTouched(MotionEvent event) {

		return physics.intersect((int) x, (int) y, getWidth(), getHeight(), (int) event.getX(), (int) event.getY());
	}

	public boolean CollidedWith(Instance b) {

		if (b.world)
			return physics.intersect((int) x, (int) y, getWidth(), getHeight(), screen.ScreenX((int) b.x), screen.ScreenY((int) b.y), b.sprite.getWidth(), (int) b.sprite.getHeight());
		else
			return physics.intersect((int) x, (int) y, getWidth(), getHeight(), (int) b.x, (int) b.y, b.sprite.getWidth(), (int) b.sprite.getHeight());

	}

	public boolean CollidedWith(int x, int y, int width, int height, boolean worldCoordinates) {

		if (worldCoordinates)
			return physics.intersect((int) this.x, (int) this.y, getWidth(), getHeight(), screen.ScreenX(x), screen.ScreenY(y), width, height);
		else
			return physics.intersect((int) this.x, (int) this.y, getWidth(), getHeight(), x, y, width, height);

	}

	public boolean inScreen() {

		return physics.intersect((int) x, (int) y, getWidth(), getHeight(), 0, 0, screen.ScreenWidth(), screen.ScreenHeight());
	}

	public Block Clone() {
		Block clone = new Block(this.accelerationx, this.accelerationy, screen, type);
		clone.speedx = this.speedx;
		clone.speedy = this.speedy;
		clone.x = this.x;
		clone.y = this.y;
		return clone;
	}

	public void setY(float y) {
		this.y = y;
		this.TargetY = (int) y;
	}

	public void animateToY(int y, int speed) {
		animateY = true;
		animSpeed = speed;
		TargetY = y;
	}

}
