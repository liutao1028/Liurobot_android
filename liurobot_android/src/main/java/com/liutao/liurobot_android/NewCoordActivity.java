package com.liutao.liurobot_android;

import java.io.File;
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

import com.liutao.liurobot_android.entity.DrawView;
import com.liutao.liurobot_android.entity.ImageTools;
import com.liutao.liurobot_android.entity.MutiView;
import com.liutao.liurobot_android.entity.ServerSide;
import com.liutao.liurobot_android.util.NowTime;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NewCoordActivity extends Activity implements OnTouchListener, OnClickListener {
	private Button ok;
	private Button back;
	private ToggleButton btn_drawline;
	private TextView text_x;
	private TextView text_y;
	private TextView text_z;
	private TextView text_w;
	private TextView text_direction;
	// 自定义View
	private MutiView mutiView;
	private EditText et_new_coord_name;
	private Intent intent;
	private ServerSide server;
	private String sendCommands;
	private String mapId;
	private String mapUrl;
	public static final int SAVECOR = 100;
	public static final int COORREFRESH = 101;
	public static final int MAPFRESH = 102;
	private static String filename = "robotconfig.xml";
	private static File xmlPath = new File(Environment.getExternalStorageDirectory(), filename);
	int count = 0;
	private Timer timer;
	private TimerTask task = new TimerTask() {

		@Override
		public void run() {
			count++;
		}
	};
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SAVECOR:
				if (server != null) {
					if (server.result_.equals("OK")) {
						Toast.makeText(NewCoordActivity.this, "保存坐标成功！！！！", Toast.LENGTH_SHORT).show();
					} else if (server.result_.equals("NO")) {
						Toast.makeText(NewCoordActivity.this, "保存坐标失败！！！！", Toast.LENGTH_SHORT).show();
					} else if (count > 5) {
						Toast.makeText(NewCoordActivity.this, "操作超时！！！！", Toast.LENGTH_SHORT).show();
					} else {
						mHandler.sendEmptyMessageDelayed(SAVECOR, 500);
					}
				}
				break;
			case COORREFRESH:
				text_x.setText("" + mutiView.x_d);
				text_y.setText("" + mutiView.y_d);
				text_direction.setText("" + mutiView.z_d);
				break;
			case MAPFRESH:
				mutiView.postInvalidate();
				// mutiView.invalidate();
				break;
			}
		}

	};
	private Bitmap mapBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_coord);
		ok = (Button) findViewById(R.id.btn_coor_save);
		back = (Button) findViewById(R.id.btn_coor_back);
		btn_drawline = (ToggleButton) findViewById(R.id.toggbtn_drawline_on);
		// btn_drawlineoff = (Button) findViewById(R.id.btn_drawline_off);
		et_new_coord_name = (EditText) findViewById(R.id.et_new_coord_name);
		mutiView = (MutiView) findViewById(R.id.iv_tuceng01);
		text_x = (TextView) findViewById(R.id.tv_coor_x);
		text_y = (TextView) findViewById(R.id.tv_coor_y);
		text_z = (TextView) findViewById(R.id.tv_coor_z);
		text_w = (TextView) findViewById(R.id.tv_coor_w);
		text_direction = (TextView) findViewById(R.id.tv_coor_dir);
		intent = getIntent();
		mapId = intent.getStringExtra("mapId");
		mapUrl = intent.getStringExtra("mapUrl");
		mapBitmap = ImageTools.readImage(Environment.getExternalStorageDirectory() + "/" + mapId + ".png");
		Log.i("bbbbb", mapBitmap.toString());
		Log.i("bbbbb", mapBitmap.toString());
		Log.i("bbbbb", Environment.getExternalStorageDirectory() + "/" + mapId + ".png");
		mutiView.setBitmapaa(mapBitmap);
		mHandler.sendEmptyMessage(MAPFRESH);
		timer = new Timer();
		btn_drawline.setOnClickListener(this);
		// btn_drawlineoff.setOnClickListener(this);
		ok.setOnClickListener(this);
		back.setOnClickListener(this);
		mutiView.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getPointerCount() == 1) {
			if (mutiView.flag_touch == 0) {
				float rx = event.getRawX();
				float ry = event.getRawY();
				Log.i("aaaaa", "触摸点的绝对坐标X：" + rx);
				Log.i("aaaaa", "触摸点的绝对坐标Y：" + ry);

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mutiView.x_down_tmp = event.getX();
					mutiView.y_down_tmp = event.getY();
					// mPath.moveTo(event.getX(), event.getY());
					break;
				case MotionEvent.ACTION_MOVE:
					// mPath.lineTo(event.getX(), event.getY());
					mutiView.x_down = mutiView.x_down_tmp;
					mutiView.y_down = mutiView.y_down_tmp;

					float[] tmp_values = new float[9];
					mutiView.matrix.getValues(tmp_values);
					mutiView.x_d = (mutiView.x_down - tmp_values[2]) / (tmp_values[0] * mutiView.bitmapwidth);
					mutiView.y_d = (mutiView.y_down - tmp_values[5]) / (tmp_values[4] * mutiView.bitmapheight);

					mutiView.x_move = event.getX();
					mutiView.y_move = event.getY();

					mutiView.x_x = mutiView.x_move - mutiView.x_down;
					mutiView.y_y = mutiView.y_move - mutiView.y_down;
					if (mutiView.y_y == 0)
						mutiView.y_y = 0.000001f;
					mutiView.z_d = mutiView.x_x / mutiView.y_y;

					mutiView.onDraw_flag = true;
					mHandler.sendEmptyMessage(COORREFRESH);
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:

					break;
				}
			}
		} else if (event.getPointerCount() == 2) {
			if (mutiView.flag_touch == 1) {
				switch (event.getActionMasked()) {
				case MotionEvent.ACTION_POINTER_DOWN:
				case MotionEvent.ACTION_DOWN:
					// MotionEvent obtain =
					// MotionEvent.obtain(event.getDownTime(),
					// event.getEventTime(), MotionEvent.ACTION_UP,
					// event.getX(),
					// event.getY(), event.getMetaState());
					// gesture.onTouchEvent(obtain);
					mutiView.lastDis = ((float) Math.sqrt(
							Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2)));
					mutiView.lastDegrees = ((float) Math
							.toDegrees(Math.atan2(event.getY(0) - event.getY(1), event.getX(0) - event.getX(1))));
					break;
				case MotionEvent.ACTION_MOVE:
					float px = (event.getX(0) + event.getX(1)) / 2;
					float py = (event.getY(0) + event.getY(1)) / 2;
					float dis = ((float) Math.sqrt(
							Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2)));
					float degrees = ((float) Math
							.toDegrees(Math.atan2(event.getY(0) - event.getY(1), event.getX(0) - event.getX(1))));
					float[] values = new float[9];

					mutiView.matrix.getValues(values);
					Log.i("aaaa", "matrix values[0] sinX" + values[0] + "");
					if (values[0] >= 2.0f) {
						if (dis / mutiView.lastDis <= 1) {
							mutiView.matrix.postScale(dis / mutiView.lastDis, dis / mutiView.lastDis, px, py);
						}
					} else if (values[0] <= 1.0f) {
						if (dis / mutiView.lastDis >= 1) {
							mutiView.matrix.postScale(dis / mutiView.lastDis, dis / mutiView.lastDis, px, py);
						}
					} else {

						// if (dis / lastDis / values[0] >= 2)
						// {
						// matrix.postScale(1, 1, px, py);
						// }
						// else if (dis / lastDis / values[0] <= 1)
						// {
						//
						// matrix.postScale(1, 1, px, py);
						//
						//
						// }
						// if (dis / lastDis / values[0] < 2 && dis / lastDis /
						// values[0] > 1 )
						{
							mutiView.matrix.postScale(dis / mutiView.lastDis, dis / mutiView.lastDis, px, py);
						}
					}
					// matrix.postScale(dis / lastDis, dis / lastDis, px, py);
					// matrix.postRotate(degrees - lastDegrees, px, py);
					mutiView.lastDis = dis;
					mutiView.lastDegrees = degrees;
					break;
				case MotionEvent.ACTION_POINTER_UP:
					Log.d("bbbbbbbbbb", "onTouchEvent: " + String.format("%x", event.getAction()));
					break;
				}
			}

		}
		mutiView.gesture.onTouchEvent(event);
		mutiView.invalidate();
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.toggbtn_drawline_on:
			if (!btn_drawline.isChecked()) {
				mutiView.flag_touch = 1;

			} else {
				mutiView.flag_touch = 0;
			}
			break;
		// case R.id.btn_drawline_off:
		// mutiView.flag_touch = 1;
		// break;
		case R.id.btn_coor_save:
			Log.i("bbbbb", "onclick 进入成功!!");
			// if (server != null) {
			// sendCommands = text_x.getText().toString() + "," +
			// text_y.getText().toString() + ","
			// + text_z.getText().toString() + "," + text_z.getText().toString()
			// + ","
			// + text_w.getText().toString();
			// server.Send_Command = "@xinjianzuobiao" + sendCommands;
			// timer.schedule(task, 0, 1000);
			// mHandler.sendEmptyMessageDelayed(SAVECOR, 500);
			// 坐标保存至xml文件
			String newCoordName = et_new_coord_name.getText().toString();
			if (newCoordName.equals("")) {
				Toast.makeText(NewCoordActivity.this, "请输入新建坐标的名称!", Toast.LENGTH_SHORT).show();
				} 
			else {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setIgnoringElementContentWhitespace(false);
				try {
					DocumentBuilder db = dbf.newDocumentBuilder();
					Log.i("bbbbb", "进入保存方法");
					Document parse = db.parse(xmlPath);
					Log.i("bbbbb", "拿到paser");
					NodeList librarylist = parse.getElementsByTagName("maplibrary");
					Element map_library = (Element) librarylist.item(0);
					NodeList mapnode_list = map_library.getElementsByTagName("map");
					Log.i("bbbbb", "拿到,Map");

					for (int i = 0; i < mapnode_list.getLength(); i++) {
						Element mapnode = (Element) mapnode_list.item(i);
						if (mapnode.getAttribute("id").equals(mapId)) {
							Log.i("bbbbb", "匹配到mapID");
							NodeList coords = mapnode.getElementsByTagName("coords");
							Log.i("bbbbb", "coords 是:" + coords.toString());
							Element coordsnode = (Element) coords.item(0);
							Element coornode = parse.createElement("coord");
							Log.i("bbbbb", "coords 是:" + coornode.toString());
							coornode.setAttribute("name", newCoordName);// 此处为Edittext.gettext,界面应有一个edittext控件保存坐标name；
							coornode.setAttribute("id", new NowTime().getCurrentTime());
							Element coor_x = parse.createElement("x");
							coor_x.setTextContent(text_x.getText().toString());
							coornode.appendChild(coor_x);
							Element coor_y = parse.createElement("y");
							coor_y.setTextContent(text_y.getText().toString());
							coornode.appendChild(coor_y);
							Element coor_z = parse.createElement("z");
							coor_z.setTextContent(text_direction.getText().toString());
							coornode.appendChild(coor_z);
							Element coor_w = parse.createElement("w");
							coor_w.setTextContent(text_w.getText().toString());
							coornode.appendChild(coor_w);
							Element coor_dir = parse.createElement("dir");
							coor_dir.setTextContent(text_direction.getText().toString());
							coornode.appendChild(coor_dir);
							coordsnode.appendChild(coornode);
						}
					}
					TransformerFactory factory = TransformerFactory.newInstance();
					Transformer former = factory.newTransformer();
					former.transform(new DOMSource(parse), new StreamResult(new File(xmlPath.toString())));
					Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(getApplicationContext(), CoordinateManageActivity.class);
					intent.putExtra("mapId", mapId);
					startActivity(intent);
					// 保存成功 跳转回原页面

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.btn_coor_back:
			Intent intent2 = new Intent(getApplicationContext(), CoordinateManageActivity.class);
			intent2.putExtra("mapname", mapId);
			startActivity(intent2);
			break;
		}

	}
}
