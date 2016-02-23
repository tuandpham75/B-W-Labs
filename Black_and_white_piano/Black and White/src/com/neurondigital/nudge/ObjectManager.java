package com.neurondigital.nudge;

import java.util.ArrayList;

import android.graphics.Canvas;

public class ObjectManager {
	public int time = 0;
	Screen screen;
	Instance DifferentObjects[];
	Boolean DestroyOutOfScreen = true;
	int Direction = 0;
	float lowBound, highBound;

	public static final int TOP = 0, LEFT = 1, RIGHT = 2, BOTTOM = 3;

	ArrayList<Integer> starttime = new ArrayList<Integer>();
	ArrayList<Integer> endtime = new ArrayList<Integer>();
	ArrayList<Integer[]> objecttypes = new ArrayList<Integer[]>();
	ArrayList<Integer> generation_frequency = new ArrayList<Integer>();
	public ArrayList<Instance> Objects_live = new ArrayList<Instance>();

	/**
	 * Generates moving objects selected from DifferentObjects[] in a particular direction
	 * 
	 * @param DifferentObjects
	 *            [] An array of instances to select from randomly
	 * @param screen
	 *            an instance of Screen
	 * @param DestroyOutOfScreen
	 *            if true, objects out of screen will be destroyed automatically
	 * @param Direction
	 *            ObjectManager.TOP,ObjectManager.LEFT,.. the direction the objects are generated from
	 * @param lowBound
	 *            a float from 0-1f indicating the lower margin that should not contain any objects
	 * @param highBound
	 *            a float from 0-1f indicating the higher margin that should not contain any objects
	 */
	public ObjectManager(Instance DifferentObjects[], Screen screen, Boolean DestroyOutOfScreen, int Direction, float lowBound, float highBound) {
		this.DifferentObjects = DifferentObjects;
		time = 0;
		this.screen = screen;
		this.DestroyOutOfScreen = DestroyOutOfScreen;
		this.Direction = Direction;
		this.lowBound = lowBound;
		this.highBound = highBound;
	}

	public void restart() {
		time = 0;
	}

	public void add_timeperiod(int starttime, int endtime, Integer[] obstacletypes, int generation_frequency) {
		this.starttime.add(starttime);
		this.endtime.add(endtime);
		this.objecttypes.add(obstacletypes);
		this.generation_frequency.add(generation_frequency);
	}

	public void update() {
		time++;
		//generate objects
		for (int i = 0; i < starttime.size(); i++)
			generate_at_timeperiod(starttime.get(i), endtime.get(i), objecttypes.get(i), generation_frequency.get(i));

		//update all onscreen objects
		for (int i = 0; i < Objects_live.size(); i++) {
			Objects_live.get(i).Update();
			if (DestroyOutOfScreen && !Objects_live.get(i).inScreen()) {
				//destroy it
				if (Direction == LEFT && Objects_live.get(i).x > screen.ScreenWidth())
					Objects_live.remove(i);
				if (Direction == RIGHT && Objects_live.get(i).x < 0)
					Objects_live.remove(i);
				if (Direction == TOP && Objects_live.get(i).x > screen.ScreenHeight())
					Objects_live.remove(i);
				if (Direction == BOTTOM && Objects_live.get(i).x < 0)
					Objects_live.remove(i);
			}
		}

	}

	public void drawObjects(Canvas canvas) {
		//draw all onscreen objects
		for (int i = 0; i < Objects_live.size(); i++)
			Objects_live.get(i).draw(canvas);
	}

	private void generate_at_timeperiod(int starttime, int endtime, Integer[] obstacletypes, int generation_frequency) {
		if (((time >= starttime) && (endtime == -1)) || ((time >= starttime) && (time < endtime))) {
			if (time % (100 - generation_frequency) == 0) {
				int type = (int) (Math.random() * obstacletypes.length) + 1;
				Objects_live.add(DifferentObjects[obstacletypes[type - 1]].Clone());
				if (Direction == RIGHT) {
					Objects_live.get(Objects_live.size() - 1).x = screen.ScreenWidth() + Objects_live.get(Objects_live.size() - 1).getWidth();
					Objects_live.get(Objects_live.size() - 1).y = (float) ((screen.ScreenHeight() * (1 - lowBound - highBound) * Math.random()) + (screen.ScreenHeight() * lowBound));
				}
				if (Direction == LEFT) {
					Objects_live.get(Objects_live.size() - 1).x = -Objects_live.get(Objects_live.size() - 1).getWidth();
					Objects_live.get(Objects_live.size() - 1).y = (float) ((screen.ScreenHeight() * (1 - lowBound - highBound) * Math.random()) + (screen.ScreenHeight() * lowBound));
				}
				if (Direction == TOP) {
					Objects_live.get(Objects_live.size() - 1).y = -Objects_live.get(Objects_live.size() - 1).getHeight();
					Objects_live.get(Objects_live.size() - 1).x = (float) ((screen.ScreenWidth() * (1 - lowBound - highBound) * Math.random()) + (screen.ScreenWidth() * lowBound));
				}
				if (Direction == BOTTOM) {
					Objects_live.get(Objects_live.size() - 1).y = screen.ScreenHeight() + Objects_live.get(Objects_live.size() - 1).getHeight();
					Objects_live.get(Objects_live.size() - 1).x = (float) ((screen.ScreenWidth() * (1 - lowBound - highBound) * Math.random()) + (screen.ScreenWidth() * lowBound));
				}

			}
		}
	}
}
