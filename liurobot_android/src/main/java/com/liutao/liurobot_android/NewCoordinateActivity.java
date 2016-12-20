package com.liutao.liurobot_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class NewCoordinateActivity extends Activity {
	Button back;
	Button test;
	Button again;
	ProgressBar progressBar;
	TextView plzwait;
	TextView ok;
	EditText edt_coordinatename;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_coordinate);
		back = (Button) findViewById(R.id.button1);
		test = (Button) findViewById(R.id.button2);
		again=(Button) findViewById(R.id.button3);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		plzwait = (TextView) findViewById(R.id.textView2);
		ok = (TextView) findViewById(R.id.textView3);
		edt_coordinatename = (EditText) findViewById(R.id.editText1);
		
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					startActivity(new Intent(getApplicationContext(),
							CoordinateManageActivity.class));
			}
		});
		
		again.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if (again.getText().equals("重试")) {
					startActivity(new Intent(getApplicationContext(),
							NewCoordActivity.class));
				}else{
					if (edt_coordinatename.getText().toString().length()==0) {
						Toast.makeText(getApplicationContext(), "请输入坐标名称！", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_SHORT).show();
						startActivity(new Intent(getApplicationContext(),
								CoordinateManageActivity.class));
						
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
				edt_coordinatename.setVisibility(View.VISIBLE);
				again.setText("保存");
				//
			}     
		});
	}

}