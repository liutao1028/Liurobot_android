package com.liutao.liurobot_android;


import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class NewMapWaitActivity extends BaseActivity implements OnClickListener {
	TextView hint;
	ProgressBar progressBar;
	Button back;
	Button test;
	Button again;
	Button btn_save_mapname;
	public static final int CHECKRESULT = 1;
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECKRESULT:
				if(server!=null){
					if(server.result_.equals("@Gmapping:OK!")){
						hint.setText("请求成功！");
						progressBar.setVisibility(View.INVISIBLE);
						Toast.makeText(getApplicationContext(), "请求成功,正在创建地图...", Toast.LENGTH_SHORT).show();
						btn_save_mapname.setVisibility(View.VISIBLE);
					}else if(server.result_.equals("@Gmapping:NO!")){
						Toast.makeText(getApplicationContext(), "创建地图失败,请重试...", Toast.LENGTH_SHORT).show();
					}else if(count>60){
						Toast.makeText(getApplicationContext(), "响应超时...", Toast.LENGTH_SHORT).show();
						count=0;
					}
					else {
						handler.sendEmptyMessageDelayed(CHECKRESULT, 500);
					}
				}
				break;
			}
		}
	};
	int count =0;
	private TimerTask task = new TimerTask() {
		
		@Override
		public void run() {
			count++;
			
		}
	};
	private Timer timer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_map_wait);
		hint =(TextView) findViewById(R.id.textView2);
		progressBar=(ProgressBar) findViewById(R.id.progressBar1);
		back=(Button) findViewById(R.id.btn_wait_back);
		btn_save_mapname=(Button) findViewById(R.id.btn_save_mapname);
		test=(Button) findViewById(R.id.btn_wait_test);
		again=(Button) findViewById(R.id.btn_aggin);
		back.setOnClickListener(this);
		test.setOnClickListener(this);
		again.setOnClickListener(this);
		btn_save_mapname.setOnClickListener(this);
		btn_save_mapname.setVisibility(View.GONE);
		timer = new Timer();
		 if(server!=null){
			 //server.setSendCommands("@Gmapping!");
			 server.setSendCommands("@Gmapping!", "0");
			 //Log.i("bbbbbbbb", server.Send_Command);
			 timer.schedule(task, 0,1000);
			 handler.sendEmptyMessageDelayed(CHECKRESULT, 500);
		 }
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_save_mapname:
			startActivity(new Intent(getApplicationContext(), NewMapActivity.class));
			break;
		case R.id.btn_wait_back:
			startActivity(new Intent(getApplicationContext(),
					MapsActivity.class));
			break;
		case R.id.btn_aggin:
			Toast.makeText(getApplicationContext(), "正在重新为您加载...", Toast.LENGTH_SHORT).show();
			if(server!=null){
				//Log.i("aaaa", server.Send_Command);
				 //server.Send_Command = "@Gmapping!";
				 handler.sendEmptyMessageDelayed(CHECKRESULT, 500);
			 }
			break;
		case R.id.btn_wait_test:
			hint.setText("请求成功！");
			progressBar.setVisibility(View.INVISIBLE);
			Toast.makeText(getApplicationContext(), "请求成功,正在准备创建地图...", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(getApplicationContext(), NewMapActivity.class));
			break;
		}
	}

}
