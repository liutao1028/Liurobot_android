package com.liutao.liurobot_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WaitMapsActivity extends Activity {
	Button back;
	Button test;
	Button again;
	ProgressBar progressBar;
	TextView plzwait;
	TextView ok;
	TextView mapname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wait_maps);
		back = (Button) findViewById(R.id.button1);
		test = (Button) findViewById(R.id.button2);
		again=(Button) findViewById(R.id.button3);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		plzwait = (TextView) findViewById(R.id.textView2);
		ok = (TextView) findViewById(R.id.textView3);
		mapname = (TextView) findViewById(R.id.textView4);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					startActivity(new Intent(getApplicationContext(),
							XianyanMapsActivity.class));
			}
		});
		
		again.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "正在重新为您加载...", Toast.LENGTH_SHORT).show();
			}
		});
		test.setOnClickListener(new OnClickListener() {
			//
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				plzwait.setVisibility(View.INVISIBLE);
				progressBar.setVisibility(View.INVISIBLE);
				ok.setVisibility(View.VISIBLE);
				mapname.setVisibility(View.VISIBLE);
				//
			}
		});
	}

}
