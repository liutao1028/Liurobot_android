package com.liutao.liurobot_android.util;

public class NowTime {
	//mapId = Long.toString(id);
	public  String getCurrentTime(){
		long id = System.currentTimeMillis();
		return Long.toString(id);
	}
}
