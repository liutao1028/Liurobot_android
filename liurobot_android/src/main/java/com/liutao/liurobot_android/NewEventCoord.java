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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewEventCoord extends Activity {
	private static String filename = "robotconfig.xml";
	private static File xmlPath = new File(
			Environment.getExternalStorageDirectory(), filename);
	EditText edt_count;
	EditText edt_name;
	Spinner spinner;
	ListView listview_event;
	ListView listview_content;
	String mapId;
	private ArrayList<String> voice_list;
	private ArrayList<String> content_list;
	private ArrayList<EventBean> event_list;
	MyAdapter myAdapter;
	EventAdapter eventAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_event_coord);
		mapId = getIntent().getStringExtra("mapId");
		edt_count=(EditText) findViewById(R.id.editText1);
		edt_name=(EditText) findViewById(R.id.editText2);
		edt_count.setText("0");
		listview_event = (ListView) findViewById(R.id.lv_new_event_coor_event);
		listview_content = (ListView) findViewById(R.id.lv_new_event_coor_content);
		spinner = (Spinner) findViewById(R.id.spinner1);
		voice_list = new ArrayList<String>();
		content_list = new ArrayList<String>();
		event_list = new ArrayList<EventBean>();
		getDataList();
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, voice_list);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
		myAdapter = new MyAdapter(content_list);
		eventAdapter = new EventAdapter(event_list);
		listview_event.setAdapter(eventAdapter);
		listview_content.setAdapter(myAdapter);
		listview_content.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String contentname = myAdapter.getData().get(position).toString();
				String delay = edt_count.getText().toString();
				EventBean bean = new EventBean();
				bean.setEventContent(contentname);
				bean.setEventDelay(delay);
				event_list.add(bean);
				eventAdapter.notifyDataSetChanged();
				edt_count.setText("0");
			}
		});
		listview_event.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				eventAdapter.removeData(eventAdapter.getData().get(position));
			}
		});
		
		
	}
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.button1:
			if (event_list.size() == 0) {
				Toast.makeText(getApplicationContext(), "保存列表不能为空！",
						Toast.LENGTH_SHORT).show();
			} else {
				if (edt_name.getText().toString().length() == 0) {
					Toast.makeText(getApplicationContext(), "保存名称不能为空！",
							Toast.LENGTH_SHORT).show();
				} else {

					DocumentBuilderFactory dbf = DocumentBuilderFactory
							.newInstance();
					dbf.setIgnoringElementContentWhitespace(false);
					try {
						DocumentBuilder db = dbf.newDocumentBuilder();
						Document parse = db.parse(xmlPath);
						NodeList librarylist = parse
								.getElementsByTagName("maplibrary");
						Element library = (Element) librarylist.item(0);
						NodeList maplist = library.getElementsByTagName("map");
						for (int i = 0; i < maplist.getLength(); i++) {
							Element map = (Element) maplist.item(i);
							if (map.getAttribute("id").equals(mapId)) {
								Element eventnode = parse.createElement("event");
								eventnode.setAttribute("name", edt_name.getText()
										.toString());
								eventnode.setAttribute("type", "coord");
								Element voicenode = parse.createElement("voicename");
								voicenode.setTextContent(voice_list.get(spinner.getSelectedItemPosition()));
								eventnode.appendChild(voicenode);
								for (int j = 0; j < event_list.size(); j++) {
									Element coordnode = parse.createElement("coordname");
									coordnode.setTextContent(event_list.get(j).getEventContent()+":"+event_list.get(j).getEventDelay());
									eventnode.appendChild(coordnode);
								}
								map.appendChild(eventnode);
							}
						}
						NodeList library_list = parse
								.getElementsByTagName("voicelibrary");
						Element library_root = (Element) library_list.item(0);
						NodeList voice_List = library_root.getElementsByTagName("voice");
						for (int y = 0; y < voice_List.getLength(); y++) {
							Element item = (Element) voice_List.item(y);
							if (item.getAttribute("name").equals(voice_list.get(spinner.getSelectedItemPosition()))) {
								item.setAttribute("isemploy", "yes");
							}
						}
						
						TransformerFactory factory = TransformerFactory
								.newInstance();
						Transformer former = factory.newTransformer();
						former.transform(new DOMSource(parse),
								new StreamResult(new File(xmlPath.toString())));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// ****
					Toast.makeText(getApplicationContext(), "保存成功！",
							Toast.LENGTH_SHORT).show();
					startActivity(new Intent(getApplicationContext(),
							EventsControlActivity.class));
					
				}
			}
			
			
			
			break;
		case R.id.button2:
			startActivity(new Intent(getApplicationContext(), EventsControlActivity.class));
			break;
		case R.id.button3:
			if (edt_count.getText().length() != 0) {
				String str = edt_count.getText().toString();
				int sub = Integer.parseInt(str);
				if (sub > 0) {
					edt_count.setText(sub - 1 + "");
				}

			}
			break;
		case R.id.button4:
			if (edt_count.getText().length() != 0) {
				String str = edt_count.getText().toString();
				int add = Integer.parseInt(str);

				edt_count.setText(add + 1 + "");
			}
			break;

		default:
			break;
		}
	
	}
	private void getDataList(){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);
		
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(xmlPath);
			NodeList nodeList = doc.getElementsByTagName("voice");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element root = (Element) nodeList.item(i);
				if (root.getAttribute("isemploy").equals("no")) {
					voice_list.add(root.getAttribute("name"));
					System.out.println(root.getAttribute("name"));
				}
			}
			
			NodeList maps = doc.getElementsByTagName("map");
			for (int x = 0; x < maps.getLength(); x++) {
				Element map = (Element) maps.item(x);
				if (map.getAttribute("id").equals(mapId)) {
					NodeList coordlist = map.getElementsByTagName("coord");
					for (int j = 0; j < coordlist.getLength(); j++) {
						Element coord = (Element) coordlist.item(j);
						content_list.add(coord.getAttribute("name"));
						System.out.println(coord.getAttribute("name"));
					}
				}
			}
			//
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			//
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
				convertView = getLayoutInflater().inflate(R.layout.checkbox_item,
						null);
				holder.txt_Name = (TextView) convertView
						.findViewById(R.id.textView1);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.txt_Name.setText(data.get(position).toString());
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
				convertView = getLayoutInflater().inflate(R.layout.relative,
						null);
				holder.txt_fileName = (TextView) convertView
						.findViewById(R.id.ItemTitle);
				holder.txt_count = (TextView) convertView
						.findViewById(R.id.ItemText);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.txt_count
					.setText("延时" + data.get(position).getEventDelay() + "秒");
			holder.txt_fileName.setText(data.get(position).getEventContent());
			return convertView;
		}

		class ViewHolder {
			TextView txt_count;
			TextView txt_fileName;
			
		}
		

	}
	
}
