package com.liutao.liurobot_android.entity;

import android.R.string;

public class ActionBean {
	String name;
	String time;
	String speed;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public ActionBean(String name, String time, String speed) {
		super();
		this.name = name;
		this.time = time;
		this.speed = speed;
	}
	@Override
	public String toString() {
		return "ActionBean [name=" + name + ", time=" + time + ", speed=" + speed + "]";
	}

}
