package com.liutao.liurobot_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class EditMapActivity extends Activity {
	Button back;
	Button test;
	Button again;
	ProgressBar progressBar;
	TextView plzwait;
	TextView ok;
	EditText edt_mapname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_maps);
		back = (Button) findViewById(R.id.button1);
		test = (Button) findViewById(R.id.button2);
		again=(Button) findViewById(R.id.button3);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		plzwait = (TextView) findViewById(R.id.textView2);
		ok = (TextView) findViewById(R.id.textView3);
		edt_mapname = (EditText) findViewById(R.id.editText1);
		
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					startActivity(new Intent(getApplicationContext(),
							MapsActivity.class));
			}
		});
		
		again.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if (again.getText().equals("重试")) {
					Toast.makeText(getApplicationContext(), "正在重新为您加载...", Toast.LENGTH_SHORT).show();
				}else{
					if (edt_mapname.getText().toString().length()==0) {
						Toast.makeText(getApplicationContext(), "请输入地图名称！", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_SHORT).show();
						startActivity(new Intent(getApplicationContext(),
								MapsActivity.class));
					}
				}
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
				edt_mapname.setVisibility(View.VISIBLE);
				again.setText("保存");
			}
		});
	}

}
