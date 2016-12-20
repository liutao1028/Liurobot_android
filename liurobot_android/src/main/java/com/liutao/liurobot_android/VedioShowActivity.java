package com.liutao.liurobot_android;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VedioShowActivity extends BaseActivity implements OnClickListener, OnPreparedListener, OnCompletionListener {
	private VideoView video_video;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// 取消状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_vedioshow);
		video_video = (VideoView) findViewById(R.id.video_video);
		String url = getIntent().getStringExtra("videoUrl");
		video_video.setVideoPath(url);
		video_video.setMediaController(new MediaController(this));
		video_video.setOnClickListener(this);
		video_video.setOnPreparedListener(this);
		video_video.setOnCompletionListener(this);
	}
	@Override
	public void onClick(View v) {
		if(video_video.isPlaying()){
			video_video.pause();
		}else{
			video_video.start();
		}
		
	}
	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		if(mp!=null){
			mp.stop();
			mp.release();
			mp=null;
		}
		//finish();
	}

}
