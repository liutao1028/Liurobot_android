package com.liutao.liurobot_android;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.liutao.liurobot_android.entity.ImageTools;
import com.liutao.liurobot_android.entity.ServerSide;
import com.liutao.liurobot_android.setting.TtsSettings;
import com.liutao.liurobot_android.setting.UnderstanderSettings;
import com.liutao.liurobot_android.util.ApkInstaller;
import com.liutao.liurobot_android.util.JsonParser;
import com.liutao.liurobot_android.util.Player;
import com.liutao.liurobot_android.view.DiyView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;//假如用到位置提醒功能，需要import该类
import com.baidu.location.Poi;

public class TtsDemo extends BaseActivity
		implements OnCompletionListener, OnLongClickListener, OnClickListener, OnPreparedListener, OnErrorListener {
	// 语音听写变量声明
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

	private static String TAG = TtsDemo.class.getSimpleName();
	// 音乐播放地址
	private String musicUrl = "http://file.kuyinyun.com/group1/M00/3A/4E/rBBGdVPUcWOAWNr_ABePVrJEUNU634.mp3";
	public MediaPlayer mediaPlayer; // 媒体播放器
	public Player player;
	// 语义理解对象（语音到语义）。
	private SpeechUnderstander mSpeechUnderstander;
	// 语义理解对象（文本到语义）。
	private TextUnderstander mTextUnderstander;
	// 语音合成对象
	private SpeechSynthesizer mTts;
	// 默认发音人
	private String voicer = "vinn";

	private String[] mCloudVoicersEntries;
	private String[] mCloudVoicersValue;
	// 缓冲进度
	private int mPercentForBuffering = 0;
	// 播放进度ss
	private int mPercentForPlaying = 0;
	// 云端/本地单选按钮
	// 引擎类型 初始化为在线
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	// 语记安装助手类
	ApkInstaller mInstaller;

	private Toast mToast;
	private SharedPreferences mSharedPreferences;
	private SharedPreferences mUnderstanSharedPreferences;
	// 语音识别 变量申明
	// 语音识别对象
	private SpeechRecognizer mAsr;
	// 缓存
	private SharedPreferences mYuYinShiBieSharedPreferences;
	// 本地语法文件
	private String mLocalGrammar = null;
	// 本地词典
	private String mLocalLexicon = null;
	// 云端语法文件
	private String mCloudGrammar = null;

	private static final String KEY_GRAMMAR_ABNF_ID = "grammar_abnf_id";
	private static final String GRAMMAR_TYPE_ABNF = "abnf";
	private static final String GRAMMAR_TYPE_BNF = "bnf";
	private String mYuYinShiBieEngineType = null;

	private DiyView diyView;

	/** 返回结果，开始说话 */
	public final static int SPEECH_START = 0;
	public final static int IMAGESHOW_ON = 110;
	public final static int IMAGESHOW_OFF = 109;
	public final static int VIDEOSHOW_ON = 111;
	public final static int VIDEOSHOW_OFF = 113;
	public final static int MUSICSHOW = 112;
	public final static int FACESHOW = 115;
	public final static int FALLINLOVE_ON = 117;
	public final static int FLAGEVENTSTART = 116;
	public final static int SHUAI_ON = 118;
	/** 开始识别 */
	public final int RECOGNIZE_RESULT = 1;
	/** 开始识别 */
	public final int RECOGNIZE_START = 2;
	// USB发现列表初始化
	private UsbManager mUsbManager;
	private static final int MESSAGE_REFRESH = 101;
	private static final long REFRESH_TIMEOUT_MILLIS = 5000;
	// usb结果初始化
	private static UsbSerialDriver sDriver = null;
	private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
	private ImageView iv_image;
	private SerialInputOutputManager mSerialIoManager;
	private VideoView video_video;
	// private final SerialInputOutputManager.Listener mListener = new
	// SerialInputOutputManager.Listener() {
	//
	// @Override
	// public void onRunError(Exception e) {
	// Log.d(TAG, "Runner stopped.");
	// }
	//
	// @Override
	// public void onNewData(final byte[] data) {
	// TtsDemo.this.runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// TtsDemo.this.updateReceivedData(data);
	// }
	// });
	// }
	// };

	/** Simple container for a UsbDevice and its driver. */
	private static class DeviceEntry {
		public UsbDevice device;
		public UsbSerialDriver driver;

		DeviceEntry(UsbDevice device, UsbSerialDriver driver) {
			this.device = device;
			this.driver = driver;
		}
	}

	int flag_event_music = 0;
	int flag_event_video = 0;
	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SPEECH_START:
				startUnderstand();
				break;
			case MESSAGE_REFRESH:
				// refreshDeviceList();
				myHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
				break;
			case IMAGESHOW_ON:
				Log.i("wwwww", "执行了 IMAGESHOW_ON");
				diyView.setVisibility(View.GONE);
				video_video.setVisibility(View.GONE);
				iv_image.setVisibility(View.VISIBLE);
				iv_image.setImageBitmap(bitmap_event);
				iv_image.invalidate();

				break;
			case IMAGESHOW_OFF:
				// iv_image.setVisibility(View.GONE);
				// diyView.setVisibility(View.VISIBLE);
				break;
			case VIDEOSHOW_ON:
				if (player != null)
					player.stop();
				diyView.setVisibility(View.GONE);
				iv_image.setVisibility(View.GONE);
				video_video.setVisibility(View.VISIBLE);
				video_video.setVideoPath(fileUrl);
				Log.i("wwwww", "mp4_fileUrl: " + fileUrl);
				video_video.start();
				video_video.requestFocus();
				video_video.setOnCompletionListener(TtsDemo.this);
				video_video.setOnErrorListener(TtsDemo.this);
				break;
			case VIDEOSHOW_OFF:
				// diyView.setVisibility(View.VISIBLE);
				// iv_image.setVisibility(View.GONE);
				// video_video.setVisibility(View.GONE);
				// diyView.setVisibility(View.VISIBLE);
				break;
			case MUSICSHOW:
				if (video_video != null)
					video_video.stopPlayback();

				video_video.setVisibility(View.INVISIBLE);

				player = new Player();
				Log.i("wwwww", " player");
				player.playUrl(fileUrl);
				Log.i("wwwww", "player.playUrl(fileUrl);  执行了");
				player.mediaPlayer.setOnCompletionListener(TtsDemo.this);
				Log.i("wwwww", " 执行完了MUSICSHOW");
				break;
			case FACESHOW:
				iv_image.setVisibility(View.GONE);
				video_video.setVisibility(View.GONE);
				diyView.setVisibility(View.VISIBLE);
				diyView.setMovieResource(R.raw.robot_listen);
				diyView.invalidate();
				if (video_video != null)
					video_video.stopPlayback();

				if (player != null)
					player.stop();
				// delayUnderstand();
				break;
			case FALLINLOVE_ON:
				diyView.setMovieResource(R.raw.robot_love);
				diyView.invalidate();
				break;
			case SHUAI_ON:
				diyView.setMovieResource(R.raw.robot_shuai);
				diyView.invalidate();
				break;
			case FLAGEVENTSTART:
				if (FLAG_START_EVENT == 3) {
					EventComplete_Flag = true;
					eventThread = new Thread() {
						@Override
						public void run() {
							super.run();
							eventLooper(strResult[0], strResult[1]);
						}
					};
					eventThread.start();
				} else {
					myHandler.sendEmptyMessageDelayed(FLAGEVENTSTART, 500);
				}
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}

	};
	Bitmap bitmap_event = null;
	// 百度地图Location Client的初始化
	public LocationClient mLocationClient = null;
	// public BDLocationListener myListener = new MyLocationListener();
	// 计时器
	private static final int TIMECOUNTOUT = 3000;
	int timeCount = 0;
	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			if (timeCount < TIMECOUNTOUT) {
				timeCount++;
			}
		}
	};

	protected void onNewIntent(Intent intent) {

	};

	@SuppressLint("ShowToast")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 横屏启动
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// 取消状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 取消状态栏
		// getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
		// getWindow().getDecorView().setSystemUiVisibility(10);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		// 百度地图的初始化在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		// SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_uishow);
		// 初始化识别无UI识别对象
		// 使用SpeechRecognizer对象，可根据回调消息自定义界面；
		// usb初始化
		// 12 01 最新应用ID 583f8c45
		SpeechUtility.createUtility(this, SpeechConstant.APPID + "=583f8c45");
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

		// 初始化APP 57fcbd22 57df59f4 57fda978 5800b09a 58043b71
		// initLayout();
		// 初始化合成对象 mTtsInitListener mIatInitListener
		mTts = SpeechSynthesizer.createSynthesizer(TtsDemo.this, mTtsInitListener);
		mIat = SpeechRecognizer.createRecognizer(TtsDemo.this, mIatInitListener);
		// 初始化对象。
		// mSpeechUnderstander =
		// SpeechUnderstander.createUnderstander(TtsDemo.this,
		// mSpeechUdrInitListener); mTextUdrInitListener
		mTextUnderstander = TextUnderstander.createTextUnderstander(TtsDemo.this, mTextUdrInitListener);

		// 云端发音人名称列表
		mCloudVoicersEntries = getResources().getStringArray(R.array.voicer_cloud_entries);
		mCloudVoicersValue = getResources().getStringArray(R.array.voicer_cloud_values);

		mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME, MODE_PRIVATE);
		mUnderstanSharedPreferences = getSharedPreferences(UnderstanderSettings.PREFER_NAME, Activity.MODE_PRIVATE);
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		iv_image = (ImageView) findViewById(R.id.iv_image);

		video_video = (VideoView) findViewById(R.id.video_video);

		video_video.setMediaController(new MediaController(this));
		video_video.setOnClickListener(this);
		mInstaller = new ApkInstaller(TtsDemo.this);
		diyView = (DiyView) findViewById(R.id.diy_view);
		diyView.setMovieResource(R.raw.robot_listen);
		diyView.setOnLongClickListener(this);
		diyView.setOnClickListener(this);
		// int heightt = diyView.getHeight();
		// int widtht = diyView.getWidth();
		// 默认设备屏幕的宽高
		WindowManager wm1 = this.getWindowManager();
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm1.getDefaultDisplay().getMetrics(outMetrics);
		// int width2 = outMetrics.widthPixels;
		// int height2 = outMetrics.heightPixels;
		// 申明Location类
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		// mLocationClient.registerLocationListener( myListener ); //注册监听函数
	}

	/**
	 * 语音合成 初始化监听器。
	 */
	private InitListener mIatInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败，错误码：" + code);
			} else {
				// 初始化成功，之后可以调用startSpeaking方法
				// 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
				// 正确的做法是将onCreate中的startSpeaking调用移至这里
				// 设置参数
				if (isNetworkAvailable(TtsDemo.this)) {
					// Toast.makeText(getApplicationContext(), "当前有可用网络！",
					// Toast.LENGTH_LONG).show();
					mEngineType = SpeechConstant.TYPE_CLOUD;
					setParam();
				} else {
					// Toast.makeText(getApplicationContext(), "当前没有可用网络！",
					// Toast.LENGTH_LONG).show();
					mEngineType = SpeechConstant.TYPE_LOCAL;
					setParam();
				}
				int codeStart = mTts.startSpeaking("欢迎使用，我是小薇，有什么可以帮您？", mTtsListener);
				// /**
				// * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
				// * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
				// */
				// String path =
				// Environment.getExternalStorageDirectory()+"/tts.pcm";
				// int code = mTts.synthesizeToUri(text, path, mTtsListener);

				if (codeStart != ErrorCode.SUCCESS) {
					if (codeStart == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
						// 未安装则跳转到提示安装页
						mInstaller.install();
					} else {
						showTip("语音合成失败,错误码: " + codeStart);
					}
				}
			}
			// 计时器时间
			Timer timer = new Timer(true);
			timer.schedule(task, 200, 1000);
			// 初始化完成 开始听写语音
		}

	};
	/**
	 * 初始化监听
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败,错误码：" + code);
			}
		}
	};
	/**
	 * 初始化监听器（文本到语义）
	 */
	private InitListener mTextUdrInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "textUnderstanderListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败,错误码：" + code);
			}
		}
	};

	/**
	 * 检查当前网络是否可用
	 * 
	 * @param
	 * @return
	 */

	public boolean isNetworkAvailable(Activity activity) {
		Context context = activity.getApplicationContext();
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					// System.out.println(i + "===状态===" +
					// networkInfo[i].getState());
					// System.out.println(i + "===类型===" +
					// networkInfo[i].getTypeName());
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param text
	 */
	private void startHeChengYuYin(String text) {
		if (isNetworkAvailable(this)) {
			// Toast.makeText(getApplicationContext(), "当前有可用网络！",
			// Toast.LENGTH_LONG).show();
			mEngineType = SpeechConstant.TYPE_CLOUD;
			// 设置合成语音参数
			setParam();

		} else {
			// Toast.makeText(getApplicationContext(), "当前没有可用网络！",
			// Toast.LENGTH_LONG).show();
			mEngineType = SpeechConstant.TYPE_LOCAL;
			// 设置合成语音参数
			setParam();

		}
		int code = mTts.startSpeaking(text, mTtsListener);
		// /**
		// * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
		// * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
		// */
		// String path =
		// Environment.getExternalStorageDirectory()+"/tts.pcm";
		// int code = mTts.synthesizeToUri(text, path, mTtsListener);

		if (code != ErrorCode.SUCCESS) {
			if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
				// 未安装则跳转到提示安装页
				mInstaller.install();
			} else {
				showTip("语音合成失败,错误码: " + code);
				delayUnderstand();

			}
		}
	}

	int ret = 0;
	int flag_xitong = 0;
	int flag_linglu = 0;

	public void startUnderstand() {
		// 判断标志位 跳转系统界面
		// Log.i("wwwww", "进入了startUnderstand");
		if (server.setCoordOK == true) {
			if (flag_linglu == 1) {
				startHeChengYuYin("尊敬的各位领导,大会马上开始啦,请大家入场,我走的比较慢,各位领导不要着急哦");
				flag_linglu = 0;
			} else if (flag_linglu == 2) {
				startHeChengYuYin("请各位领导就座");
				flag_linglu = 0;
			} else if (flag_linglu == 3) {
				startHeChengYuYin("大家好");
				flag_linglu = 0;
			}
			server.setCoordOK = false;

		} else if (flag_xitong == 1 && EventComplete_Flag == false) {
			Log.i("bbbbb", "离线进入了识别方法");
			// Log.i("wwwww", "进入了startUnderstandflag_xitong == 1 &&
			// EventComplete_Flag == false");
			mIatResults.clear();
			if (isNetworkAvailable(this)) {
				// Toast.makeText(getApplicationContext(), "当前有可用网络！",
				// Toast.LENGTH_LONG).show();
				mEngineType = SpeechConstant.TYPE_CLOUD;
				// 设置参数
				setIatParam();
			} else {
				// Toast.makeText(getApplicationContext(), "当前没有可用网络！",
				// Toast.LENGTH_LONG).show();
				mEngineType = SpeechConstant.TYPE_LOCAL;
				// 设置参数
				setIatParam();
			}
			// 不显示听写对话框
			ret = mIat.startListening(mRecognizerListener);

			if (ret != ErrorCode.SUCCESS) {
				showTip("听写失败,错误码：" + ret);
			} else {
				showTip(getString(R.string.text_begin));
			}
		} else {
			delayUnderstand();
		}

	}

	/**
	 * 开始语音录入延时操作
	 */
	public void delayUnderstand() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 在一次会话交流结束或者异常终止，间隔时间
					// if (player == null) {
					Thread.sleep(100);
					myHandler.obtainMessage(SPEECH_START).sendToTarget();
					// }
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {

		@Override
		public void onSpeakBegin() {
			showTip("开始播放");
			FLAG_START_EVENT = 0;
			diyView.setMovieResource(R.raw.robot_speak);
			diyView.invalidate();
		}

		@Override
		public void onSpeakPaused() {
			showTip("暂停播放");
			diyView.setMovieResource(R.raw.robot_listen);
			diyView.invalidate();
		}

		@Override
		public void onSpeakResumed() {
			showTip("继续播放");
			diyView.setMovieResource(R.raw.robot_speak);
			diyView.invalidate();
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
			// 合成进度
			mPercentForBuffering = percent;
			showTip(String.format(getString(R.string.tts_toast_format), mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			// 播放进度
			mPercentForPlaying = percent;
			showTip(String.format(getString(R.string.tts_toast_format), mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				// showTip("播放完成");
				diyView.setMovieResource(R.raw.robot_listen);
				diyView.invalidate();
				FLAG_START_EVENT = 3;
				delayUnderstand();

			} else if (error != null) {
				// showTip(error.getPlainDescription(true));
				delayUnderstand();
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}
	};

	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	/**
	 * 参数设置
	 * 
	 * @param
	 * @return
	 */
	private void setParam() {
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 根据合成引擎设置相应参数
		if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
			// 设置在线合成发音人
			mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
			// 设置合成语速
			mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "65"));
			// 设置合成音调
			mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "65"));
			// 设置合成音量
			mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "100"));
			// 设置合成以及识别的采样率16k 8k
			mTts.setParameter(SpeechConstant.SAMPLE_RATE, "16k");
		} else {
			// 本地合成
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
			// 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
			mTts.setParameter(SpeechConstant.VOICE_NAME, "");
			// 设置合成以及识别的采样率16k 8k
			// mTts.setParameter(SpeechConstant.SAMPLE_RATE, "16k");

			/**
			 * TODO 本地合成不设置语速、音调、音量，默认使用语记设置 开发者如需自定义参数，请参考在线合成参数设置
			 */
		}
		// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
		// 设置播放合成音频打断音乐播放，默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMA T参数语记需要更新版本才能生效
		mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTts.stopSpeaking();
		// 退出时释放连接
		mTts.destroy();
		myHandler.removeCallbacksAndMessages(null);

		if (mTextUnderstander.isUnderstanding())
			mTextUnderstander.cancel();
		mTextUnderstander.destroy();
		// 退出时释放连接
		mIat.cancel();
		mIat.destroy();
	}

	@Override
	protected void onResume() {
		// 移动数据统计分析
		super.onResume();
		// usb
		myHandler.sendEmptyMessage(MESSAGE_REFRESH);
		// usb结果
		flag_xitong = 1;
	}

	@Override
	protected void onPause() {
		// 移动数据统计分析
		super.onPause();
		// stopUnderstand();
		flag_xitong = 0;
		// usb
		myHandler.removeMessages(MESSAGE_REFRESH);
		// usb结果
		// stopIoManager();
		if (sDriver != null) {
			try {
				sDriver.close();
			} catch (IOException e) {
				// Ignore.
			}
			sDriver = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("确认退出吗？").setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 点击“确认”后的操作
						TtsDemo.this.finish();
						System.exit(0);

					}
				}).setNegativeButton("返回", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 点击“返回”后的操作,这里不设置没有任何操作
					}
				}).show();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.media.MediaPlayer.OnCompletionListener#onCompletion(android.media
	 * .MediaPlayer)
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		if (mp != null) {
			mp.stop();
			mp = null;
			if (player != null) {
				player = null;
			}
			if (video_video != null) {
				// flag_event_video = 1;
				video_video.setVisibility(View.INVISIBLE);
				iv_image.setVisibility(View.GONE);
				// video_video.setVisibility(View.GONE);
				diyView.setVisibility(View.VISIBLE);
			}
		}
		delayUnderstand();
	}

	// /**
	// * usb
	// */
	// private void refreshDeviceList() {
	// new AsyncTask<Void, Void, List<DeviceEntry>>() {
	// @Override
	// protected List<DeviceEntry> doInBackground(Void... params) {
	// Log.d(TAG, "Refreshing device list ...");
	// SystemClock.sleep(1000);
	// final List<DeviceEntry> result = new ArrayList<DeviceEntry>();
	// for (final UsbDevice device : mUsbManager.getDeviceList().values()) {
	// final List<UsbSerialDriver> drivers =
	// UsbSerialProber.probeSingleDevice(mUsbManager, device);
	// Log.d(TAG, "Found usb device: " + device);
	// if (drivers.isEmpty()) {
	// Log.d(TAG, " - No UsbSerialDriver available.");
	// result.add(new DeviceEntry(device, null));
	// } else {
	// for (UsbSerialDriver driver : drivers) {
	// Log.d(TAG, " + " + driver);
	// result.add(new DeviceEntry(device, driver));
	// }
	// }
	// }
	// return result;
	// }
	//
	// int FLAG = 0;
	//
	// @Override
	// protected void onPostExecute(List<DeviceEntry> result) {
	// if (result.size() > 0) {
	// Log.i("usb321", result.size() + "设备数量");
	// for (DeviceEntry device : result) {
	// int deviceId = device.device.getDeviceId();
	// String deviceName = device.device.getDeviceName();
	// Log.i("usb321", "设备名称" + deviceName + ",设备ID：" + deviceId);
	// }
	// DeviceEntry entry = result.get(0);
	// UsbSerialDriver driver = entry.driver;
	// sDriver = driver;
	// Log.d(TAG, "Resumed, sDriver=" + sDriver);
	// if (sDriver == null) {
	// FLAG = 0;
	// } else if (sDriver != null && FLAG == 0) {
	// try {
	// Log.i("usb321", result.size() + "");
	// Log.i("usb321", result.size() + "");
	// sDriver.open();
	// sDriver.setParameters(115200, 8, UsbSerialDriver.STOPBITS_1,
	// UsbSerialDriver.PARITY_NONE);
	// } catch (IOException e) {
	// Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
	// try {
	// sDriver.close();
	// } catch (IOException e2) {
	// // Ignore.
	// }
	// sDriver = null;
	// return;
	// }
	// FLAG = 1;
	// }
	// onDeviceStateChange();
	//
	// }
	// }
	// }.execute((Void) null);
	// }
	//
	// /**
	// * usb结果
	// */
	// private void stopIoManager() {
	// if (mSerialIoManager != null) {
	// Log.i(TAG, "Stopping io manager ..");
	// mSerialIoManager.stop();
	// mSerialIoManager = null;
	// }
	// }
	//
	// private void startIoManager() {
	// if (sDriver != null) {
	// Log.i(TAG, "Starting io manager ..");
	// mSerialIoManager = new SerialInputOutputManager(sDriver, mListener);
	//
	// mExecutor.submit(mSerialIoManager);
	// }
	// }
	//
	// private void onDeviceStateChange() {
	// stopIoManager();
	// startIoManager();
	// }
	//
	// byte[] strContainer = new byte[1024];
	// String recvdata = null;
	// int lenCont = 0;
	//
	// private void updateReceivedData(byte[] data) {
	// final String message = "Read " + data.length + " bytes: \n" +
	// HexDump.dumpHexString(data) + "\n\n";
	// }

	// 语音听写的语音录入设置参数
	/**
	 * 参数设置
	 * 
	 * @param
	 * @return
	 */
	public void setIatParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		String lag = mSharedPreferences.getString("iat_language_preference", "mandarin");
		// 设置引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);

		}
		// 设置合成以及识别的采样率16k 8k
		// mIat.setParameter(SpeechConstant.SAMPLE_RATE, "16k");
		mIat.setParameter(SpeechConstant.SAMPLE_RATE, "8k");
		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
	}

	// 配置文件
	private static String filename = "robotconfig.xml";
	private static File xmlPath = new File(Environment.getExternalStorageDirectory(), filename);

	/**
	 * 听写监听器。
	 */
	// 跟本地语音库对比结果
	String result_local = "";
	boolean EventComplete_Flag = false;
	String[] strResult;
	Thread eventThread = null;
	int FLAG_START_EVENT = 0;
	private RecognizerListener mRecognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
		}

		@Override
		public void onError(SpeechError error) {
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// showTip(error.getPlainDescription(true));

			delayUnderstand();
		}

		@Override
		public void onEndOfSpeech() {
			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
			// showTip("结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			// String text =
			// JsonParser.parseIatResult(results.getResultString());
			String Tts_resultText = printResult(results);
			// 判断语音听写 结果 处理命令 语音听写在线解析返回结果
			Log.i("wwwww", "语音听写结果      :" + Tts_resultText);
			if (isLast) {
				if (mTextUnderstander.isUnderstanding()) {
					mTextUnderstander.cancel();
				} else {
					// TODO: 获取xml语音集合 拿到List 循环遍历 比对结果
					String mapId = getNowMapId();
					// 通用地图ID
					String mapCommonId = getCommonMapId();

					// 本地当前地图执行
					strResult = getLocalQuestion(Tts_resultText, mapId);
					result_local = strResult[2];

					// 本地通用地图执行
					if (result_local.equals("")) {
						strResult = getLocalQuestion(Tts_resultText, mapCommonId);
						result_local = strResult[2];
						if (result_local.equals("")) {
							result_local = getLocalVoiceResponse(Tts_resultText);
							if (result_local.equals("")) {
								ret = mTextUnderstander.understandText(Tts_resultText, textListener);
								if (ret != 0) {
									showTip("语义理解失败,错误码" + ret);
									delayUnderstand();
								}
							} else {
								startHeChengYuYin(result_local);
							}
						} else {
							// TODO: 比对结果字符串
							if (result_local.equals("aaaaa")) {
								Log.i("wwwww", "进入了匹配 aaaaa");
								startHeChengYuYin("嗯，好的");
								flag_linglu = 1;
								FLAG_START_EVENT = 3;
								myHandler.sendEmptyMessage(FLAGEVENTSTART);
							} else if (result_local.equals("bbbbb")) {
								startHeChengYuYin("嗯，好");
								flag_linglu = 2;
								FLAG_START_EVENT = 3;
								myHandler.sendEmptyMessage(FLAGEVENTSTART);
							} else if (result_local.equals("ccccc")) {
								startHeChengYuYin("嗯，来了");
								flag_linglu = 3;
								FLAG_START_EVENT = 3;
								myHandler.sendEmptyMessage(FLAGEVENTSTART);
							} else {
								startHeChengYuYin(result_local);
								result_local = "";
								FLAG_START_EVENT = 0;
								myHandler.sendEmptyMessage(FLAGEVENTSTART);
							}
						}
					} else {
						// TODO: 比对结果字符串
						Log.i("wwwww", "1111111111111111");
						if (result_local.equals("aaaaa")) {
							startHeChengYuYin("嗯，好的");
							result_local = "";
							flag_linglu = 1;
							FLAG_START_EVENT = 0;
							myHandler.sendEmptyMessage(FLAGEVENTSTART);
							Log.i("wwwww", "222222222222222");
						} else if (result_local.equals("bbbbb")) {
							startHeChengYuYin("嗯，好");
							result_local = "";
							flag_linglu = 2;
							FLAG_START_EVENT = 0;
							myHandler.sendEmptyMessage(FLAGEVENTSTART);
						} else if (result_local.equals("ccccc")) {
							startHeChengYuYin("嗯，来了");
							result_local = "";
							flag_linglu = 3;
							FLAG_START_EVENT = 0;
							myHandler.sendEmptyMessage(FLAGEVENTSTART);
						} else {
							startHeChengYuYin(result_local);
							result_local = "";
							FLAG_START_EVENT = 0;
							myHandler.sendEmptyMessage(FLAGEVENTSTART);
							Log.i("wwwww", "33333333333333333");
						}
					}
					Log.i("wwwww", "44444444444444444");
					// // 本地语音库执行
					// if (result_local.equals("")) {
					// result_local = getLocalVoiceResponse(Tts_resultText);
					// if (!result_local.equals("")) {
					// startHeChengYuYin(result_local);
					// }
					// }
					// // else{
					// // Log.i("wwwww", "执行了语音库的合成语音");
					// // startHeChengYuYin(result_local);
					// // }
					// Log.i("wwwww", "Tts_resultText 是：" + Tts_resultText);
					// Log.i("wwwww", "result_local 是：" + result_local);
					// if (result_local.equals("")) {
					// ret = mTextUnderstander.understandText(Tts_resultText,
					// textListener);
					// if (ret != 0) {
					// showTip("语义理解失败,错误码" + ret);
					// delayUnderstand();
					// }
					// }
				}
			}
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("当前正在说话，音量大小：" + volume);
			Log.d(TAG, "返回音频数据：" + data.length);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}

	};

	private String getLocalVoiceResponse(String Tts_resultText) {
		Log.i("wwwww", "进入了本地语音库!!!!");
		mVoicelist = new ArrayList<String>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(false);
		DocumentBuilder db;
		String local_question = "";
		String localVoiceChecked = "";
		try {
			db = dbf.newDocumentBuilder();
			Document parse = db.parse(xmlPath);
			NodeList voicelibrarylist = parse.getElementsByTagName("voicelibrary");

			Element voicelibraryroot = (Element) voicelibrarylist.item(0);
			NodeList voice_nodelist = voicelibraryroot.getElementsByTagName("voice");

			int[] voice_list_count = new int[voice_nodelist.getLength()];
			int[] voice_list_count_x = new int[voice_nodelist.getLength()];
			String[] voice_list_answer = new String[voice_nodelist.getLength()];

			for (int j = 0; j < voice_nodelist.getLength(); j++) {
				Element voicenode = (Element) voice_nodelist.item(j);
				// String voices =
				// voicenode.getElementsByTagName("question").item(0).getFirstChild().getNodeValue();
				local_question = voicenode.getElementsByTagName("question").item(0).getFirstChild().getNodeValue();
				// String[] local_question_array = voices.split("/");

				// int max_count_x = 0;
				// int tmp_voice_list_count_x = 0;
				// for (int m = 0; m < local_question_array.length; m ++)
				// {
				// local_question = local_question_array[m];
				voice_list_count_x[j] = 0;
				voice_list_answer[j] = voicenode.getElementsByTagName("response").item(0).getFirstChild()
						.getNodeValue();

				int result_num = 0;
				for (result_num = 0; result_num < Tts_resultText.length() - 1; result_num++)
					for (int k = 0; k < local_question.length(); k++) {
						if (Tts_resultText.charAt(result_num) != local_question.charAt(k)) {
							continue;
						}
						// ?????
						++voice_list_count_x[j];
						++result_num;
					}
				// 把要比对的question的长度 赋给 voice列表count的j
				voice_list_count[j] = local_question.length();
				// }

			}
			int max_value = 0;
			int max_index = 0xFF;
			for (int l = 0; l < voice_list_count_x.length; l++) {
				if (voice_list_count_x[l] > max_value) {
					max_value = voice_list_count_x[l];
					max_index = l;
				}
			}
			boolean Send_Flag = false;
			if (max_index == 0xFF)
				localVoiceChecked = "";
			else {
				int tmp_count = voice_list_count[max_index];
				int tmp_count_x = voice_list_count_x[max_index];
				switch (tmp_count) {
				case 1:
				case 2:
				case 3:
					if (tmp_count - tmp_count_x == 0)
						Send_Flag = true;
					break;
				case 4:
				case 5:
					if (tmp_count - tmp_count_x <= 1)
						Send_Flag = true;
					break;
				case 6:
				case 7:
					if (tmp_count - tmp_count_x <= 2)
						Send_Flag = true;
					break;
				case 8:
				case 9:
					if (tmp_count - tmp_count_x <= 3)
						Send_Flag = true;
					break;
				}
			}
			if (Send_Flag) {

				localVoiceChecked = voice_list_answer[max_index];
				Log.i("wwwww", "String is :" + localVoiceChecked);

				Log.i("wwwww", "总长度：" + voice_list_count[max_index]);
				Log.i("wwwww", "包含长度：" + voice_list_count_x[max_index]);
			} else {
				localVoiceChecked = "";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return localVoiceChecked;
	}

	ArrayList<String> mVoicelist = null;
	String check_result = null;

	private String[] getLocalQuestion(String tts_result, String mapId) {
		String[] strings = new String[3];
		Log.i("wwwww", "进入了方法");
		Log.i("wwwww", "进入方法的 tts_result" + tts_result);

		// 当前地图ID
		// String mapId = getNowMapId();
		// 通用地图ID
		String mapCommonId = mapId;// getCommonMapId();
		strings[0] = mapCommonId;
		// 通用事件Id列表
		ArrayList<String> CommenEventIdList = getEventListDataId(mapCommonId);
		// 通用事件Voice列表
		ArrayList<String> CommenEventVoiceList = getEventListDataVocie(mapCommonId);
		// 本地语音库语音ID列表
		// ArrayList<String> localVoiceIdList = getLocalVoiceIdList();
		// //本地语音库语音 question 列表
		// ArrayList<String> localVoiceQuesList =
		// getLocalVoiceQuestionList();
		// //本地语音库语音answer列表
		// ArrayList<String> localVoiceAnswerList =
		// getLocalVoiceAnswerList();
		int[] eachLocalQues_list_count = new int[CommenEventIdList.size()];
		int[] eachLocalQues_list_count_x = new int[CommenEventIdList.size()];
		String[] eachLocalQues_list_answer = new String[CommenEventIdList.size()];
		String[] eachLocalQues_list_envent_id = new String[CommenEventIdList.size()];
		String checkedQuestion = "";
		for (int i = 0; i < CommenEventIdList.size(); i++) {
			String eachVoiceId = CommenEventVoiceList.get(i);
			checkedQuestion = getLocalVoiceQuestion(eachVoiceId);
			eachLocalQues_list_count_x[i] = 0;
			eachLocalQues_list_answer[i] = getLocalVoiceAnswer(eachVoiceId);
			eachLocalQues_list_envent_id[i] = CommenEventIdList.get(i);
			Log.i("wwwww", "eachLocalQues_list_envent_id[i]" + eachLocalQues_list_envent_id[i]);
			int result_num = 0;
			for (result_num = 0; result_num < tts_result.length() - 1; result_num++)
				for (int k = 0; k < checkedQuestion.length(); k++) {
					if (tts_result.charAt(result_num) != checkedQuestion.charAt(k)) {
						continue;
					}
					++eachLocalQues_list_count_x[i];
					++result_num;
				}
			eachLocalQues_list_count[i] = checkedQuestion.length();
		}
		int max_value = 0;
		int max_index = 0xFF;
		for (int l = 0; l < eachLocalQues_list_count_x.length; l++) {
			if (eachLocalQues_list_count_x[l] > max_value) {
				max_value = eachLocalQues_list_count_x[l];
				max_index = l;
			}
		}
		boolean Send_Flag = false;
		if (max_index == 0xFF)
			strings[2] = "";
		else {
			int tmp_count = eachLocalQues_list_count[max_index];
			int tmp_count_x = eachLocalQues_list_count_x[max_index];
			switch (tmp_count) {
			case 1:
			case 2:
			case 3:
				if (tmp_count - tmp_count_x == 0)
					Send_Flag = true;
				break;
			case 4:
			case 5:
				if (tmp_count - tmp_count_x <= 1)
					Send_Flag = true;
				break;
			case 6:
			case 7:
				if (tmp_count - tmp_count_x <= 2)
					Send_Flag = true;
				break;
			case 8:
			case 9:
				if (tmp_count - tmp_count_x <= 3)
					Send_Flag = true;
				break;
			}
		}
		if (Send_Flag) {
			// Element tmp_node = (Element) voice_nodelist.item(max_index);
			// check_result =
			// tmp_node.getElementsByTagName("response").item(0).getFirstChild().getNodeValue();
			check_result = eachLocalQues_list_answer[max_index];
			Log.i("wwwww", "String is :" + checkedQuestion);
			Log.i("wwwww", "MAP_ID:" + mapCommonId);
			Log.i("wwwww", "ENVENT_ID:" + eachLocalQues_list_envent_id[max_index]);
			strings[1] = eachLocalQues_list_envent_id[max_index];
			strings[2] = check_result;
			Log.i("wwwww", "总长度：" + eachLocalQues_list_count[max_index]);
			Log.i("wwwww", "包含长度：" + eachLocalQues_list_count_x[max_index]);
		} else {
			strings[2] = "";
		}
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return strings;
	}

	String fileUrl = "";

	/**
	 * 事件轮循 方法
	 */
	private void eventLooper(String mapId, String eventId) {
		Log.i("wwwww", "执行了eventLooper方法");
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
				if (mapId.equals(root.getAttribute("id").toString())) {
					NodeList eventnodelist = root.getElementsByTagName("event");
					for (int j = 0; j < eventnodelist.getLength(); j++) {
						Element eventnode = (Element) eventnodelist.item(j);
						String localEventId = eventnode.getAttribute("id");
						if (eventId.equals(localEventId)) {
							Log.i("wwwww", "匹配到eventId");
							NodeList eventContentList = eventnode.getElementsByTagName("contentname");
							for (int k = 0; k < eventContentList.getLength(); k++) {
								Element eventcontent = (Element) eventContentList.item(k);
								String eventContentComm = eventcontent.getFirstChild().getNodeValue();

								String[] eventContentResult = eventContentComm.split(":");
								Log.i("www", "eventContentResult[0]:" + eventContentResult[0] + "eventContentResult[1]"
										+ eventContentResult[1] + "eventContentResult[2]" + eventContentResult[2]);
								String delayActionTime = eventContentResult[2];
								long delayAction = Long.parseLong(delayActionTime) * 1000;
								switch (eventContentResult[0]) {
								case "actions":
									Log.i("wwwww", "进入了actions节点");
									String actionsId = eventContentResult[1];
									ArrayList<String> actions = getActionList(actionsId);
									String actionComm = "";
									for (int l = 0; l < actions.size(); l++) {
										String action = actions.get(l);
										String[] actionsum = action.split(":");
										if (actionsum.length > 2) {
											actionComm += actionsum[0] + "#" + actionsum[1] + "#" + actionsum[2];
										} else {
											actionComm += actionsum[0];
										}
										if (l >= actions.size() - 1) {
											break;
										}
										actionComm += ",";
									}
									String actionCommRes = "@SetActions:" + actionComm + "!";
									server.setSendCommands(actionCommRes, "0");
									Log.i("wwwww", "actionCommRes是：" + actionCommRes);
									// server.Send_Command = actionCommRes;
									// Thread.sleep(delayAction);

									break;
								case "media":
									Log.i("wwwww", "media");
									fileUrl = eventContentResult[1];
									if (fileUrl.endsWith("mp3") || fileUrl.endsWith("wav")) {
										myHandler.sendEmptyMessage(MUSICSHOW);
										// Log.i("wwwww", "3333");
										// while (flag_event_music == 0) {
										// Thread.sleep(100);
										// }
										// Log.i("wwwww", "4444");
										// flag_event_music = 0;
										// Thread.sleep(delayAction);
									} else if (fileUrl.endsWith("mp4") || fileUrl.endsWith("rmvb")) {
										myHandler.sendEmptyMessage(VIDEOSHOW_ON);
										// Log.i("wwwww", "1111");
										// while (flag_event_video == 0) {
										// Thread.sleep(100);
										// }
										// Log.i("wwwww", "2222");
										// flag_event_video = 0;
										// Thread.sleep(delayAction);
										// myHandler.sendEmptyMessage(VIDEOSHOW_OFF);
									} else if (fileUrl.endsWith("jpg") || fileUrl.endsWith("png")) {
										bitmap_event = ImageTools.readImage(fileUrl);
										myHandler.sendEmptyMessage(IMAGESHOW_ON);
										// Thread.sleep(delayAction);
										// myHandler.sendEmptyMessage(IMAGESHOW_OFF);
										// Log.i("wwwww", "666");
									} else if (fileUrl.endsWith("love")) {
										myHandler.sendEmptyMessage(FALLINLOVE_ON);
									} else if (fileUrl.endsWith("shuai")) {
										myHandler.sendEmptyMessage(SHUAI_ON);
									}
									break;
								case "coord":
									Log.i("wwwww", "进入coordcase");
									ArrayList<String> coordList = getCoordList(mapId, eventContentResult[1]);
									String x = coordList.get(0);
									String y = coordList.get(1);
									String z = coordList.get(2);
									String w = coordList.get(3);
									// server.Send_Command =
									// "@SetXYZ:"+x+","+y+","+z+","+w+"!";
									//server.setSendCommands("@SetCoord:" + x + "," + y + "," + z + "," + w + "!", "0");
									server.setSendCommands("@SetXYZ:" + x + "," + y + "," + z + "," + w + "!", "0");
					
									// Thread.sleep(delayAction);
									break;
								case "delay":
									Thread.sleep(delayAction);
									break;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("wwwww", "" + e.getMessage());
		}
		myHandler.sendEmptyMessage(FACESHOW);
		EventComplete_Flag = false;
		Log.i("wwwww", "执行了 方法底部");
		Log.i("wwwww", "eventLooper（）方法执行完了");
//		Looper.prepare();
//		delayUnderstand();
//		Looper.loop();
	}

	private ArrayList<String> getCoordList(String mapId, String coorId) {
		ArrayList<String> coorderList = new ArrayList<String>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(false);
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document parse = db.parse(xmlPath);
			NodeList librarylist = parse.getElementsByTagName("maplibrary");
			Element map_library = (Element) librarylist.item(0);
			NodeList mapnode_list = map_library.getElementsByTagName("map");
			Log.i("bbbbb", "拿到,Map");
			for (int i = 0; i < mapnode_list.getLength(); i++) {
				Element mapnode = (Element) mapnode_list.item(i);
				if (mapnode.getAttribute("id").equals(mapId)) {
					Log.i("bbbbb", "匹配到mapID");
					NodeList coords = mapnode.getElementsByTagName("coords");
					Element coordsnode = (Element) coords.item(0);
					NodeList coordList = coordsnode.getElementsByTagName("coord");
					for (int j = 0; j < coordList.getLength(); j++) {
						Element coordnode = (Element) coordList.item(j);
						if (coorId.equals(coordnode.getAttribute("id"))) {
							String x = coordnode.getElementsByTagName("x").item(0).getFirstChild().getNodeValue();
							String y = coordnode.getElementsByTagName("y").item(0).getFirstChild().getNodeValue();
							String z = coordnode.getElementsByTagName("z").item(0).getFirstChild().getNodeValue();
							String w = coordnode.getElementsByTagName("w").item(0).getFirstChild().getNodeValue();
							coorderList.add(x);
							coorderList.add(y);
							coorderList.add(z);
							coorderList.add(w);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return coorderList;
	}

	private ArrayList<String> getActionList(String actionsId) {
		ArrayList<String> actionList = new ArrayList<String>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(xmlPath);
			NodeList nodeList = doc.getElementsByTagName("actionlibrary");
			Element item = (Element) nodeList.item(0);
			NodeList mapnodelist = item.getElementsByTagName("actions");
			for (int i = 0; i < mapnodelist.getLength(); i++) {
				Element root = (Element) mapnodelist.item(i);
				if (actionsId.equals(root.getAttribute("id").toString())) {
					NodeList eventnodelist = root.getElementsByTagName("action");
					for (int j = 0; j < eventnodelist.getLength(); j++) {
						String actionContent = eventnodelist.item(j).getFirstChild().getNodeValue();
						actionList.add(actionContent);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return actionList;
	}

	private ArrayList<String> getLocalVoiceAnswerList() {
		ArrayList<String> mVoiceAnswerlist = new ArrayList<String>();
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
				String local_question = voicenode.getElementsByTagName("response").item(0).getFirstChild()
						.getNodeValue();
				mVoiceAnswerlist.add(local_question);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mVoiceAnswerlist;
	}

	private ArrayList<String> getLocalVoiceQuestionList() {
		ArrayList<String> mVoiceQuestionlist = new ArrayList<String>();
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
				String local_question = voicenode.getElementsByTagName("question").item(0).getFirstChild()
						.getNodeValue();
				mVoiceQuestionlist.add(local_question);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mVoiceQuestionlist;
	}

	private String getLocalVoiceAnswer(String eachVoiceId) {
		String voiceAnswer = "";
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
				if (eachVoiceId.equals(voiceId)) {
					voiceAnswer = voicenode.getElementsByTagName("response").item(0).getFirstChild().getNodeValue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return voiceAnswer;
	}

	private String getLocalVoiceQuestion(String eachVoiceId) {
		String voiceQestion = "";
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
				if (eachVoiceId.equals(voiceId)) {
					voiceQestion = voicenode.getElementsByTagName("question").item(0).getFirstChild().getNodeValue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return voiceQestion;
	}

	private ArrayList<String> getLocalVoiceIdList() {
		ArrayList<String> mVoicelist = new ArrayList<String>();
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
				mVoicelist.add(voiceId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mVoicelist;
	}

	private ArrayList<String> getEventListDataId(String mapId) {
		ArrayList<String> eventid_list = new ArrayList<String>();
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
				if (mapId.equals(root.getAttribute("id").toString())) {
					NodeList eventnodelist = root.getElementsByTagName("event");
					for (int j = 0; j < eventnodelist.getLength(); j++) {
						Element eventnode = (Element) eventnodelist.item(j);
						String eventId = eventnode.getAttribute("id");
						eventid_list.add(eventId);
						Log.i("wwwww", "eventid_list.size() " + eventid_list.size());
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eventid_list;
	}

	private ArrayList<String> getEventListDataVocie(String mapId) {
		ArrayList<String> eventVoice_list = new ArrayList<String>();
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
				if (mapId.equals(root.getAttribute("id").toString())) {
					NodeList eventnodelist = root.getElementsByTagName("event");
					for (int j = 0; j < eventnodelist.getLength(); j++) {
						Element eventnode = (Element) eventnodelist.item(j);
						eventVoice_list.add(eventnode.getAttribute("voice"));
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eventVoice_list;
	}

	private String getCommonMapId() {
		String commonMapId = "";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);

		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(xmlPath);
			NodeList nodeList = doc.getElementsByTagName("maplibrary");
			Element item = (Element) nodeList.item(0);
			NodeList mapnodelist = item.getElementsByTagName("map");
			Element root = (Element) mapnodelist.item(0);
			commonMapId = root.getAttribute("id");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return commonMapId;
	}

	private String getNowMapId() {
		String mapId = "";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);

		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(xmlPath);
			NodeList nodeList = doc.getElementsByTagName("nowmap");
			Element item = (Element) nodeList.item(0);
			mapId = item.getAttribute("mapid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapId;
	}

	private String printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());
		// 防止重复监听，即说话一次，产生两次结果
		mIat.stopListening();
		if (TextUtils.isEmpty(text)) {
			return "";
		}
		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mIatResults.put(sn, text);
		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}
		String res = resultBuffer.toString();
		return res;
	}

	String textFrom = null;
	String textResult = null;
	int len__ = 0;
	byte[] data_ = null;
	String actionStr = null;
	private TextUnderstanderListener textListener = new TextUnderstanderListener() {
		Runnable writeRunnable = new Runnable() {
			@Override
			public void run() {
				if (mSerialIoManager != null) {
					mSerialIoManager.writeAsync(data_);
				} else {
					len__ = 0;
				}
			}
		};
		Runnable understandRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(300);
					if (mSerialIoManager != null) {
						len__ = mSerialIoManager.result_;
					}
					if (len__ > 0) {
						startHeChengYuYin(actionStr);
						actionStr = null;
					} else {
						startHeChengYuYin("有点累了，休息一下吧");
					}
					len__ = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		@Override
		public void onResult(final UnderstanderResult result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (null != result) {
						// 显示
						Log.d(TAG, "understander result：" + result.getResultString());
						String text = result.getResultString();
						if (!TextUtils.isEmpty(text)) {
							Log.i("wwwww", text);
							try {
								JSONObject object = new JSONObject(text);
								textFrom = object.optString("text");
								// 判断条件在answer为空且player为空的时候，在此加入其它的条件
								// 建议改成switch 翻译，股票，酒店（hotel），航班，地图，火车，提醒，参观，天气
								if (object.optString("service").equals("openQA") || timeCount < TIMECOUNTOUT) {
									switch (object.optString("service")) {
									case "music":// 音乐
										JSONObject obj = object.optJSONObject("data");
										JSONArray array = obj.optJSONArray("result");
										if (array == null) {
											startHeChengYuYin("对不起 人家唱歌要钱，我唱歌要命！");
										} else {
											JSONObject objMusic = array.getJSONObject(0);
											if (objMusic == null) {
												startHeChengYuYin("对不起 人家唱歌要钱，我唱歌要命！");
											} else {
												musicUrl = objMusic.optString("downloadUrl");
												player = new Player();
												player.mediaPlayer.setOnCompletionListener(TtsDemo.this);
												player.playUrl(musicUrl);
											}
										}
										break;
									case "weather":
										JSONObject objTianqi = object.optJSONObject("data");
										JSONArray arrayTianqi = objTianqi.optJSONArray("result");
										JSONObject objTodayWeather = arrayTianqi.getJSONObject(0);
										if (objTodayWeather != null) {
											String date = objTodayWeather.optString("date");
											String city = objTodayWeather.optString("city");
											String weather = objTodayWeather.optString("weather");
											String tempRange = objTodayWeather.optString("tempRange");
											startHeChengYuYin(city + date + weather + tempRange);
										} else {
											startHeChengYuYin("对不起，夜观星象的时候睡着了，不知道天气什么状况");
										}
										break;
									case "chat":
										JSONObject oJsonObjectchat = object.optJSONObject("answer");
										if (oJsonObjectchat != null) {
											textResult = oJsonObjectchat.getString("text");
											startHeChengYuYin(textResult);
											textResult = null;
										} else {
											startHeChengYuYin("对不起，我没有听清！");
										}
										break;
									case "openQA":
										JSONObject oJsonObjectopenQA = object.optJSONObject("answer");
										if (oJsonObjectopenQA != null) {
											textResult = oJsonObjectopenQA.getString("text");
											// @SetVelocity:300,1,1000!
											String[] strResult = textResult.split(":");
											if (textResult.equals("wakeup")) {
												timeCount = 0;
												startHeChengYuYin("干嘛呀！");
												break;
											} else if (timeCount >= TIMECOUNTOUT) {
												delayUnderstand();
												break;
											} else if (strResult[0].equals("@SetXYZ")) {
												String[] strSetXYZ = textResult.split("!");
												switch (strSetXYZ[1]) {
												case "0": // 回原点
													data_ = new String(strSetXYZ[0] + '!').getBytes();
													new Thread(writeRunnable).start();
													actionStr = "好的，我回去啦";
													new Thread(understandRunnable).start();
													break;

												case "1": // 去某一点
													data_ = new String(strSetXYZ[0] + '!').getBytes();
													new Thread(writeRunnable).start();
													actionStr = "好的，马上过去";
													new Thread(understandRunnable).start();
													break;
												default:
													break;
												}

											} else if (strResult[0].equals("@SetVelocity")) {
												String[] strs = strResult[1].split(",");
												String condition = strs[1];
												data_ = textResult.getBytes();
												switch (condition) {
												case "0":
													// 0前进 1后退 2 左转3 右转
													new Thread(writeRunnable).start();
													actionStr = "好的，向前走";
													new Thread(understandRunnable).start();
													break;
												case "1":
													// 0前进 1后退 2 左转3 右转
													new Thread(writeRunnable).start();
													actionStr = "好的，向后退";
													new Thread(understandRunnable).start();
													break;
												case "2":
													// 0前进 1后退 2 左转3 右转
													new Thread(writeRunnable).start();
													actionStr = "好的，向左转";
													new Thread(understandRunnable).start();
													break;
												case "3":
													// 0前进 1后退 2 左转3 右转
													new Thread(writeRunnable).start();
													actionStr = "好的，向右转";
													new Thread(understandRunnable).start();
													break;
												case "4":
													// 0前进 1后退 2 左转3 右转
													new Thread(writeRunnable).start();
													actionStr = "好的，向后转";
													new Thread(understandRunnable).start();
													break;
												}
											} else {
												startHeChengYuYin(textResult);
											}
											textResult = null;
										} else {
											startHeChengYuYin("知识储备不足，咱们换个话题吧");
										}
										break;
									case "datetime":
										JSONObject oJsonObjectdatetime = object.optJSONObject("answer");
										if (oJsonObjectdatetime != null) {
											textResult = oJsonObjectdatetime.getString("text");
											startHeChengYuYin(textResult);
											textResult = null;
										} else {
											startHeChengYuYin("对不起，我没有听清！");
										}
										break;
									case "calc":
										JSONObject oJsonObjectcalc = object.optJSONObject("answer");
										if (oJsonObjectcalc != null) {
											textResult = oJsonObjectcalc.getString("text");
											startHeChengYuYin(textResult);
											textResult = null;
										} else {
											startHeChengYuYin("哎哟，难倒我了！");
										}
										break;
									// case "hotel":
									// break;
									// case "flight":
									// break;
									// case "restaurant":
									// break;
									// case "train":
									// break;
									// case "map":
									// break;
									// case "schedule":// 提醒
									// break;
									// case "translation":
									// break;
									// case "stock":// 股票
									// break;
									default:
										// startHeChengYuYin("抱歉啊，知识储备不足，我答不出来哦");
										// 在service 不为上述情况 连续出血，还是
										delayUnderstand();
										break;
									}
									if (timeCount < TIMECOUNTOUT)
										timeCount = 0;
								} else {
									delayUnderstand();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							startHeChengYuYin("对不起，我没有听清楚");
						}
					} else {// 在rsult为空的时候调用
						delayUnderstand();
					}
				}
			});
		}

		@Override
		public void onError(SpeechError error) {
			delayUnderstand();
		}
	};

	/**
	 * @param v
	 * @return
	 */
	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.diy_view:
			startActivity(new Intent(this, Main_ui.class));
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.video_video:
			if (video_video.isPlaying()) {
				video_video.pause();
			} else {
				video_video.start();
			}
			break;
		case R.id.diy_view:
			if (getWindow().getDecorView().getSystemUiVisibility() != View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) {
				// 取消状态栏
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
				// 取消状态栏
				// getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
				// getWindow().getDecorView().setSystemUiVisibility(10);
				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
			}
			break;
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.i("ddddd", "播放错误！！！！");
		if (mp != null) {
			mp.stop();
			mp = null;
			if (video_video != null) {
				// flag_event_video = 1;
				video_video.setVisibility(View.INVISIBLE);
				iv_image.setVisibility(View.GONE);
				// video_video.setVisibility(View.GONE);
				diyView.setVisibility(View.VISIBLE);
			}
		}
		delayUnderstand();
		return false;
	}
}

// TODO:校验结果 tts_result 听写结果 匹配完返回 确定问题的答案/若没有返回""
// mVoicelist(String List)语音库集合
// int[] eachLocalQues_list_count_x = new int[mVoicelist.size()];
// int[] eachLocalQues_list_count = new int[mVoicelist.size()];
// for (int m = 0; m < mVoicelist.size(); m++) {
//
// eachLocalQues_list_count_x[m] = 0;
// int result_num = 0;
// for (result_num = 0; result_num < tts_result.length() - 1; result_num++)
// for (int k = 0; k < mVoicelist.get(m).length() - 1; k++) {
//
// if (tts_result.charAt(result_num) != mVoicelist.get(m).charAt(k)) {
// continue;
// }
//
// ++eachLocalQues_list_count_x[m];
// ++result_num;
//
// }
// eachLocalQues_list_count[m] = mVoicelist.get(m).length() - 1;
// Log.i("wwwww", "String is :" + mVoicelist.get(m));
// Log.i("wwwww", "eachLocalQues_list_count[" + m + "]" +
// eachLocalQues_list_count[m]);
// Log.i("wwwww", "eachLocalQues_list_count_x[" + m + "]" +
// eachLocalQues_list_count_x[m]);
// }