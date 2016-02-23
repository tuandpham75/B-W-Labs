package com.neurondigital.blackandwhite;

import java.util.ArrayList;

import android.graphics.Canvas;

import com.neurondigital.nudge.Screen;

public class RowGenerator {
	Screen screen;
	int[][] pattern;
	ArrayList<Block[]> BlocksOnScreen = new ArrayList<Block[]>();
	int currentRow = 0;
	boolean loop = true;
	int speedy = 0;

	public RowGenerator(Screen screen, int[][] pattern, int speedy) {
		this.screen = screen;
		this.pattern = pattern;
		this.speedy = speedy;
	}

	public void setPettern(int[][] pattern) {
		this.pattern = pattern;
	}

	/* Move 1 row down. */
	public void RowDown() {

		//move all blocks down
		for (int row = 0; row < BlocksOnScreen.size(); row++) {
			for (int i = 0; i < screen.getResources().getInteger(R.integer.blocks_per_row); i++) {
				BlocksOnScreen.get(row)[i].animateToY((int) (BlocksOnScreen.get(row)[i].TargetY + BlocksOnScreen.get(row)[i].getHeight()), speedy);
			}
		}

	}

	/* Move 1 row up. */
	public void RowUp() {

		//move all blocks up
		for (int row = 0; row < BlocksOnScreen.size(); row++) {
			for (int i = 0; i < screen.getResources().getInteger(R.integer.blocks_per_row); i++) {
				BlocksOnScreen.get(row)[i].animateToY((int) (BlocksOnScreen.get(row)[i].TargetY - BlocksOnScreen.get(row)[i].getHeight()), speedy * 2);
			}
		}

	}

	/* create 1 additional row out of the screen */
	public boolean createRow() {
		if (currentRow < pattern.length) {
			Block[] row_blocks = new Block[screen.getResources().getInteger(R.integer.blocks_per_row)];
			for (int i = 0; i < screen.getResources().getInteger(R.integer.blocks_per_row); i++) {
				row_blocks[i] = new Block(i * (screen.ScreenWidth() / screen.getResources().getInteger(R.integer.blocks_per_row)), getFirstRowY() - (screen.ScreenHeight() / screen.getResources().getInteger(R.integer.blocks_per_row)), screen, pattern[currentRow][i]);
				row_blocks[i].animateToY(getFirstRowTargetY() - (screen.ScreenHeight() / screen.getResources().getInteger(R.integer.blocks_per_row)), speedy);
			}
			BlocksOnScreen.add(row_blocks);
		}

		//increment for next row of blocks
		currentRow++;
		if (currentRow >= pattern.length) {
			if (loop)
				currentRow = 0;
			else
				return false;
		}
		return true;
	}

	/* move all rows just a little up or down depending on their speed */
	public void moveDownStep() {
		//move all blocks down
		for (int row = 0; row < BlocksOnScreen.size(); row++) {
			for (int i = 0; i < screen.getResources().getInteger(R.integer.blocks_per_row); i++) {
				BlocksOnScreen.get(row)[i].Update();
			}
		}
		//create new row
		if (getFirstRowTargetY() > -(screen.ScreenHeight() / screen.getResources().getInteger(R.integer.blocks_per_row))) {
			createRow();
		}
		//delete last row if out of screen
		if (getLastRowY() > screen.ScreenHeight() + (screen.ScreenHeight() / screen.getResources().getInteger(R.integer.blocks_per_row))) {
			BlocksOnScreen.remove(0);
		}
	}

	public int getFirstRowY() {
		return (int) ((BlocksOnScreen.size() == 0) ? (0) : BlocksOnScreen.get(BlocksOnScreen.size() - 1)[0].y);
	}

	public int getLastRowY() {
		return (int) ((BlocksOnScreen.size() == 0) ? (0) : BlocksOnScreen.get(0)[0].y);
	}

	public int getFirstRowTargetY() {
		return (int) ((BlocksOnScreen.size() == 0) ? (0) : BlocksOnScreen.get(BlocksOnScreen.size() - 1)[0].TargetY);
	}

	public void loop(boolean loop) {
		this.loop = loop;
	}

	public void restart() {
		currentRow = 0;
		BlocksOnScreen.clear();
		createRow();
		createRow();
		createRow();
		createRow();

		//move all blocks 3 positions down
		for (int row = 0; row < BlocksOnScreen.size(); row++) {
			for (int i = 0; i < screen.getResources().getInteger(R.integer.blocks_per_row); i++) {
				BlocksOnScreen.get(row)[i].setY(BlocksOnScreen.get(row)[i].y + (3 * (screen.ScreenHeight() / screen.getResources().getInteger(R.integer.blocks_per_row))));
			}
		}

	}

	public void draw(Canvas canvas) {
		for (int row = 0; row < BlocksOnScreen.size(); row++) {
			for (int i = 0; i < screen.getResources().getInteger(R.integer.blocks_per_row); i++) {
				BlocksOnScreen.get(row)[i].draw(canvas);
			}
		}
	}
}
