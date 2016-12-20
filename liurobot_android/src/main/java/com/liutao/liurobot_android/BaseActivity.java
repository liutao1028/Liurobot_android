package com.liutao.liurobot_android;


import com.liutao.liurobot_android.entity.ServerSide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BaseActivity extends Activity {
	public static  ServerSide server =  new ServerSide();
	//public Intent intent;
	static{
		server.start();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
