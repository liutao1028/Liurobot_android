package com.liutao.liurobot_android;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ModelActivity extends Activity {
	String buffer = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.model_ui);
		
				
	}
	public void onclick2(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.button1:
			Toast.makeText(ModelActivity.this, "先验", Toast.LENGTH_SHORT).show();
			//Socket 通信
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					try {
//						Socket socket = new Socket("10.42.0.1",8250); //IP：10.14.114.127，端口54321  
//			            //向服务器发送消息  
//						        
//			            PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);        
//			            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));  
//			            
////			            out.print("@Edit_O_XYZ:old,1.000000,1.000000,1.000000,1.000000!");
////			            out.print("@GetNowMapName!");
//			            out.print("@Gmapping!");
//			            out.flush();
//			            
//			            String line = null;
//			            buffer = "";
//			            while ((line = br.readLine()) != null) {  
//			            	buffer = line + buffer;  
//			            }
//			            System.out.println("ZZZZZZZZZZZ"+buffer);
//			            br.close();
//			            out.close();
//			            socket.close();
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}   
//				}
//			}).start();
			
			startActivity(new Intent(ModelActivity.this, XianyanMapsActivity.class));
			break;
		case R.id.button3:
			Toast.makeText(ModelActivity.this, "先验", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(ModelActivity.this, XianyanMapsActivity.class));
			//
			break;
		case R.id.button2:
			Toast.makeText(ModelActivity.this, "后验", Toast.LENGTH_SHORT).show();
			break;
		case R.id.button4:
			Toast.makeText(ModelActivity.this, "后验", Toast.LENGTH_SHORT).show();
			break;
		case R.id.button15:
			startActivity(new Intent(ModelActivity.this, Main_ui.class));
				
			break;
				
		default:
			break;
		}
	}

}
