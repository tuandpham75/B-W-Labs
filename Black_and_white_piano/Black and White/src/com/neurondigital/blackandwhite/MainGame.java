package com.neurondigital.blackandwhite;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.neurondigital.nudge.Button;
import com.neurondigital.nudge.Screen;
import com.neurondigital.nudge.Share;
import com.neurondigital.nudge.SingleScore;
import com.neurondigital.nudge.Sprite;

public class MainGame extends Screen {

	//paints
	Paint background_shader = new Paint();
	Paint Title_Paint = new Paint();
	Paint SubTitle_Paint = new Paint();
	Paint Score_Paint = new Paint();
	Paint Instruction_Paint = new Paint();
	Paint Instruction_Paint2 = new Paint();
	Paint gameover_score_stroke = new Paint();
	Paint gameover_score_paint = new Paint();
	Paint GameOver_Paint = new Paint();
	Paint Line_paint = new Paint();

	//states
	final int MENU = 0, GAMEPLAY = 1, GAMEOVER = 2;
	int state = MENU;
	boolean notstarted = true;

	//menu buttons
	Button btn_Classic, btn_Zen, btn_Arcade, btn_Bomb, btn_Highscores, btn_Exit, btn_Home, btn_share, btn_Replay, btn_sound_mute, btn_rate;

	//score
	int score_black_tapped = 0;
	int score_time = 0;
	int[] topscores = new int[4];

	//sound
	SoundPool sp;
	MediaPlayer music;
	int sound_click, sound_gameover;
	boolean sound_muted = false;
	Sprite sound_on, sound_off;
	int[] sound_effect_notes;

	//ad
	private InterstitialAd interstitial;
	int ad_counter = 0;
	AdRequest adRequest;

	//game over counter
	int gameover_counter = 0;
	boolean game_over = false, success = true;

	//note counter
	int currentNote = 0, randomSong = 0;

	//gamePlay
	final int Classic = 0, Arcade = 1, Zen = 2, Bomb = 3;
	final int[] GamePlayMode_name_ref = new int[] { R.string.Classic, R.string.Arcade, R.string.Zen, R.string.Bomb };
	int GamePlayMode = 0;
	int moving_speed;

	//time keeping
	private long now = SystemClock.elapsedRealtime(), lastTick, last_row_moved;

	//Block generator
	RowGenerator BlockGenerator;

	//TODO: you may change the gameplay variables below
	//the delay from game over to when the green gameover screen is displayed
	int gameover_delay = 20;

	//list of notes recorded from piano
	int[] sound_effect_notes_ref = new int[] { R.raw.note1, R.raw.note2, R.raw.note3, R.raw.note4, R.raw.note5, R.raw.note6, R.raw.note7, R.raw.note8, R.raw.note9, R.raw.note10, R.raw.note11, R.raw.note12, R.raw.note13, R.raw.note14, R.raw.note15, R.raw.note16, R.raw.note17, R.raw.note18, R.raw.note19, R.raw.note20, R.raw.note21 };

	//music notes. Each one of the 3 lists below represents a song. One of them is selected randomly. The numbers indicate the note to play as the user hits the black tiles.
	//TODO: You may wish to play with the numbers to create your own song
	int[][] music_notes = new int[][]
	{
			{ 8, 15, 10, 15, 12, 15, 12, 17, 12, 15, 12, 16, 12, 16, 12, 11, 12, 16, 12, 11, 12, 16, 12, 11, 12, 18, 12, 16, 12, 17, 12, 10, 12, 17, 12, 10, 12, 17, 12, 10, 17, 18, 12, 17, 12, 18, 13, 11, 18, 18, 13, 17, 13, 12, 11, 9, 11, 14, 11, 10, 9, 15, 12, 10, 12, 8 },
			{ 8, 10, 12, 15, 9, 11, 13, 16, 11, 13, 15, 18, 15, 13, 11, 16, 13, 11, 9, 15, 12, 10 },
			{ 10, 11, 12, 17, 15, 16, 15, 15, 14, 14, 9, 10, 11, 16, 14, 15, 14, 13, 12, 12, 10, 11, 12, 15, 16, 17, 16, 15, 13, 16, 17, 18, 17, 16, 12, 13, 12, 11 },

	};

