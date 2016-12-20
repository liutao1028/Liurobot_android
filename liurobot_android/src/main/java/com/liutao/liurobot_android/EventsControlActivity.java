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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EventsControlActivity extends Activity {
	ListView list_map;
	ListView list_event;
	TextView mapName;
	TextView eventName;
	MyAdapter myMapAdapter;
	MyAdapter myEventAdapter;
	private ArrayList<String> map_list;
	private ArrayList<String> mapid_list;
	private ArrayList<String> event_list;
	private ArrayList<String> eventid_list;
	private static String filename = "robotconfig.xml";
	private static File xmlPath = new File(Environment.getExternalStorageDirectory(), filename);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events_ui);
		list_map = (ListView) findViewById(R.id.listview_event_map);
		list_event = (ListView) findViewById(R.id.listview_event_event);
		mapName = (TextView) findViewById(R.id.tv_event_mapname);
		eventName = (TextView) findViewById(R.id.tv_event_eventname);
		map_list = new ArrayList<String>();
		mapid_list = new ArrayList<String>();
		event_list = new ArrayList<String>();
		eventid_list = new ArrayList<String>();
		getMapListData();
		myMapAdapter = new MyAdapter(map_list);
		myEventAdapter = new MyAdapter(event_list);
		list_map.setAdapter(myMapAdapter);
		list_event.setAdapter(myEventAdapter);
		list_map.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				getEventListData(mapid_list.get(position).toString());
				myEventAdapter.setData(event_list);
				myEventAdapter.notifyDataSetChanged();
				mapName.setText(map_list.get(position));
			}
		});
		list_event.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				eventName.setText(event_list.get(position));
			}
		});
	}

	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.button2:// 返回
			Toast.makeText(getApplicationContext(), "返回", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(getApplicationContext(), Main_ui.class));
			break;
		case R.id.button4:// 删除
			if (eventName.getText().equals("请选择")) {
				Toast.makeText(getApplicationContext(), "请选择要删除的事件", Toast.LENGTH_SHORT).show();
			} else {
				String eventName_txt = eventName.getText().toString();
				String mapName_txt = mapName.getText().toString();
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setIgnoringElementContentWhitespace(true);
				try {
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document xmldoc = db.parse(xmlPath);
					NodeList librarylist = xmldoc.getElementsByTagName("maplibrary");
					Element item = (Element) librarylist.item(0);

					NodeList maplist = item.getElementsByTagName("map");
					for (int i = 0; i < maplist.getLength(); i++) {
						Element node = (Element) maplist.item(i);
						String attribute = node.getAttribute("id");
						if (attribute.equals(mapName_txt)) {
							NodeList events = node.getElementsByTagName("event");
							for (int j = 0; j < events.getLength(); j++) {
								Element event = (Element) events.item(j);
								String attribute2 = event.getAttribute("id");
								if (attribute2.equals(eventName_txt)) {
									node.removeChild(event);
									myEventAdapter.removeData(eventName_txt);
									eventName.setText("请选择");
									String voicename = event.getAttribute("voice");
									NodeList voicelibrary = xmldoc.getElementsByTagName("voicelibrary");
									Element voice_library = (Element) voicelibrary.item(0);
									NodeList voice_nodelist = voice_library.getElementsByTagName("voice");
//									for (int k = 0; k < voice_nodelist.getLength(); k++) {
//										Element voicenode = (Element) voice_nodelist.item(k);
//										if (voicenode.getAttribute("id").equals(voicename)) {
//											voicenode.setAttribute("isemploy", "no");
//										}
//									}
								}
							}
						}
					}
					TransformerFactory factory = TransformerFactory.newInstance();
					Transformer former = factory.newTransformer();
					former.transform(new DOMSource(xmldoc), new StreamResult(new File(xmlPath.toString())));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case R.id.button6:// 新增
			if (mapName.getText().equals("请选择")) {
				Toast.makeText(getApplicationContext(), "请选择新建事件的地图", Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent(getApplicationContext(), NewEventAction.class);
				String mapID = mapid_list.get(map_list.indexOf(mapName.getText().toString()));
				Log.i("ddddd", mapID);
				intent.putExtra("mapId", mapID);
				startActivity(intent);
			}
			break;

		default:
			break;
		}
	}

	private void getMapListData() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);

		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(xmlPath);
			NodeList nodeList = doc.getElementsByTagName("maplibrary");
			Element item = (Element) nodeList.item(0);
			NodeList mapnodelist = item.getElementsByTagName("map");

			for (int i = 0; i < mapnodelist.getLength(); i++) {
				Element root = (Element) mapnodelist.item(i);
				map_list.add(root.getAttribute("name").toString());
				mapid_list.add(root.getAttribute("id").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getEventListData(String mapname) {
		event_list.clear();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);

		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(xmlPath);
			NodeList nodeList = doc.getElementsByTagName("maplibrary");
			Element item = (Element) nodeList.item(0);
			NodeList mapnodelist = item.getElementsByTagName("map");

			for (int i = 0; i < mapnodelist.getLength(); i++) {
				Element root = (Element) mapnodelist.item(i);
				if (mapname.equals(root.getAttribute("id").toString())) {
					NodeList eventnodelist = root.getElementsByTagName("event");
					for (int j = 0; j < eventnodelist.getLength(); j++) {
						Element eventnode = (Element) eventnodelist.item(j);
						event_list.add(eventnode.getAttribute("name"));
						eventid_list.add(eventnode.getAttribute("id"));
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class MyAdapter extends BaseAdapter {
		List<String> data;

		public List<String> getData() {
			return data;
		}

		public MyAdapter(List<String> list) {
			data = list;
		}

		public void setData(List<String> list) {
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
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.checkbox_item, null);
				holder.txt_Name = (TextView) convertView.findViewById(R.id.textView1);
				convertView.setTag(holder);

			} else
				holder = (ViewHolder) convertView.getTag();
			holder.txt_Name.setText(data.get(position).toString());
			return convertView;
		}

		class ViewHolder {
			TextView txt_Name;

		}

	}
}
