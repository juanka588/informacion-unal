package com.unal.iun;

import java.io.IOException;
import java.net.URLEncoder;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class RadioActivity extends Activity implements OnClickListener {

	private SeekBar playSeekBar;

	private Button buttonPlay;

	private Button buttonStopPlay;

	private MediaPlayer player;

	private Handler myHandler = new Handler();

	int songPosition = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_radio);
		initializeMediaPlayer();
		initializeUIElements();
	}

	private void initializeUIElements() {

		playSeekBar = (SeekBar) findViewById(R.id.seekBar1);
		playSeekBar.setMax(player.getDuration());
		playSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				player.seekTo(arg1);

			}
		});
		playSeekBar.setVisibility(View.INVISIBLE);

		buttonPlay = (Button) findViewById(R.id.buttonPlay);
		buttonPlay.setOnClickListener(this);

		buttonStopPlay = (Button) findViewById(R.id.buttonStop);
		buttonStopPlay.setEnabled(false);
		buttonStopPlay.setOnClickListener(this);

	}

	private Runnable UpdateSongTime = new Runnable() {
		public void run() {
			if (player.isPlaying()) {
				playSeekBar.setProgress(player.getCurrentPosition());
				myHandler.postDelayed(this, 100);
			}
		}
	};

	public void onClick(View v) {
		if (v == buttonPlay) {
			try {
				startPlaying();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (v == buttonStopPlay) {
			stopPlaying();
		}
	}

	private void startPlaying() throws IllegalStateException, IOException {
		buttonStopPlay.setEnabled(true);
		buttonPlay.setEnabled(false);

		playSeekBar.setVisibility(View.VISIBLE);
		// player.prepareAsync();
		player.start();
		player.seekTo(songPosition);
		// player.prepare();
		player.setOnPreparedListener(new OnPreparedListener() {

			public void onPrepared(MediaPlayer mp) {
				player.start();
			}
		});
		myHandler.postDelayed(UpdateSongTime, 100);
	}

	private void stopPlaying() {
		if (player.isPlaying()) {
			songPosition=player.getCurrentPosition();
			player.stop();
			player.release();
			//initializeMediaPlayer();			
		}
		buttonPlay.setEnabled(true);
		buttonStopPlay.setEnabled(false);
		playSeekBar.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onDestroy() {
		if (player != null) {
			player.release();
		}
		super.onDestroy();
	}

	private void initializeMediaPlayer() {
		player = new MediaPlayer();
		try {
			/*
			 * player.setAudioStreamType(AudioManager.STREAM_MUSIC); String url
			 * = "http://www.giss.tv:8000/1063laplata.mp3";
			 * player.setDataSource(URLEncoder.encode(url, "UTF-8"));
			 */
			player = MediaPlayer.create(getApplicationContext(),
					R.raw.classical);

			player.setVolume(100, 100);
		} catch (IllegalArgumentException e) {
			Log.e("ERROR Repro", e.toString());
		} catch (IllegalStateException e) {
			// e.printStackTrace();
			Log.e("ERROR Repro", e.toString());
		} catch (Exception e) {
			// e.printStackTrace();
			Log.e("ERROR Repro", e.toString());
		}

		player.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				playSeekBar.setSecondaryProgress(percent);
				Log.i("Buffering", "" + percent);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (player.isPlaying()) {
			player.stop();
		}
	}
}
