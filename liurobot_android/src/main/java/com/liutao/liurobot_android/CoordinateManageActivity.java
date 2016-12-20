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

import com.liutao.liurobot_android.entity.CoordBean;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CoordinateManageActivity extends Activity {
	private TextView coordinate_name;
	private TextView coordinate_x;
	private TextView coordinate_y;
	private TextView coordinate_z;
	private TextView coordinate_w;
	private ListView list_coor;
	private ImageView iv_coor_manager_luetu;
	private MyAdapter myAdapter;
	private ArrayList<CoordBean> coordbean_list;
	private Intent intent;
	private String sendCommands;
	private String mapId;
	private static String filename = "robotconfig.xml";
	private static File xmlPath = new File(
			Environment.getExternalStorageDirectory(), filename);
	public static final int DELETECOR = 110;
	int count=0;
	private String mapUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coordinate_manage);
		coordinate_name =(TextView) findViewById(R.id.textView3);
		coordinate_x =(TextView) findViewById(R.id.textView10);
		coordinate_y =(TextView) findViewById(R.id.textView11);
		coordinate_z =(TextView) findViewById(R.id.textView12);
		coordinate_w =(TextView) findViewById(R.id.textView13);
		iv_coor_manager_luetu = (ImageView)findViewById(R.id.iv_coor_manager_luetu);
		coordbean_list = new ArrayList<CoordBean>();
		list_coor =(ListView) findViewById(R.id.listview);
		intent = getIntent();
		mapId = intent.getStringExtra("mapId");
		mapUrl = intent.getStringExtra("mapUrl");
		Bitmap bitmap_mapSuolue = ImageTools.readImage(mapUrl);
		iv_coor_manager_luetu.setImageBitmap(bitmap_mapSuolue);
		getCoordListData(mapId);
		myAdapter = new MyAdapter(coordbean_list);
		list_coor.setAdapter(myAdapter);
		list_coor.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				coordinate_name.setText(myAdapter.getData().get(position).getName());
				coordinate_x.setText(myAdapter.getData().get(position).getX());
				coordinate_y.setText(myAdapter.getData().get(position).getY());
				coordinate_z.setText(myAdapter.getData().get(position).getZ());
				coordinate_w.setText(myAdapter.getData().get(position).getW());
			}
		});
	}
	public void onclick(View view){
		switch (view.getId()) {
		case R.id.button2://返回
			Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
			intent.putExtra("mapId", mapId);
			startActivity(intent);
			//
			break;
		case R.id.button4://删除
			//删除xml中的对应内容
			String select_txt = coordinate_name.getText().toString();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringElementContentWhitespace(true);
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document xmldoc = db.parse(xmlPath);
				NodeList librarylist = xmldoc.getElementsByTagName("maplibrary");
				Element item = (Element) librarylist.item(0);
				NodeList actionlist = item.getElementsByTagName("map");	
				for (int i = 0; i < actionlist.getLength(); i++) {
					Element node = (Element) actionlist.item(i);
					String attribute = node.getAttribute("id");
					if (attribute.equals(mapId)) {
						NodeList elementsByTagName = node.getElementsByTagName("coords");
						 Element coords = (Element) elementsByTagName.item(0);
						 NodeList coordnode_list = coords.getElementsByTagName("coord");
						 for (int j = 0; j < coordnode_list.getLength(); j++) {
							 Element coor = (Element) coordnode_list.item(j);
							if (coor.getAttribute("name").equals(select_txt)) {
								coords.removeChild(coor);
								item.removeChild(node);
								myAdapter.removeData(myAdapter.data.get(j));
								coordinate_name.setText("");
								coordinate_x.setText("");
								coordinate_y.setText("");
								coordinate_z.setText("");
								coordinate_w.setText("");
							}
						}
					}
				}
				TransformerFactory factory = TransformerFactory
						.newInstance();
				Transformer former = factory.newTransformer();
				former.transform(new DOMSource(xmldoc),
						new StreamResult(new File(xmlPath.toString())));
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			break;
		case R.id.button5://新建
			Intent intent_xinjianzuobiao =new Intent(getApplicationContext(), NewCoordActivity.class);
			intent_xinjianzuobiao.putExtra("mapId", mapId);
			intent_xinjianzuobiao.putExtra("mapUrl", mapUrl);
			startActivity(intent_xinjianzuobiao);
			Toast.makeText(getApplicationContext(), "新建", Toast.LENGTH_SHORT).show();
			break;
		}
	}
	private void getCoordListData(String mapId){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);

		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(xmlPath);
			NodeList nodeList = doc.getElementsByTagName("maplibrary");
			if(nodeList!=null){
				Element item = (Element) nodeList.item(0);
				NodeList mapnodelist = item.getElementsByTagName("map");
				if(mapnodelist!=null){
					
					for (int i = 0; i < mapnodelist.getLength(); i++) {
						Element root = (Element) mapnodelist.item(i);
						if (mapId.equals(root.getAttribute("id").toString())) {
							NodeList nodelist = root.getElementsByTagName("coords");
							Element coordroot = (Element) nodelist.item(0);
							if(coordroot!=null){
							NodeList coordnodelist = coordroot.getElementsByTagName("coord");
							for (int j = 0; j < coordnodelist.getLength(); j++) {
								Element coornode = (Element) coordnodelist.item(j);
								String coordname = coornode.getAttribute("name");
								String coordid = coornode.getAttribute("id");
								String str_x = coornode.getElementsByTagName("x").item(0).getFirstChild().getNodeValue();
								String str_y = coornode.getElementsByTagName("y").item(0).getFirstChild().getNodeValue();
								String str_z = coornode.getElementsByTagName("z").item(0).getFirstChild().getNodeValue();
								String str_w = coornode.getElementsByTagName("w").item(0).getFirstChild().getNodeValue();
								CoordBean coordBean = new CoordBean(coordname, str_x, str_y, str_z, str_w, coordid);
								coordbean_list.add(coordBean);
								System.out.println(coordbean_list);
							}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	class MyAdapter extends BaseAdapter {
		List<CoordBean> data;
		public List<CoordBean> getData() {
			return data;
		}

		public MyAdapter(List<CoordBean> list) {
			data = list;
		}

		public void setData(List<CoordBean> list) {
			data = list;
		}

		public void addData(CoordBean coordBean) {
			data.add(coordBean);
			notifyDataSetChanged();
		}

		public void removeData(CoordBean coordBean) {
			data.remove(coordBean);
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
				convertView = getLayoutInflater().inflate(R.layout.checkbox_item,
						null);
				holder.txt_Name = (TextView) convertView
						.findViewById(R.id.textView1);
				convertView.setTag(holder);
				
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.txt_Name.setText(data.get(position).getName().toString());
			return convertView;
		}
	
		class ViewHolder {
			TextView txt_Name;
			        
		}
		

	}
}
