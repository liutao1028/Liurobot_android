package com.liutao.liurobot_android;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.liutao.liurobot_android.util.NowTime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class NewMapActivity extends BaseActivity implements OnClickListener {
	Button back;
	Button saveMap;
	EditText edt_mapname;
	EditText edt_mapmess;
	String map_name;
	String map_mess;
	String mapId;
	String map_url;
	public static final int SAVEFILE = 100;
	public static final int SAVEMAPOK = 101;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SAVEFILE:
				if (server != null) {
					if (server.isFileSaveOK == 1) {
						Toast.makeText(NewMapActivity.this, "地图保存成功", Toast.LENGTH_SHORT).show();
						startActivity(new Intent(getApplicationContext(), MapsActivity.class));
					} else {
						mHandler.sendEmptyMessageDelayed(SAVEFILE, 500);
					}
				}
				break;
			case SAVEMAPOK:
				if (server != null) {
					if (server.result_.equals("@SaveMap:OK!")) {
						server.setFileName(mapId);
//						server.send_isfile = true;
//						server.Send_Command = "@GetMap:" + mapId + "!";		
						server.setSendCommands("@GetMap:" + mapId + "!", "1");
						
						mHandler.sendEmptyMessageDelayed(SAVEFILE, 500);
					} else if (server.result_.equals("@SaveMap:NO!")) {
						Toast.makeText(NewMapActivity.this, "保存ok不成功！", Toast.LENGTH_SHORT).show();
					} else if (count > 20) {
						Toast.makeText(NewMapActivity.this, "超时处理！", Toast.LENGTH_SHORT).show();
						count = 0;
					} else {
						mHandler.sendEmptyMessageDelayed(SAVEMAPOK, 500);
					}
				}
				break;
			}
		}
	};
	private Timer timer;
	int count = 0;
	private TimerTask task = new TimerTask() {

		@Override
		public void run() {
			count++;

		}
	};
	private static String filename = "robotconfig.xml";
	private static File xmlPath = new File(
			Environment.getExternalStorageDirectory(), filename);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_maps);
		back = (Button) findViewById(R.id.btn_newmap_back);
		saveMap = (Button) findViewById(R.id.btn_savemap);
		edt_mapname = (EditText) findViewById(R.id.et_map_name);
		edt_mapmess = (EditText) findViewById(R.id.et_map_mess);
		timer = new Timer();
		back.setOnClickListener(this);
		saveMap.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_savemap:
			if (edt_mapname.getText().toString().length() == 0 || edt_mapmess.getText().toString().length() == 0) {
				Toast.makeText(getApplicationContext(), "请输入完整信息！", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "保存中！", Toast.LENGTH_SHORT).show();
				long id = System.currentTimeMillis();
				mapId = new NowTime().getCurrentTime();
				map_name = edt_mapname.getText().toString();
				map_mess = edt_mapmess.getText().toString();
				map_url =Environment.getExternalStorageDirectory().getPath()+"/"+mapId+".png";
				//server.Send_Command = "@SaveMap:" + mapId + "!";
				server.setSendCommands("@SaveMap:" + mapId + "!", "0");
				timer.schedule(task, 0, 1000);
				mHandler.sendEmptyMessageDelayed(SAVEMAPOK, 500);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setIgnoringElementContentWhitespace(false);
				try {
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document parse = db.parse(xmlPath);
					NodeList librarylist = parse.getElementsByTagName("maplibrary");
					Element maps = (Element) librarylist.item(0);
					Element map = parse.createElement("map");
					map.setAttribute("name", map_name);
					map.setAttribute("id", mapId);
					map.setAttribute("note", map_mess);// 地图描述
					map.setAttribute("url", map_url);// 地图图片的存储位置
					Element coords = parse.createElement("coords");
					maps.appendChild(map);
					TransformerFactory factory = TransformerFactory.newInstance();
					Transformer former = factory.newTransformer();
					former.transform(new DOMSource(parse), new StreamResult(new File(xmlPath.toString())));
					Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_SHORT).show();
					startActivity(new Intent(getApplicationContext(), ActionControlActivity.class));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.btn_newmap_back:
			startActivity(new Intent(getApplicationContext(), MapsActivity.class));
			break;
		}
		
	}

}
