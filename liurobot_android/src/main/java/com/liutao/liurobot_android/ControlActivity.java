package com.liutao.liurobot_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ControlActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control);
	}   
	public void onclick2(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.button1:
			Toast.makeText(ControlActivity.this, "动作", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(ControlActivity.this, ActionControlActivity.class));
			break;
		case R.id.button5:
			Toast.makeText(ControlActivity.this, "动作", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(ControlActivity.this, ActionControlActivity.class));
			break;
		case R.id.button2:
			Toast.makeText(ControlActivity.this, "语音", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(ControlActivity.this, VoiceControlActivity.class));
			break;
		case R.id.button4:
			Toast.makeText(ControlActivity.this, "语音", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(ControlActivity.this, VoiceControlActivity.class));
			break;
		case R.id.button3:
			Toast.makeText(ControlActivity.this, "媒体", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(ControlActivity.this,NewMediaActivity.class));
			break;
		case R.id.button6:
			Toast.makeText(ControlActivity.this, "媒体", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(ControlActivity.this,NewMediaActivity.class));
			break;
		case R.id.button10:
			startActivity(new Intent(ControlActivity.this, Main_ui.class));
			break;

		default:
			break;
		}

	}

}