	//classic/arcade/zen patterns. A pattern is selected randomly when game starts. 
	//1:white tile, 0:black tile
	//TODO: you may add another pattern or change the numbers to modify the patterns
	int[][][] classic_arcade_zen_pattern = new int[][][]
	{
			{
			{ 0, 1, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 1, 0 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 0, 1 },
			{ 1, 0, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 1, 1, 0, 1 },
			{ 0, 1, 1, 1 },
			{ 1, 1, 1, 0 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 1, 0 },
			{ 1, 0, 1, 1 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 1, 0 },
			{ 0, 1, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 0, 1 },
			{ 0, 1, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 1, 0 },
			{ 1, 1, 1, 0 },
			{ 1, 1, 0, 1 },
			{ 1, 0, 1, 1 },
			{ 0, 1, 1, 1 },
			{ 1, 1, 1, 0 },
			{ 1, 1, 0, 1 },
			{ 1, 0, 1, 1 },
			{ 0, 1, 1, 1 },
			{ 0, 1, 1, 1 },
			{ 1, 1, 1, 0 },
			{ 0, 1, 1, 1 },
			{ 1, 1, 1, 0 }
	},

			{
			{ 0, 1, 1, 1 },
			{ 0, 1, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 0, 1, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 0, 1, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 0, 1, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 1, 1, 0, 1 },
			{ 0, 1, 1, 1 },
			{ 1, 1, 1, 0 },
			{ 1, 1, 0, 1 },
			{ 0, 1, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 1, 1, 1, 0 },
			{ 1, 1, 1, 0 },
			{ 0, 1, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 1, 1, 1, 0 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 1, 0 },
			{ 1, 0, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 1, 1, 1, 0 },
			{ 1, 1, 1, 0 },
			{ 1, 0, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 0, 1, 1, 1 },
			{ 1, 1, 1, 0 },
			{ 1, 1, 1, 0 },
			{ 1, 1, 1, 0 },
			{ 0, 1, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 1, 0 },
			{ 1, 1, 1, 0 }
	}
	};

	//bomb pattern.
	//1:white tile, 0:black tile, 2:bomb (red tile)
	//TODO: you may Change the numbers to modify the pattern.
	int[][] bomb_pattern = new int[][]
	{
			{ 0, 1, 1, 1 },
			{ 1, 0, 1, 2 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 2, 0 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 0, 1 },
			{ 1, 0, 1, 2 },
			{ 1, 0, 1, 1 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 1, 0 },
			{ 0, 1, 1, 1 },
			{ 1, 0, 1, 2 },
			{ 0, 1, 1, 1 },
			{ 1, 0, 2, 1 },
			{ 0, 1, 1, 1 },
			{ 1, 0, 1, 1 },
			{ 0, 1, 1, 2 },
			{ 0, 1, 1, 1 },
			{ 0, 1, 2, 1 },
			{ 0, 1, 1, 1 },
			{ 1, 1, 1, 0 },
			{ 1, 1, 0, 1 },
			{ 1, 0, 1, 1 },
			{ 1, 1, 2, 0 },
			{ 1, 0, 1, 1 },
			{ 1, 1, 2, 0 },
			{ 1, 1, 0, 1 },
			{ 1, 1, 1, 0 },
			{ 1, 1, 2, 0 },
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//setDebugMode(true);
		//initialiseAccelerometer();

		if (getResources().getString(R.string.InterstitialAd_unit_id).length() > 0) {
			// Create the interstitial
			interstitial = new InterstitialAd(this);
			interstitial.setAdUnitId(getResources().getString(R.string.InterstitialAd_unit_id));

			// Create ad request.
			adRequest = new AdRequest.Builder()
					.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
					.addTestDevice("275D94C2B5B93B3C4014933E75F92565")///nexus7//////testing
					.addTestDevice("91608B19766D984A3F929C31EC6AB947") /////////////////testing//////////////////remove///////////
					.addTestDevice("6316D285813B01C56412DAF4D3D80B40") ///test htc sensesion xl
					.addTestDevice("8C416F4CAF490509A1DA82E62168AE08")//asus transformer
					.addTestDevice("7B4C6D080C02BA40EF746C4900BABAD7")//Galaxy S4
					.build();
		}
		//initialise banner ad
		this.BANNER_AD_UNIT_ID = getResources().getString(R.string.BannerAd_unit_id);
		showBanner();

	}

	public void openAd() {
		if (getResources().getString(R.string.InterstitialAd_unit_id).length() > 0) {
			runOnUiThread(new Runnable() {
				public void run() {
					if (!interstitial.isLoaded()) {
						interstitial.loadAd(adRequest);
					}
					interstitial.setAdListener(new AdListener() {
						public void onAdLoaded() {
							interstitial.show();
						}

					});

				}
			});
		}
	}

