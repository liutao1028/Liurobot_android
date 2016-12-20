package com.liutao.liurobot_android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.liutao.liurobot_android.entity.Bean;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NewMediaActivity extends Activity {
	ListView list2;
	MyAdapter myAdapter;
	EditText count;
	EditText node_name;
	private ArrayList<File> music_list;
	private ArrayList<File> photo_list;
	private ArrayList<File> video_list;
	private ArrayList<Bean> media_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media__control);
		list2 = (ListView) findViewById(R.id.listview2);
		music_list = new ArrayList<File>();
		photo_list = new ArrayList<File>();
		video_list = new ArrayList<File>();
		getAllFiles(new File("/sdcard"));
		myAdapter = new MyAdapter(photo_list);
		list2.setAdapter(myAdapter);
	}

	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.button5:
			myAdapter.setData(photo_list);
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.button6:
			myAdapter.setData(photo_list);
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.button4:
			myAdapter.setData(music_list);
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.button7:
			myAdapter.setData(music_list);
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.button3:
			myAdapter.setData(video_list);
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.button8:
			myAdapter.setData(video_list);
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.button2:
			startActivity(new Intent(getApplicationContext(),
					ControlActivity.class));
			break;

		default:
			break;
		}
	}

	class MyAdapter extends BaseAdapter {

		List<File> data;

		public List<File> getData() {
			return data;
		}

		public MyAdapter(List<File> list) {
			data = list;
		}

		public void setData(List<File> list) {
			data = list;
		}

		public void addData(File file) {
			data.add(file);
			notifyDataSetChanged();
		}

		public void removeData(File file) {
			data.remove(file);
			notifyDataSetChanged();
		}   
			
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			convertView = getLayoutInflater().inflate(R.layout.relative, null);
			TextView textView1 = (TextView) convertView
					.findViewById(R.id.ItemTitle);
			TextView textView2 = (TextView) convertView
					.findViewById(R.id.ItemText);

			textView1.setText(data
					.get(position)
					.toString()   
					.substring(   
							data.get(position).toString().lastIndexOf("/") + 1,
							data.get(position).toString().length()));
			textView2.setText(data.get(position).toString());
			
			return convertView;
		}
	}

	private void getAllFiles(File root) {//
		File files[] = root.listFiles();

		if (files != null)
			for (File f : files) {

				if ((f.isFile() && f.getName().endsWith(".mp3"))
						|| (f.isFile() && f.getName().endsWith(".ogg"))
						|| (f.isFile() && f.getName().endsWith(".wav"))
						|| (f.isFile() && f.getName().endsWith(".ape"))
						|| (f.isFile() && f.getName().endsWith(".cda"))
						|| (f.isFile() && f.getName().endsWith(".mac"))
						|| (f.isFile() && f.getName().endsWith(".aac"))) {
					music_list.add(f);
				}
				if ((f.isFile() && f.getName().endsWith(".rmvb"))
						|| (f.isFile() && f.getName().endsWith(".wmv"))
						|| (f.isFile() && f.getName().endsWith(".avi"))
						|| (f.isFile() && f.getName().endsWith(".3gp"))
						|| (f.isFile() && f.getName().endsWith(".mpg"))
						|| (f.isFile() && f.getName().endsWith(".mkv"))
						|| (f.isFile() && f.getName().endsWith(".mp4"))
						|| (f.isFile() && f.getName().endsWith(".dvd"))
						|| (f.isFile() && f.getName().endsWith(".mov"))
						|| (f.isFile() && f.getName().endsWith(".asf"))
						|| (f.isFile() && f.getName().endsWith(".mpeg4"))
						|| (f.isFile() && f.getName().endsWith(".mpeg2"))) {
					video_list.add(f);
				}
				if ((f.isFile() && f.getName().endsWith(".gif"))
						|| (f.isFile() && f.getName().endsWith(".jpeg"))
						|| (f.isFile() && f.getName().endsWith(".bmp"))
						|| (f.isFile() && f.getName().endsWith(".tif"))
						|| (f.isFile() && f.getName().endsWith(".jpg"))
						|| (f.isFile() && f.getName().endsWith(".pcd"))
						|| (f.isFile() && f.getName().endsWith(".png"))) {
					photo_list.add(f);
				} else if (f.isDirectory()) {
					getAllFiles(f);
				}
			}
	}


}
