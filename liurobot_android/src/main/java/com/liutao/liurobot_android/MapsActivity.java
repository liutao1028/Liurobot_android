package com.liutao.liurobot_android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.liutao.liurobot_android.entity.Bean;
import com.liutao.liurobot_android.entity.ImageTools;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.CorrectionInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MapsActivity extends BaseActivity {
	private static String filename = "robotconfig.xml";
	private static File xmlPath = new File(Environment.getExternalStorageDirectory(), filename);
	private String sendCommands;
	private ArrayList<Bean> map_list;
	ImageView map_image;
	TextView mapnote_txt;
	MyAdapter adapter;
	ListView map_id_list;
	private TextView tv_mapname;
	public static final int SETMAP = 110;
	public static final int DELETEMAP = 120;
	public static final int GETNOWMAONAME = 130;
	String mapUrl;
	String mapId;
	String mapNote;
	String mapName;
	private Bitmap map_lue = null;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SETMAP:
				if (server != null) {
					if (server.result_.equals("@SetMap:0K!")) {
						Toast.makeText(MapsActivity.this, "应用成功！！！", Toast.LENGTH_SHORT).show();
						 setNowMap(mapId);
//						server.setSendCommands("@GetNowMapName!", "0");
//						mHandler.sendEmptyMessageDelayed(GETNOWMAONAME, 2000);
					} else if (server.result_.equals("@SetMap:NO!")) {
						Toast.makeText(MapsActivity.this, "应用失败！！！", Toast.LENGTH_SHORT).show();
					} else if (count > 60) {
						Toast.makeText(MapsActivity.this, "响应超时！！！", Toast.LENGTH_SHORT).show();
						count = 0;
					} else {
						mHandler.sendEmptyMessageDelayed(SETMAP, 500);
					}
				}
				break;
			case DELETEMAP:
				if (server != null) {
					if (server.result_.equals("OK")) {
						Toast.makeText(MapsActivity.this, "删除成功！！！", Toast.LENGTH_SHORT).show();
					} else if (server.result_.equals("NO")) {
						Toast.makeText(MapsActivity.this, "删除失败！！！", Toast.LENGTH_SHORT).show();
					} else if (count > 5) {
						Toast.makeText(MapsActivity.this, "响应超时！！！", Toast.LENGTH_SHORT).show();
						count = 0;
					} else {
						mHandler.sendEmptyMessageDelayed(DELETEMAP, 500);
					}
				}
				break;
			case GETNOWMAONAME:
				if (server != null) {
					if (!server.result_.isEmpty()) {
						String[] answer = server.result_.split(":");
						Log.i("ddddd", "@GetNowMapName返回结果：：：" + server.result_);
						if (answer[0].equals("@GetNowMapName")) {
							Log.i("ddddd", "answer[0].equals(@GetNowMapName)");
							Toast.makeText(MapsActivity.this, "设置当前地图成功", Toast.LENGTH_SHORT).show();
							String[] mapIDs = answer[1].split("!");
							String mapId = mapIDs[0];
							setNowMap(mapId);
						}
					} else if (count > 5) {
						Toast.makeText(MapsActivity.this, "响应超时！！！", Toast.LENGTH_SHORT).show();
						count = 0;
					} else {
						mHandler.sendEmptyMessageDelayed(GETNOWMAONAME, 500);
					}
				}
				break;
			default:
				break;
			}
		}

	};
	int count = 0;
	private TimerTask task = new TimerTask() {

		@Override
		public void run() {
			count++;

		}
	};
	private Timer timer;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps_ui);
		tv_mapname = (TextView) findViewById(R.id.tv_mapname);
		map_id_list = (ListView) findViewById(R.id.lv_maplist);
		map_image = (ImageView) findViewById(R.id.iv_mapsuolue);
		mapnote_txt = (TextView) findViewById(R.id.tv_mapnote);
		map_list = new ArrayList<Bean>();
		getMapListData();
		adapter = new MyAdapter(map_list);
		map_id_list.setAdapter(adapter);
		map_id_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String str_id = adapter.data.get(position).getMapId();
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setIgnoringElementContentWhitespace(true);
				try {
					DocumentBuilder builder = dbf.newDocumentBuilder();
					Document doc = builder.parse(xmlPath);
					NodeList nodeList = doc.getElementsByTagName("maplibrary");
					Element item = (Element) nodeList.item(0);
					NodeList mapnodelist = item.getElementsByTagName("map");

					for (int i = 0; i < mapnodelist.getLength(); i++) {
						Element map = (Element) mapnodelist.item(i);
						if (map.getAttribute("id").toString().equals(str_id)) {
							mapName = map.getAttribute("name");
							mapId = map.getAttribute("id");
							mapNote = map.getAttribute("note");
							mapUrl = map.getAttribute("url");
							tv_mapname.setText(mapName);
							mapnote_txt.setText(mapNote);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				map_lue = ImageTools.readImage(mapUrl);
				map_image.setImageBitmap(map_lue);
			}
		});
	}

	public void setNowMap(String mapId) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(false);
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document parse = db.parse(xmlPath);
			NodeList librarylist = parse.getElementsByTagName("nowmap");
			Element map = (Element) librarylist.item(0);
			map.setAttribute("mapid", mapId);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer former = factory.newTransformer();
			former.transform(new DOMSource(parse), new StreamResult(new File(xmlPath.toString())));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.btn_yingyong:
			Toast.makeText(MapsActivity.this, "应用", Toast.LENGTH_SHORT).show();
			sendCommands = mapId;
			// server.Send_Command = "@SetMap:" + sendCommands + "!";
			server.setSendCommands("@SetMap:" + sendCommands + "!", "0");

			// Log.i("bbbbb", server.Send_Command);
			if (timer == null) {
				timer = new Timer();
				timer.schedule(task, 0, 1000);
			}
			mHandler.sendEmptyMessageDelayed(SETMAP, 500);
			break;
		case R.id.btn_getnowmap:

		case R.id.btn_maps_back:
			startActivity(new Intent(MapsActivity.this, Main_ui.class));
			break;
		case R.id.btn_maps_edit_map:
			Toast.makeText(MapsActivity.this, "编辑地图", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(getApplicationContext(), EditMapActivity.class));
			break;
		case R.id.btn_maps_delete_map:
			String select_txt = tv_mapname.getText().toString();
			if (select_txt.equals("通用场景")) {
				Toast.makeText(MapsActivity.this, "通用场景不可删除", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MapsActivity.this, "删除地图", Toast.LENGTH_SHORT).show();
				sendCommands = tv_mapname.getText().toString();
				server.setSendCommands("@DeleteMap:" + sendCommands + "!", "0");
				// server.Send_Command = "@deletemap" + sendCommands;
				mHandler.sendEmptyMessageDelayed(DELETEMAP, 500);

				// 删除xml中对应代码

				// DocumentBuilderFactory dbf =
				// DocumentBuilderFactory.newInstance();
				// dbf.setIgnoringElementContentWhitespace(true);
				// try {
				// DocumentBuilder db = dbf.newDocumentBuilder();
				// Document xmldoc = db.parse(xmlPath);
				// NodeList librarylist =
				// xmldoc.getElementsByTagName("maplibrary");
				// Element item = (Element) librarylist.item(0);
				//
				// NodeList actionlist = item.getElementsByTagName("map");
				// for (int i = 0; i < actionlist.getLength(); i++) {
				// Element node = (Element) actionlist.item(i);
				// String attribute = node.getAttribute("name");
				// System.out.println(attribute);
				// if (attribute.equals(select_txt)) {
				// item.removeChild(node);
				// adapter.removeData(adapter.data.get(i));
				// tv_mapname.setText("");
				// }
				// }
				// TransformerFactory factory = TransformerFactory
				// .newInstance();
				// Transformer former = factory.newTransformer();
				// former.transform(new DOMSource(xmldoc),
				// new StreamResult(new File(xmlPath.toString())));
				//
				//
				// } catch (Exception e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			}
			break;
		case R.id.btn_maps_new_map:
			// 新建地图
			Toast.makeText(MapsActivity.this, "新建地图", Toast.LENGTH_SHORT).show();
			Intent intent_newmap = new Intent(MapsActivity.this, NewMapWaitActivity.class);
			startActivity(intent_newmap);
			break;
		case R.id.btn_maps_coord_manager:
			// 坐标管理
			String str_mapname = tv_mapname.getText().toString();
			if (str_mapname.equals("请选择")) {
				Toast.makeText(MapsActivity.this, "请选择地图", Toast.LENGTH_SHORT).show();
			} else {
				Intent intent_zuobiao = new Intent(getApplicationContext(), CoordinateManageActivity.class);
				intent_zuobiao.putExtra("mapId", mapId);
				intent_zuobiao.putExtra("mapUrl", mapUrl);
				startActivity(intent_zuobiao);
			}
			break;

		default:
			break;
		}
	}

	private void getMapListData() {
		map_list.clear();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(xmlPath);
			NodeList nodeList = doc.getElementsByTagName("maplibrary");
			Element item = (Element) nodeList.item(0);
			NodeList mapnodelist = item.getElementsByTagName("map");

			for (int i = 0; i < mapnodelist.getLength(); i++) {
				Element map = (Element) mapnodelist.item(i);
				Bean bean = new Bean();
				String mapid = map.getAttribute("id");
				String mapname = map.getAttribute("name");
				String mapurl = map.getAttribute("url");
				String mapnote = map.getAttribute("note");
				bean.setMapId(mapid);
				bean.setMapName(mapname);
				bean.setMapNote(mapnote);
				bean.setMapUrl(mapurl);
				map_list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class MyAdapter extends BaseAdapter {
		List<Bean> data;

		public List<Bean> getData() {
			return data;
		}

		public MyAdapter(List<Bean> list) {
			data = list;
		}

		public void setData(List<Bean> list) {
			data = list;
		}

		public void addData(Bean bean) {
			data.add(bean);
			notifyDataSetChanged();
		}

		public void removeData(Bean bean) {
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
				convertView = getLayoutInflater().inflate(R.layout.mapid_list_item, null);
				holder.txt_name = (TextView) convertView.findViewById(R.id.textView1);
				holder.txt_note = (TextView) convertView.findViewById(R.id.textView2);
				convertView.setTag(holder);

			} else
				holder = (ViewHolder) convertView.getTag();
			holder.txt_name.setText(data.get(position).getMapName());
			holder.txt_note.setText(data.get(position).getMapNote());

			return convertView;
		}

		class ViewHolder {
			TextView txt_name;
			TextView txt_note;
		}

	}

}
