package com.liutao.liurobot_android;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Main_ui extends BaseActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_ui);
	}

	public void onclick(View view) {
		switch (view.getId()) {
		
		case R.id.button1:
			Toast.makeText(Main_ui.this, "管理", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(Main_ui.this, ControlActivity.class));
			break;
		case R.id.button6:
			Toast.makeText(Main_ui.this, "管理", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(Main_ui.this, ControlActivity.class));
			break;
		case R.id.button2:
			Toast.makeText(Main_ui.this, "地图", Toast.LENGTH_SHORT).show();
			Intent intent_map = new Intent(Main_ui.this, MapsActivity.class);
			startActivity(intent_map);
			break;
		case R.id.button7:
			Toast.makeText(Main_ui.this, "地图", Toast.LENGTH_SHORT).show();
			Intent intent_map1 = new Intent(Main_ui.this, MapsActivity.class);
			startActivity(intent_map1);
			break;
		case R.id.button4:
			Toast.makeText(Main_ui.this, "事件", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(Main_ui.this, EventsControlActivity.class));
			break;
		case R.id.button9:
			Toast.makeText(Main_ui.this, "事件", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(Main_ui.this, EventsControlActivity.class));
			break;
		case R.id.button5:
			Toast.makeText(Main_ui.this, "设置", Toast.LENGTH_SHORT).show();
			break;//
		case R.id.button10:
			Toast.makeText(Main_ui.this, "设置", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_back_main_ui:
			startActivity(new Intent(Main_ui.this, TtsDemo.class));
			break;
		default:
			break;
		}
	}
}
