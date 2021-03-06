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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MediaControlActivity extends Activity {
	TextView select;
	ListView list;
	MediaAdapter myAdapter;
	private ArrayList<String> myMediaList;

	private static String filename = "robotconfig.xml";
	private static File xmlPath = new File(
			Environment.getExternalStorageDirectory(), filename);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_media);
		select = (TextView) findViewById(R.id.txt_select);
		list = (ListView) findViewById(R.id.mapslist);
		myMediaList = new ArrayList<String>();
		getDataList();
		myAdapter = new MediaAdapter(myMediaList);
		list.setAdapter(myAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				select.setText(myMediaList.get(position));
			}
		});

	}

	public void onclick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.button1:
			startActivity(new Intent(getApplicationContext(),
					ControlActivity.class));
			break;
		case R.id.button2:
			startActivity(new Intent(getApplicationContext(),
					NewMediaActivity.class));
			break;
		case R.id.button3:
			String select_txt = select.getText().toString();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringElementContentWhitespace(true);
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document xmldoc = db.parse(xmlPath);
				NodeList librarylist = xmldoc
						.getElementsByTagName("medialibrary");
				Element item = (Element) librarylist.item(0);

				NodeList actionlist = item.getElementsByTagName("medias");
				for (int i = 0; i < actionlist.getLength(); i++) {
					Element node = (Element) actionlist.item(i);
					String attribute = node.getAttribute("name");
					System.out.println(attribute);
					if (attribute.equals(select_txt)) {
						item.removeChild(node);
						myAdapter.removeData(select_txt);
						select.setText("");
					}
				}
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer former = factory.newTransformer();
				former.transform(new DOMSource(xmldoc), new StreamResult(
						new File(xmlPath.toString())));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		default:
			break;

		}
	}

	private void getDataList() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);

		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(xmlPath);
			NodeList nodeList = doc.getElementsByTagName("medias");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element root = (Element) nodeList.item(i);
				myMediaList.add(root.getAttribute("name"));

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// listview适配器
	class MediaAdapter extends BaseAdapter {
		List<String> data;

		public List<String> getData() {
			return data;
		}

		public MediaAdapter(List<String> list) {
			data = list;
		}

		public void setData(List<String> list) {
			data = list;
		}

		public void addData(String name) {
			data.add(name);
			notifyDataSetChanged();
		}

		public void removeData(String name) {
			data.remove(name);
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
				convertView = getLayoutInflater().inflate(
						R.layout.checkbox_item, null);
				holder.txt_name = (TextView) convertView
						.findViewById(R.id.textView1);

				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.txt_name.setText("媒体名：" + data.get(position).toString());

			return convertView;
		}

		class ViewHolder {
			TextView txt_name;

		}

	}
}