	@Override
	public void Start() {
		super.Start();
		//fonts
		Typeface Orbitron = Typeface.createFromAsset(getAssets(), "Orbitron-Medium.ttf");
		//TODO: change any font sizes from here.
		//set paints
		//title
		Title_Paint.setTextSize(dpToPx(35));
		Title_Paint.setAntiAlias(true);
		Title_Paint.setColor(getResources().getColor(R.color.black));
		Title_Paint.setTypeface(Orbitron);

		//gameover paint - used for classic/Arcade/Zen/Bomb mode
		GameOver_Paint.setTextSize(dpToPx(45));
		GameOver_Paint.setAntiAlias(true);
		GameOver_Paint.setColor(getResources().getColor(R.color.white));
		GameOver_Paint.setTypeface(Orbitron);

		//SubTitle Paint
		SubTitle_Paint.setTextSize(dpToPx(20));
		SubTitle_Paint.setAntiAlias(true);
		SubTitle_Paint.setColor(getResources().getColor(R.color.white));
		SubTitle_Paint.setTypeface(Orbitron);

		//gameover score stroke
		gameover_score_stroke.setTextSize(dpToPx(50));
		gameover_score_stroke.setAntiAlias(true);
		gameover_score_stroke.setStrokeWidth(dpToPx(3));
		gameover_score_stroke.setColor(getResources().getColor(R.color.black));
		gameover_score_stroke.setTypeface(Orbitron);
		gameover_score_stroke.setStyle(Style.STROKE);
		gameover_score_stroke.setStrokeJoin(Join.ROUND);
		gameover_score_stroke.setStrokeMiter(10);

		//gameover score paint
		gameover_score_paint.setTextSize(dpToPx(50));
		gameover_score_paint.setAntiAlias(true);
		gameover_score_paint.setColor(getResources().getColor(R.color.white));
		gameover_score_paint.setTypeface(Orbitron);

		//score Paint
		Score_Paint.setTextSize(dpToPx(25));
		Score_Paint.setAntiAlias(true);
		Score_Paint.setColor(getResources().getColor(R.color.red));
		Score_Paint.setTypeface(Orbitron);

		//Instruction Paint
		Instruction_Paint.setTextSize(dpToPx(15));
		Instruction_Paint.setAntiAlias(true);
		Instruction_Paint.setColor(getResources().getColor(R.color.black));
		Instruction_Paint.setTypeface(Orbitron);

		Instruction_Paint2.setTextSize(dpToPx(20));
		Instruction_Paint2.setAntiAlias(true);
		Instruction_Paint2.setColor(getResources().getColor(R.color.black));
		Instruction_Paint2.setTypeface(Orbitron);

		//lines between blocks
		Line_paint.setAntiAlias(true);
		Line_paint.setColor(getResources().getColor(R.color.black));

		//get menu ready______________________________________________________________________________
		//classic button
		btn_Classic = new Button(getResources().getString(R.string.Classic), 30, Orbitron, getResources().getColor(R.color.white), 0, 0, ScreenWidth() / 2, ScreenWidth() / 2, getResources().getColor(R.color.black), this, false);
		btn_Classic.x = 0;
		btn_Classic.y = ScreenHeight() - btn_Classic.getHeight() * 2;

		//Arcade button
		btn_Arcade = new Button(getResources().getString(R.string.Arcade), 30, Orbitron, getResources().getColor(R.color.black), 0, 0, ScreenWidth() / 2, ScreenWidth() / 2, getResources().getColor(R.color.white), this, false);
		btn_Arcade.x = btn_Classic.getWidth();
		btn_Arcade.y = ScreenHeight() - btn_Classic.getHeight() * 2;

		//Zen button
		btn_Zen = new Button(getResources().getString(R.string.Zen), 30, Orbitron, getResources().getColor(R.color.black), 0, 0, ScreenWidth() / 2, ScreenWidth() / 2, getResources().getColor(R.color.white), this, false);
		btn_Zen.x = 0;
		btn_Zen.y = ScreenHeight() - btn_Classic.getHeight();

		//Bomb button
		btn_Bomb = new Button(getResources().getString(R.string.Bomb), 30, Orbitron, getResources().getColor(R.color.white), 0, 0, ScreenWidth() / 2, ScreenWidth() / 2, getResources().getColor(R.color.black), this, false);
		btn_Bomb.x = btn_Bomb.getWidth();
		btn_Bomb.y = ScreenHeight() - btn_Classic.getHeight();

		//menu buttons_________________________________________________________________________________

		//rate button
		btn_rate = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.rate), ScreenWidth() * 0.15f), 0, 0, this, false);
		btn_rate.x = (ScreenWidth() / 2) + btn_rate.getWidth() * 0.2f;
		btn_rate.y = btn_Classic.y - btn_rate.getHeight() * 1.5f;

		//exit button
		btn_Exit = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.exit), ScreenWidth() * 0.13f), 0, 0, this, false);
		btn_Exit.x = ScreenWidth() / 2 + btn_rate.getWidth() * 0.2f + btn_Exit.getWidth() * 1.6f;
		btn_Exit.y = btn_Classic.y - btn_Exit.getHeight() * 1.5f;

		//highscores button
		btn_Highscores = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.highscore), ScreenWidth() * 0.15f), 0, 0, this, false);
		btn_Highscores.x = ScreenWidth() / 2 - btn_Highscores.getWidth() * 1.4f;
		btn_Highscores.y = btn_Classic.y - btn_Highscores.getHeight() * 1.4f;

		//sound buttons
		sound_off = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.sound_off), ScreenWidth() * 0.15f);
		sound_on = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.sound_on), ScreenWidth() * 0.15f);

		//sound mute
		btn_sound_mute = new Button(sound_on, 0, 0, this, false);
		btn_sound_mute.x = ScreenWidth() / 2 - btn_sound_mute.getWidth() * 1.3f - btn_Highscores.getWidth() * 1.4f;
		btn_sound_mute.y = btn_Classic.y - btn_sound_mute.getHeight() * 1.6f;

		//gameover buttons____________________________________________________________________________

		//share button
		btn_share = new Button(getResources().getString(R.string.Share), 35, Orbitron, getResources().getColor(R.color.white), 0, 0, this, false);
		btn_share.x = (ScreenWidth() / 2) - btn_share.getWidth() / 2;
		btn_share.y = (ScreenHeight() * 0.64f);

		//home button
		btn_Home = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.menu), ScreenWidth() * 0.15f), 0, 0, this, false);
		btn_Home.x = (ScreenWidth() / 2) - btn_share.getWidth() * 0.75f - (btn_Home.getWidth());
		btn_Home.y = (ScreenHeight() * 0.62f);

		//replay button
		btn_Replay = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.replay), ScreenWidth() * 0.15f), 0, 0, this, false);
		btn_Replay.x = (ScreenWidth() / 2) + btn_share.getWidth() * 0.75f;
		btn_Replay.y = (ScreenHeight() * 0.62f);

		//initialise blocks
		BlockGenerator = new RowGenerator(screen, classic_arcade_zen_pattern[1], dpToPx(28));

		//initialise sound fx
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

		//initialise music and sound effects
		//TODO: you can rename sound files from here
		sound_click = sp.load(activity, R.raw.click, 1);
		sound_gameover = sp.load(activity, R.raw.gameover, 1);

		sound_effect_notes = new int[sound_effect_notes_ref.length];
		for (int i = 0; i < sound_effect_notes_ref.length; i++) {
			sound_effect_notes[i] = sp.load(activity, sound_effect_notes_ref[i], 1);
		}

	}

	@Override
	synchronized public void Step() {
		super.Step();
		if (state == MENU) {

		} else if (state == GAMEPLAY) {

			//things to pause
			if (!notstarted && !game_over) {
				//step of each mode
				if (GamePlayMode == Classic) {
					//update clock
					now = SystemClock.elapsedRealtime();
					if (now - lastTick > 10) {//every 10ms

						//add time to score
						score_time += (now - lastTick);
						lastTick = SystemClock.elapsedRealtime();
					}

				} else if (GamePlayMode == Arcade) {
					//update clock
					now = SystemClock.elapsedRealtime();
					if (now - last_row_moved > (1000 - moving_speed)) {//every 10ms

						last_row_moved = SystemClock.elapsedRealtime();
						BlockGenerator.speedy = (moving_speed / 10);
						//move row down
						BlockGenerator.RowDown();
						if (moving_speed < 1000)
							moving_speed += getResources().getInteger(R.integer.arcade_acceleration);
					}

				} else if (GamePlayMode == Zen) {
					//update clock
					now = SystemClock.elapsedRealtime();
					if (now - lastTick > 10) {//every 10ms

						//reduce time from score
						if (score_time - (now - lastTick) <= 0) {
							//time up - zen mode
							game_over = true;
							success = true;
							score_time = 0;
						} else
							score_time -= (now - lastTick);
						lastTick = SystemClock.elapsedRealtime();

					}

				} else if (GamePlayMode == Bomb) {
					//update clock
					now = SystemClock.elapsedRealtime();
					if (now - last_row_moved > (1000 - moving_speed)) {//every 10ms

						last_row_moved = SystemClock.elapsedRealtime();
						BlockGenerator.speedy = (moving_speed / 10);
						//move row down
						BlockGenerator.RowDown();
						if (moving_speed < 1000)
							moving_speed += getResources().getInteger(R.integer.bomb_acceleration);
					}
				}

				//if black block went out of screen gameover
				for (int i = 0; i < screen.getResources().getInteger(R.integer.blocks_per_row); i++) {
					if (BlockGenerator.BlocksOnScreen.get(0)[i].type == Block.Black && BlockGenerator.getLastRowY() > ScreenHeight()) {
						//black escaped from screen
						game_over = true;
						success = false;
						BlockGenerator.RowUp();
						BlockGenerator.RowUp();
						BlockGenerator.BlocksOnScreen.get(0)[i].ChangeRed();
						if (sound_gameover != 0 && !sound_muted)
							sp.play(sound_gameover, 1, 1, 0, 0, 1);
					}
				}

			}
			BlockGenerator.moveDownStep();

			//check for game over
			if (game_over)
				gameover_counter++;
			else
				gameover_counter = 0;
			if (gameover_counter > gameover_delay)
				GameOver();

		}

	}

	@Override
	public synchronized void onAccelerometer(PointF point) {

	}

	@Override
	public synchronized void BackPressed() {
		if (state == GAMEPLAY) {
			state = MENU;
		} else if (state == MENU) {
			Exit();

		} else if (state == GAMEOVER) {
			state = MENU;
		}
	}

	@Override
	public synchronized void onTouch(float TouchX, float TouchY, MotionEvent event) {

		if (state == MENU) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_Classic.isTouched(event)) {
					btn_Classic.Highlight(getResources().getColor(R.color.black));
				}
				if (btn_Arcade.isTouched(event)) {
					btn_Arcade.Highlight(getResources().getColor(R.color.black));
				}
				if (btn_Zen.isTouched(event)) {
					btn_Zen.Highlight(getResources().getColor(R.color.black));
				}
				if (btn_Bomb.isTouched(event)) {
					btn_Bomb.Highlight(getResources().getColor(R.color.black));
				}

				if (btn_Highscores.isTouched(event)) {
					btn_Highscores.Highlight(getResources().getColor(R.color.black));
				}
				if (btn_Exit.isTouched(event)) {
					btn_Exit.Highlight(getResources().getColor(R.color.black));
				}
				if (btn_rate.isTouched(event)) {
					btn_rate.Highlight(getResources().getColor(R.color.black));
				}
				if (btn_sound_mute.isTouched(event)) {
					btn_sound_mute.Highlight(getResources().getColor(R.color.black));
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				//refresh all
				btn_Classic.LowLight();
				btn_Highscores.LowLight();
				btn_Exit.LowLight();
				btn_rate.LowLight();
				btn_sound_mute.LowLight();
				btn_Arcade.LowLight();
				btn_Bomb.LowLight();
				btn_Zen.LowLight();

				//start game buttons are pressed
				if (btn_Classic.isTouched(event)) {
					if (sound_click != 0 && !sound_muted)
						sp.play(sound_click, 1, 1, 0, 0, 1);
					GamePlayMode = Classic;
					StartGame();
				}
				if (btn_Arcade.isTouched(event)) {
					if (sound_click != 0 && !sound_muted)
						sp.play(sound_click, 1, 1, 0, 0, 1);
					GamePlayMode = Arcade;
					StartGame();
				}
				if (btn_Bomb.isTouched(event)) {
					if (sound_click != 0 && !sound_muted)
						sp.play(sound_click, 1, 1, 0, 0, 1);
					GamePlayMode = Bomb;
					StartGame();
				}
				if (btn_Zen.isTouched(event)) {
					if (sound_click != 0 && !sound_muted)
						sp.play(sound_click, 1, 1, 0, 0, 1);
					GamePlayMode = Zen;
					StartGame();
				}

				//menu buttons pressed
				if (btn_sound_mute.isTouched(event)) {
					toggleSoundFx();
				}
				if (btn_Highscores.isTouched(event)) {
					if (sound_click != 0 && !sound_muted)
						sp.play(sound_click, 1, 1, 0, 0, 1);
					//open leaderboard
					OpenLeaderBoards();
				}
				if (btn_rate.isTouched(event)) {
					Rate();
				}
				if (btn_Exit.isTouched(event)) {
					Exit();
				}

			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {

			}
		} else if (state == GAMEOVER) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_Home.isTouched(event)) {
					btn_Home.Highlight(getResources().getColor(R.color.black));
				}
				if (btn_share.isTouched(event)) {
					btn_share.Highlight(getResources().getColor(R.color.black));
				}
				if (btn_Replay.isTouched(event)) {
					btn_Replay.Highlight(getResources().getColor(R.color.black));
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				//refresh all
				btn_Home.LowLight();
				btn_share.LowLight();
				btn_Replay.LowLight();
				if (btn_Home.isTouched(event)) {
					state = MENU;
					if (sound_click != 0 && !sound_muted)
						sp.play(sound_click, 1, 1, 0, 0, 1);
				}
				if (btn_share.isTouched(event)) {
					//share with facebook
					share();
					if (sound_click != 0 && !sound_muted)
						sp.play(sound_click, 1, 1, 0, 0, 1);
				}
				if (btn_Replay.isTouched(event)) {
					StartGame();
					if (sound_click != 0 && !sound_muted)
						sp.play(sound_click, 1, 1, 0, 0, 1);

				}
			}
		} else if (state == GAMEPLAY) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				if (notstarted)
					notstarted = false;

				boolean blackFound = false;
				//test touches on all blocks in last row
				for (int row = 0; row < BlockGenerator.BlocksOnScreen.size(); row++) {
					for (int i = 0; i < screen.getResources().getInteger(R.integer.blocks_per_row); i++) {

						//check if the current row includes a black block
						if (BlockGenerator.BlocksOnScreen.get(row)[i].type == Block.Black) {
							//black found. No need to test other rows
							blackFound = true;
						}

						//test all rows for screen touch
						if (BlockGenerator.BlocksOnScreen.get(row)[i].isTouched(event)) {
							//play a musical note if black is pressed
							if (BlockGenerator.BlocksOnScreen.get(row)[i].type == Block.Black) {
								//change to grey
								BlockGenerator.BlocksOnScreen.get(row)[i].lowLight();
								if (sound_effect_notes[music_notes[randomSong][currentNote]] != 0 && !sound_muted)
									sp.play(sound_effect_notes[music_notes[randomSong][currentNote]], 1, 1, 0, 0, 1);

								//update current note
								currentNote++;
								if (currentNote >= music_notes[randomSong].length)
									currentNote = 0;

								if (GamePlayMode == Classic) {
									//move one row down
									BlockGenerator.RowDown();
									if (row == BlockGenerator.BlocksOnScreen.size() - 1) {
										//game finished in classic mode. End of pattern
										game_over = true;
										success = true;
									}
								} else if (GamePlayMode == Arcade) {
									score_black_tapped++;

								} else if (GamePlayMode == Zen) {
									score_black_tapped++;
									BlockGenerator.RowDown();
								} else if (GamePlayMode == Bomb) {
									score_black_tapped++;
								}

							} else if (BlockGenerator.BlocksOnScreen.get(row)[i].type == Block.White) {
								//white clicked. Game over
								BlockGenerator.BlocksOnScreen.get(row)[i].ChangeRed();
								game_over = true;
								if (GamePlayMode == Classic || GamePlayMode == Zen)
									success = false;
								else
									success = true;
								if (sound_gameover != 0 && !sound_muted)
									sp.play(sound_gameover, 1, 1, 0, 0, 1);
							} else if (BlockGenerator.BlocksOnScreen.get(row)[i].type == Block.Red) {
								if (GamePlayMode == Bomb) {
									//red bomb clicked. game over
									game_over = true;
									success = true;
									if (sound_gameover != 0 && !sound_muted)
										sp.play(sound_gameover, 1, 1, 0, 0, 1);
								}
							}
						}

					}
					//black found in last row. No need to test other rows
					if (blackFound)
						break;

				}

			}
			if (event.getAction() == MotionEvent.ACTION_UP) {

			}

		}
	}

	//..................................................Game Functions..................................................................................................................................

	public void StartGame() {

		//refresh camera
		cameraY = 0;
		cameraX = 0;//29000;

		//not started
		notstarted = true;
		game_over = false;
		state = GAMEPLAY;

		//gameover counter
		gameover_counter = 0;

		//restart current note
		currentNote = 0;

		//update last clock reading
		lastTick = SystemClock.elapsedRealtime();

		randomSong = (int) (Math.random() * ((double) music_notes.length));
		int randompattern = (int) (Math.random() * ((double) classic_arcade_zen_pattern.length));

		//refresh mode specific details
		if (GamePlayMode == Classic) {
			BlockGenerator.loop(false);
			//refresh score
			score_time = 0;
			BlockGenerator.setPettern(classic_arcade_zen_pattern[randompattern]);
		} else if (GamePlayMode == Arcade) {
			BlockGenerator.loop(true);
			//refresh score
			score_black_tapped = 0;
			moving_speed = getResources().getInteger(R.integer.arcade_starting_speed);
			BlockGenerator.speedy = (moving_speed / 10);
			BlockGenerator.setPettern(classic_arcade_zen_pattern[randompattern]);

		} else if (GamePlayMode == Zen) {
			BlockGenerator.loop(true);
			//refresh score
			score_time = getResources().getInteger(R.integer.Zen_starting_time_in_milliseconds);
			score_black_tapped = 0;
			last_row_moved = SystemClock.elapsedRealtime();
			BlockGenerator.setPettern(classic_arcade_zen_pattern[randompattern]);

		} else if (GamePlayMode == Bomb) {
			BlockGenerator.loop(true);
			//refresh score
			score_black_tapped = 0;
			moving_speed = getResources().getInteger(R.integer.Bomb_starting_speed);
			BlockGenerator.speedy = (moving_speed / 10);
			BlockGenerator.setPettern(bomb_pattern);
		}

		BlockGenerator.restart();

	}

	public void share() {
		//share
		Share sharer = new Share();
		Bitmap screenshot = Bitmap.createBitmap(ScreenWidth(), ScreenHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas();
		canvas.setBitmap(screenshot);
		int temp_state = state;
		state = GAMEPLAY;
		Draw(canvas);
		state = temp_state;
		sharer.share_screenshot(this, screenshot);
	}

	public synchronized void GameOver() {
		if (success) {
			//if player signed into Google play update score depending on the mode
			if (GamePlayMode == Classic) {
				updateScore(score_time, false, getResources().getString(R.string.Classic_leaderboard_id), "" + GamePlayMode);
			} else if (GamePlayMode == Arcade) {
				updateScore(score_black_tapped, true, getResources().getString(R.string.Arcade_leaderboard_id), "" + GamePlayMode);
			} else if (GamePlayMode == Zen) {
				updateScore(score_black_tapped, true, getResources().getString(R.string.Zen_leaderboard_id), "" + GamePlayMode);
			} else if (GamePlayMode == Bomb) {
				updateScore(score_black_tapped, true, getResources().getString(R.string.Bomb_leaderboard_id), "" + GamePlayMode);
			}
		}
		//get topscore from local storage
		topscores[GamePlayMode] = new SingleScore(screen).load_localscore_simple("" + GamePlayMode);

		//display interstatial ad
		ad_counter++;
		if (ad_counter >= getResources().getInteger(R.integer.ad_shows_every_X_gameovers)) {
			openAd();
			ad_counter = 0;
		}
		state = GAMEOVER;
	}

	public void toggleSoundFx() {
		if (sound_muted) {
			sound_muted = false;
			btn_sound_mute.sprite = sound_on;
		} else {
			sound_muted = true;
			btn_sound_mute.sprite = sound_off;
		}
	}

	public void Rate() {
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, getResources().getString(R.string.unable_to_reach_market), Toast.LENGTH_LONG).show();
		}
	}

	public String scoreTime() {
		return ((double) score_time / 1000) + getResources().getString(R.string.score_suffix);
	}

	public String IntegerToScore(int score) {
		return ((double) score / 1000) + getResources().getString(R.string.score_suffix);
	}

	//...................................................Rendering of screen............................................................................................................................
	@Override
	public void Draw(Canvas canvas) {

		if (state == MENU) {
			//draw menu background
			canvas.drawColor(getResources().getColor(R.color.white));

			//title
			canvas.drawText(getResources().getString(R.string.app_name), (ScreenWidth() / 2) - (Title_Paint.measureText(getResources().getString(R.string.app_name)) / 2), (ScreenHeight() * 0.1f), Title_Paint);

			//play button
			btn_Classic.draw(canvas);
			btn_Bomb.draw(canvas);
			btn_Arcade.draw(canvas);
			btn_Zen.draw(canvas);

			//don't display google play top scores if no app id is inserted
			//if (getResources().getString(R.string.app_id).length() > 0)
			btn_Highscores.draw(canvas);

			btn_Exit.draw(canvas);
			btn_rate.draw(canvas);

			//draw sound buttons
			btn_sound_mute.draw(canvas);

		} else if (state == GAMEPLAY) {
			//draw gameplay background
			canvas.drawColor(getResources().getColor(R.color.green));

			//draw blocks
			BlockGenerator.draw(canvas);

			//draw lines
			for (int y = 0; y < ScreenHeight(); y += (ScreenHeight() / getResources().getInteger(R.integer.blocks_per_row))) {
				canvas.drawLine(0, y, ScreenWidth(), y, Line_paint);
			}
			for (int x = 0; x < ScreenWidth(); x += (ScreenWidth() / getResources().getInteger(R.integer.blocks_per_row))) {
				canvas.drawLine(x, 0, x, ScreenHeight(), Line_paint);
			}

			//draw score
			if (GamePlayMode == Classic) {
				canvas.drawText(scoreTime(), (ScreenWidth() * 0.5f) - (Score_Paint.measureText(scoreTime()) / 2), (float) (ScreenHeight() * 0.1f), Score_Paint);
			} else if (GamePlayMode == Arcade) {
				canvas.drawText("" + score_black_tapped, (ScreenWidth() * 0.5f) - (Score_Paint.measureText("" + score_black_tapped) / 2), (float) (ScreenHeight() * 0.1f), Score_Paint);
			} else if (GamePlayMode == Zen) {
				canvas.drawText(scoreTime(), (ScreenWidth() * 0.5f) - (Score_Paint.measureText(scoreTime()) / 2), (float) (ScreenHeight() * 0.1f), Score_Paint);
			} else if (GamePlayMode == Bomb) {
				canvas.drawText("" + score_black_tapped, (ScreenWidth() * 0.5f) - (Score_Paint.measureText("" + score_black_tapped) / 2), (float) (ScreenHeight() * 0.1f), Score_Paint);
			}

			//before game starts
			if (notstarted) {
				//draw instructions
				String instruction = "";
				if (GamePlayMode == Classic) {
					instruction = getResources().getString(R.string.classic_instruction);
				} else if (GamePlayMode == Arcade) {
					instruction = getResources().getString(R.string.arcade_instruction);
				} else if (GamePlayMode == Zen) {
					instruction = getResources().getString(R.string.zen_instruction);
				} else if (GamePlayMode == Bomb) {
					instruction = getResources().getString(R.string.bomb_instruction);
				}
				//draw tap to start
				canvas.drawText(getResources().getString(R.string.Tap_to_start2), (ScreenWidth() / 2) - (Instruction_Paint2.measureText(getResources().getString(R.string.Tap_to_start2)) / 2), ScreenHeight() - ((ScreenHeight() / getResources().getInteger(R.integer.blocks_per_row)) * 0.7f), Instruction_Paint2);

				StaticLayout instructionlayout = new StaticLayout(instruction, new TextPaint(Instruction_Paint), (int) ((ScreenWidth() / 1.60)), Layout.Alignment.ALIGN_CENTER, 1.2f, 1f, false);
				canvas.translate((ScreenWidth() / 2) - (ScreenWidth() / 3.3f), ScreenHeight() - ((ScreenHeight() / getResources().getInteger(R.integer.blocks_per_row)) * 0.7f) + Instruction_Paint2.getTextSize()); //position the text
				instructionlayout.draw(canvas);
				canvas.translate(-((ScreenWidth() / 2) - (ScreenWidth() / 3.3f)), -(ScreenHeight() - ((ScreenHeight() / getResources().getInteger(R.integer.blocks_per_row)) * 0.7f) + Instruction_Paint2.getTextSize())); //position the text
			}

		} else if (state == GAMEOVER) {
			//draw gameover background
			if (success)
				canvas.drawColor(getResources().getColor(R.color.green));
			else
				canvas.drawColor(getResources().getColor(R.color.red));

			//draw game mode title
			canvas.drawText(getResources().getString(GamePlayMode_name_ref[GamePlayMode]), (ScreenWidth() / 2) - (GameOver_Paint.measureText(getResources().getString(GamePlayMode_name_ref[GamePlayMode])) / 2), (ScreenHeight() * 0.3f), GameOver_Paint);

			//draw score
			if (GamePlayMode == Classic) {
				canvas.drawText(scoreTime(), (ScreenWidth() / 2) - (gameover_score_paint.measureText(scoreTime()) / 2), (ScreenHeight() * 0.55f), gameover_score_paint);
				canvas.drawText(scoreTime(), (ScreenWidth() / 2) - (gameover_score_stroke.measureText(scoreTime()) / 2), (ScreenHeight() * 0.55f), gameover_score_stroke);
				canvas.drawText(getResources().getString(R.string.your_top) + " " + IntegerToScore(topscores[GamePlayMode]), (ScreenWidth() / 2) - (SubTitle_Paint.measureText(getResources().getString(R.string.your_top) + " " + IntegerToScore(topscores[GamePlayMode])) * 0.5f), (ScreenHeight() * 0.38f), SubTitle_Paint);

			} else if (GamePlayMode == Arcade) {
				canvas.drawText("" + score_black_tapped, (ScreenWidth() / 2) - (gameover_score_paint.measureText("" + score_black_tapped) / 2), (ScreenHeight() * 0.55f), gameover_score_paint);
				canvas.drawText("" + score_black_tapped, (ScreenWidth() / 2) - (gameover_score_stroke.measureText("" + score_black_tapped) / 2), (ScreenHeight() * 0.55f), gameover_score_stroke);
				canvas.drawText(getResources().getString(R.string.your_top) + " " + (topscores[GamePlayMode]), (ScreenWidth() / 2) - (SubTitle_Paint.measureText(getResources().getString(R.string.your_top) + " " + (topscores[GamePlayMode])) * 0.5f), (ScreenHeight() * 0.38f), SubTitle_Paint);

			} else if (GamePlayMode == Zen) {
				canvas.drawText("" + score_black_tapped, (ScreenWidth() / 2) - (gameover_score_paint.measureText("" + score_black_tapped) / 2), (ScreenHeight() * 0.55f), gameover_score_paint);
				canvas.drawText("" + score_black_tapped, (ScreenWidth() / 2) - (gameover_score_stroke.measureText("" + score_black_tapped) / 2), (ScreenHeight() * 0.55f), gameover_score_stroke);
				canvas.drawText(getResources().getString(R.string.your_top) + " " + (topscores[GamePlayMode]), (ScreenWidth() / 2) - (SubTitle_Paint.measureText(getResources().getString(R.string.your_top) + " " + (topscores[GamePlayMode])) * 0.5f), (ScreenHeight() * 0.38f), SubTitle_Paint);

			} else if (GamePlayMode == Bomb) {
				canvas.drawText("" + score_black_tapped, (ScreenWidth() / 2) - (gameover_score_paint.measureText("" + score_black_tapped) / 2), (ScreenHeight() * 0.55f), gameover_score_paint);
				canvas.drawText("" + score_black_tapped, (ScreenWidth() / 2) - (gameover_score_stroke.measureText("" + score_black_tapped) / 2), (ScreenHeight() * 0.55f), gameover_score_stroke);
				canvas.drawText(getResources().getString(R.string.your_top) + " " + (topscores[GamePlayMode]), (ScreenWidth() / 2) - (SubTitle_Paint.measureText(getResources().getString(R.string.your_top) + " " + (topscores[GamePlayMode])) * 0.5f), (ScreenHeight() * 0.38f), SubTitle_Paint);

			}

			btn_share.draw(canvas);
			btn_Home.draw(canvas);
			btn_Replay.draw(canvas);

		}

		//physics.drawDebug(canvas);
		super.Draw(canvas);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
