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
import org.w3c.dom.NodeList;

import com.liutao.liurobot_android.entity.ActionBean;
import com.liutao.liurobot_android.util.NowTime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NewActionActivity extends Activity {
	private static String filename = "robotconfig.xml";
	private static File xmlPath = new File(Environment.getExternalStorageDirectory(), filename);
	private static String[] baseAction = { "fwd", "bac", "lef", "rig", "rigturn", "lefturn" };
	private ArrayList<String> baseActionList;
	private ArrayList<ActionBean> newAcitonList;
	ListView baselist;
	ListView newlist;
	EditText speed_count;
	EditText time_count;
	EditText name;
	BaseAcitonAdapter baseAcitonAdapter;
	NewActionAdapter newActionAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_action);
		newlist = (ListView) findViewById(R.id.listview1);
		baselist = (ListView) findViewById(R.id.listview2);
		speed_count = (EditText) findViewById(R.id.editText2);
		time_count = (EditText) findViewById(R.id.editText3);
		name = (EditText) findViewById(R.id.editText1);
		baseActionList = new ArrayList<String>();
		newAcitonList = new ArrayList<ActionBean>();
		speed_count.setText("0");
		time_count.setText("0");

		getDatalist(baseAction);
		baseAcitonAdapter = new BaseAcitonAdapter(baseActionList);
		newActionAdapter = new NewActionAdapter(newAcitonList);

		baselist.setAdapter(baseAcitonAdapter);
		baselist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String Actionname = baseAction[position];
				String Actionspeedcount = speed_count.getText().toString();
				String Actiontimecount = time_count.getText().toString();
				ActionBean mediaBean = new ActionBean(Actionname, Actiontimecount, Actionspeedcount);
				newAcitonList.add(mediaBean);
				newActionAdapter.notifyDataSetChanged();
				speed_count.setText("0");
				time_count.setText("0");

				//
			}
		});
		newlist.setAdapter(newActionAdapter);
		newlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				newActionAdapter.removeData(newActionAdapter.getData().get(position));
			}
		});
	}

	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.btn_actions_save:
			if (newAcitonList.size() == 0) {
				Toast.makeText(getApplicationContext(), "动作列表不能为空！", Toast.LENGTH_SHORT).show();
			} else {
				if (name.getText().toString().length() == 0) {
					Toast.makeText(getApplicationContext(), "保存名称不能为空！", Toast.LENGTH_SHORT).show();
				} else {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setIgnoringElementContentWhitespace(false);
					try {
						DocumentBuilder db = dbf.newDocumentBuilder();
						Document parse = db.parse(xmlPath);
						NodeList librarylist = parse.getElementsByTagName("actionlibrary");
						Element library = (Element) librarylist.item(0);
						Element rootnode = parse.createElement("actions");
						rootnode.setAttribute("name", name.getText().toString());
						rootnode.setAttribute("id", new NowTime().getCurrentTime());
						for (int i = 0; i < newAcitonList.size(); i++) {
							Element sonnode = parse.createElement("action");
							sonnode.setTextContent(newAcitonList.get(i).getName().toString() + ":"
									+ newAcitonList.get(i).getTime().toString() + ":"
									+ newAcitonList.get(i).getSpeed().toString());
							rootnode.appendChild(sonnode);
						}
						library.appendChild(rootnode);

						TransformerFactory factory = TransformerFactory.newInstance();
						Transformer former = factory.newTransformer();
						former.transform(new DOMSource(parse), new StreamResult(new File(xmlPath.toString())));
						Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_SHORT).show();
						startActivity(new Intent(getApplicationContext(), ActionControlActivity.class));

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
			break;
		case R.id.button2:
			startActivity(new Intent(NewActionActivity.this, ActionControlActivity.class));
			break;
		case R.id.button9:
			if (speed_count.getText().length() != 0) {
				String str = speed_count.getText().toString();
				int sub = Integer.parseInt(str);
				if (sub > 0) {
					speed_count.setText(sub - 1 + "");
				}

			}
			break;
		case R.id.button10:
			if (speed_count.getText().length() != 0) {
				String str = speed_count.getText().toString();
				int add = Integer.parseInt(str);
				speed_count.setText(add + 1 + "");

			}
			break;
		case R.id.button11:
			if (time_count.getText().length() != 0) {
				String str = time_count.getText().toString();
				int sub = Integer.parseInt(str);
				if (sub > 0) {
					time_count.setText(sub - 1 + "");
				}

			}
			break;
		case R.id.button12:
			if (time_count.getText().length() != 0) {
				String str = time_count.getText().toString();
				int add = Integer.parseInt(str);
				time_count.setText(add + 1 + "");

			}
			break;

		default:
			break;
		}
	}

	private void getDatalist(String[] baseAciton) {
		for (int i = 0; i < baseAciton.length; i++) {
			baseActionList.add(baseAciton[i]);
		}

	}

	class BaseAcitonAdapter extends BaseAdapter {

		List<String> data;

		public List<String> getData() {
			return data;
			// p11
		}

		public BaseAcitonAdapter(List<String> list) {
			data = list;
		}

		public void setData(List<String> list) {
			data = list;
		}

		public void addData(String string) {
			data.add(string);
			notifyDataSetChanged();
			//
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
			// TODO Auto-generated method stub
			if(convertView==null){
				convertView = getLayoutInflater().inflate(R.layout.new_action_item, null);
			}
			TextView textView1 = (TextView) convertView.findViewById(R.id.textView1);

			textView1.setText(data.get(position).toString());

			return convertView;
		}
	}

	class NewActionAdapter extends BaseAdapter {
		List<ActionBean> data;

		public List<ActionBean> getData() {
			return data;
		}

		public NewActionAdapter(List<ActionBean> list) {
			data = list;
		}

		public void setData(List<ActionBean> list) {
			data = list;
		}

		public void addData(ActionBean actionBean) {
			data.add(actionBean);
			notifyDataSetChanged();
		}

		public void removeData(ActionBean actionBean) {
			data.remove(actionBean);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO this ok
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}//

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
				convertView = getLayoutInflater().inflate(R.layout.relative, null);
				holder.txt_actionName = (TextView) convertView.findViewById(R.id.ItemTitle);
				holder.txt_timecount = (TextView) convertView.findViewById(R.id.ItemText);
				holder.txt_speedcount = (TextView) convertView.findViewById(R.id.ItemText2);
				convertView.setTag(holder);
				//
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.txt_timecount.setText("时间" + data.get(position).getTime() + "秒");
			holder.txt_speedcount.setText("速度" + data.get(position).getSpeed() + "/秒");
			holder.txt_actionName.setText(data.get(position).getName());
			return convertView;
			//

		}

		class ViewHolder {
			TextView txt_timecount;
			TextView txt_actionName;
			TextView txt_speedcount;

		}

	}

}
