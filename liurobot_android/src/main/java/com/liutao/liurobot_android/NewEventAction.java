package com.liutao.liurobot_android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.liutao.liurobot_android.entity.Bean;
import com.liutao.liurobot_android.entity.EventBean;
import com.liutao.liurobot_android.util.NowTime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewEventAction extends Activity{
	private static String filename = "robotconfig.xml";
	private static File xmlPath = new File(Environment.getExternalStorageDirectory(), filename);
	Spinner spinner;
	EditText edt_name;
	EditText edt_delay;
	TextView btn_selectlist;
	ListView listview_event;
	ListView listview_content;
	Button btn_MP3;
	Button btn_video;
	Button btn_pic;
	String mapId;
	private ArrayList<String> music_list;
	private ArrayList<String> photo_list;
	private ArrayList<String> video_list;
	private ArrayList<String> voice_list;
	private ArrayList<String> voice_id_list;
	private ArrayList<String> aciotn_list;
	private ArrayList<String> coord_list;
	private ArrayList<EventBean> event_list;
	MyAdapter myAdapter;
	EventAdapter eventAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_event_action);
		mapId = getIntent().getStringExtra("mapId");
		btn_MP3 = (Button) findViewById(R.id.btn_mp3);
		btn_video = (Button) findViewById(R.id.btn_video);
		btn_pic = (Button) findViewById(R.id.btn_pic);
		btn_selectlist = (TextView) findViewById(R.id.tv_selectlist);
		edt_delay = (EditText) findViewById(R.id.et_count);
		edt_name = (EditText) findViewById(R.id.et_name);
		edt_delay.setText("0");
		listview_event = (ListView) findViewById(R.id.listview_newevent_event);
		listview_content = (ListView) findViewById(R.id.listview_newevent_content);
		voice_list = new ArrayList<String>();
		voice_id_list = new ArrayList<String>();
		aciotn_list = new ArrayList<String>();
		coord_list = new ArrayList<String>();
		music_list = new ArrayList<String>();
		photo_list = new ArrayList<String>();
		video_list = new ArrayList<String>();
		event_list = new ArrayList<EventBean>();
		getDataList();
		getSDFiles(new File("/sdcard"));
		getVoiceList();
		spinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				voice_list);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
		myAdapter = new MyAdapter(aciotn_list);
		eventAdapter = new EventAdapter(event_list);
		listview_event.setAdapter(eventAdapter);
		listview_content.setAdapter(myAdapter);
		listview_content.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String contentname = null;
				switch (btn_selectlist.getText().toString()) {
				case "媒体":
					contentname = "media:" + myAdapter.getData().get(position);
					break;
				case "动作":
					contentname = "actions:" + myAdapter.getData().get(position).split(":")[1];
					break;
				case "坐标":
					contentname = "coord:" + myAdapter.getData().get(position).split(":")[1];
					break;

				default:
					break;
				}

				String delay = edt_delay.getText().toString();
				EventBean bean = new EventBean();
				bean.setEventContent(contentname);
				bean.setEventDelay(delay);
				event_list.add(bean);
				eventAdapter.notifyDataSetChanged();
				edt_delay.setText("0");
			}
		});
		listview_event.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				eventAdapter.removeData(eventAdapter.getData().get(position));
			}
		});
	}

	private void getVoiceList() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(false);
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document parse = db.parse(xmlPath);
			NodeList voicelibrarylist = parse.getElementsByTagName("voicelibrary");
			Element voicelibraryroot = (Element) voicelibrarylist.item(0);
			NodeList voice_nodelist = voicelibraryroot.getElementsByTagName("voice");
			for (int i = 0; i < voice_nodelist.getLength(); i++) {
				Element voicenode = (Element) voice_nodelist.item(i);
				String voiceId = voicenode.getAttribute("id");
				String voiceName = voicenode.getAttribute("name");
				voice_list.add(voiceId+":"+voiceName);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.btn_new_event_save:
			if(spinner.getSelectedItemPosition()==0){
				Toast.makeText(getApplicationContext(), "语音不能为空！", Toast.LENGTH_SHORT).show();
			}else{
			if (event_list.size() == 0) {
				Toast.makeText(getApplicationContext(), "保存列表不能为空！", Toast.LENGTH_SHORT).show();
			} else {
				if (edt_name.getText().toString().length() == 0) {
					Toast.makeText(getApplicationContext(), "保存名称不能为空！", Toast.LENGTH_SHORT).show();
				} else {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setIgnoringElementContentWhitespace(false);
					try {
						DocumentBuilder db = dbf.newDocumentBuilder();
						Document parse = db.parse(xmlPath);
						NodeList librarylist = parse.getElementsByTagName("maplibrary");
						Element library = (Element) librarylist.item(0);
						NodeList maplist = library.getElementsByTagName("map");
						for (int i = 0; i < maplist.getLength(); i++) {
							Element map = (Element) maplist.item(i);
							if (map.getAttribute("id").equals(mapId)) {
								Element eventnode = parse.createElement("event");
								eventnode.setAttribute("name", edt_name.getText().toString());
								eventnode.setAttribute("id", Long.toString(System.currentTimeMillis()));
								eventnode.setAttribute("voice", voice_list.get(spinner.getSelectedItemPosition()).split(":")[0]);
								for (int j = 0; j < event_list.size(); j++) {
									Element coordnode = parse.createElement("contentname");
									coordnode.setTextContent(event_list.get(j).getEventContent() + ":"
											+ event_list.get(j).getEventDelay());
									eventnode.appendChild(coordnode);
								}
								map.appendChild(eventnode);
							}
						}
//						NodeList library_list = parse.getElementsByTagName("voicelibrary");
//						Element library_root = (Element) library_list.item(0);
//						NodeList voice_List = library_root.getElementsByTagName("voice");
//						for (int y = 0; y < voice_List.getLength(); y++) {
//							Element item = (Element) voice_List.item(y);
//							//isemploy yes??????
//							if (item.getAttribute("name").equals(voice_list.get(spinner.getSelectedItemPosition()))) {
//								item.setAttribute("isemploy", "yes");
//							}
//						}
						TransformerFactory factory = TransformerFactory.newInstance();
						Transformer former = factory.newTransformer();
						former.transform(new DOMSource(parse), new StreamResult(new File(xmlPath.toString())));
						Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						e.printStackTrace();
					}
					// putExtra操作
					Intent intent = new Intent(getApplicationContext(), EventsControlActivity.class);
					startActivity(intent);
				}
			}
			}
			break;
		case R.id.btn_new_event_back:
			// putExtra操作
			Intent intent = new Intent(getApplicationContext(), EventsControlActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_new_event_jiandelay:
			if (edt_delay.getText().length() != 0) {
				String str = edt_delay.getText().toString();
				int sub = Integer.parseInt(str);
				if (sub > 0) {
					edt_delay.setText(sub - 1 + "");
				}
			}
			break;
		case R.id.btn_new_event_jiadelay:
			if (edt_delay.getText().length() != 0) {
				String str = edt_delay.getText().toString();
				int add = Integer.parseInt(str);
				edt_delay.setText(add + 1 + "");
			}
			break;
		case R.id.btn_new_event_action:
			myAdapter.setData(aciotn_list);
			myAdapter.notifyDataSetChanged();
			btn_MP3.setVisibility(View.INVISIBLE);
			btn_pic.setVisibility(View.INVISIBLE);
			btn_video.setVisibility(View.INVISIBLE);
			btn_selectlist.setText("动作");
			break;
		case R.id.btn_new_event_coord:
			myAdapter.setData(coord_list);
			myAdapter.notifyDataSetChanged();
			btn_MP3.setVisibility(View.INVISIBLE);
			btn_pic.setVisibility(View.INVISIBLE);
			btn_video.setVisibility(View.INVISIBLE);
			btn_selectlist.setText("坐标");
			break;
		case R.id.btn_new_event_media:
			myAdapter.setData(video_list);
			myAdapter.notifyDataSetChanged();
			btn_MP3.setVisibility(View.VISIBLE);
			btn_pic.setVisibility(View.VISIBLE);
			btn_video.setVisibility(View.VISIBLE);
			btn_selectlist.setText("媒体");
			break;
		case R.id.btn_mp3:
			myAdapter.setData(music_list);
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.btn_video:
			myAdapter.setData(video_list);
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.btn_pic:
			myAdapter.setData(photo_list);
			myAdapter.notifyDataSetChanged();
			break;
		}
	}

	private void getDataList() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);

		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(xmlPath);
			NodeList voiceList = doc.getElementsByTagName("voice");
			for (int i = 0; i < voiceList.getLength(); i++) {
				Element root = (Element) voiceList.item(i);
				//+":"+root.getAttribute("id")
					//voice_list.add(root.getAttribute("name"));
				
			}
			NodeList maplibrarys = doc.getElementsByTagName("maplibrary");
			Element maplibrary = (Element) maplibrarys.item(0);
			NodeList maps = maplibrary.getElementsByTagName("map");
			for (int x = 0; x < maps.getLength(); x++) {
				Element map = (Element) maps.item(x);
				if (map.getAttribute("id").equals(mapId)) {
					Log.i("ddddd", "新增事件匹配到了mapId"+mapId);
					NodeList coordslist = map.getElementsByTagName("coords");
					Element coords = (Element)coordslist.item(0);
					NodeList coordlist =coords.getElementsByTagName("coord");
					for (int j = 0; j < coordlist.getLength(); j++) {
						Element coord = (Element) coordlist.item(j);
						coord_list.add(coord.getAttribute("name")+":"+coord.getAttribute("id"));
						System.out.println(coord.getAttribute("name"));
					}
				}
			}
			NodeList actionsList = doc.getElementsByTagName("actions");
			for (int x = 0; x < actionsList.getLength(); x++) {
				Element actions = (Element) actionsList.item(x);
				aciotn_list.add(actions.getAttribute("name")+":"+actions.getAttribute("id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getCurrentTime(){
		long id = System.currentTimeMillis();
		return Long.toString(id);
	}

	private void getSDFiles(File root) {//
		File files[] = root.listFiles();

		if (files != null)
			for (File f : files) {

				if ((f.isFile() && f.getName().endsWith(".mp3")) || (f.isFile() && f.getName().endsWith(".ogg"))
						|| (f.isFile() && f.getName().endsWith(".wav")) || (f.isFile() && f.getName().endsWith(".ape"))
						|| (f.isFile() && f.getName().endsWith(".cda")) || (f.isFile() && f.getName().endsWith(".mac"))
						|| (f.isFile() && f.getName().endsWith(".aac"))) {
					music_list.add(f.toString());
				}
				if ((f.isFile() && f.getName().endsWith(".rmvb")) || (f.isFile() && f.getName().endsWith(".wmv"))
						|| (f.isFile() && f.getName().endsWith(".avi")) || (f.isFile() && f.getName().endsWith(".3gp"))
						|| (f.isFile() && f.getName().endsWith(".mpg")) || (f.isFile() && f.getName().endsWith(".mkv"))
						|| (f.isFile() && f.getName().endsWith(".mp4")) || (f.isFile() && f.getName().endsWith(".dvd"))
						|| (f.isFile() && f.getName().endsWith(".mov")) || (f.isFile() && f.getName().endsWith(".asf"))
						|| (f.isFile() && f.getName().endsWith(".mpeg4"))
						|| (f.isFile() && f.getName().endsWith(".mpeg2"))) {
					video_list.add(f.toString());
				}
				if ((f.isFile() && f.getName().endsWith(".gif")) || (f.isFile() && f.getName().endsWith(".jpeg"))
						|| (f.isFile() && f.getName().endsWith(".bmp")) || (f.isFile() && f.getName().endsWith(".tif"))
						|| (f.isFile() && f.getName().endsWith(".jpg")) || (f.isFile() && f.getName().endsWith(".pcd"))
						|| (f.isFile() && f.getName().endsWith(".png"))) {
					photo_list.add(f.toString());
				} else if (f.isDirectory()) {
					getSDFiles(f);
				}
			}
	}

	// 适配器
	class MyAdapter extends BaseAdapter {
		ArrayList<String> data = new ArrayList<String>();

		public ArrayList<String> getData() {
			return data;
		}

		public MyAdapter(ArrayList<String> list) {
			data = list;
		}

		public void setData(ArrayList<String> list) {
			data = list;
		}

		public void addData(String string) {
			data.add(string);
			notifyDataSetChanged();
		}

		public void removeData(String string) {
			data.remove(string);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.checkbox_item, null);
				holder.txt_Name = (TextView) convertView.findViewById(R.id.textView1);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.txt_Name.setText(data.get(position).split(":")[0]);
			return convertView;//
		}

		class ViewHolder {
			TextView txt_Name;

		}

	}

	class EventAdapter extends BaseAdapter {
		List<EventBean> data;

		public List<EventBean> getData() {
			return data;
		}

		public EventAdapter(List<EventBean> list) {
			data = list;
		}

		public void setData(List<EventBean> list) {
			data = list;
		}

		public void addData(EventBean bean) {
			data.add(bean);
			notifyDataSetChanged();
		}

		public void removeData(EventBean bean) {
			data.remove(bean);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.relative, null);
				holder.txt_fileName = (TextView) convertView.findViewById(R.id.ItemTitle);
				holder.txt_count = (TextView) convertView.findViewById(R.id.ItemText);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.txt_count.setText("延时" + data.get(position).getEventDelay() + "秒");
			holder.txt_fileName.setText(data.get(position).getEventContent());
			return convertView;
		}

		class ViewHolder {
			TextView txt_count;
			TextView txt_fileName;
		}

	}

}
