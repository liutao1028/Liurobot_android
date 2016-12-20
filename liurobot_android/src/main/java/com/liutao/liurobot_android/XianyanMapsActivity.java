package com.liutao.liurobot_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class XianyanMapsActivity extends Activity {
	Button use;
	Button back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xianyan_maps);
		use = (Button) findViewById(R.id.button1);
		back = (Button) findViewById(R.id.button2);
		use.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(XianyanMapsActivity.this, "应用", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(getApplicationContext(), WaitMapsActivity.class));
				
			}
		});
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(XianyanMapsActivity.this, Main_ui.class));
				
			}
		});
		
	}

}
