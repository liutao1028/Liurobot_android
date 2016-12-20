package com.liutao.liurobot_android.entity;

public class CoordBean {
	String name;
	String x;
	String y;
	String z;
	String w;
	String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public String getZ() {
		return z;
	}
	public void setZ(String z) {
		this.z = z;
	}
	public String getW() {
		return w;
	}
	public void setW(String w) {
		this.w = w;
	}
	
	@Override
	public String toString() {
		return "CoordBean [name=" + name + ", x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + ", id=" + id + "]";
	}
	public CoordBean(String name, String x, String y, String z, String w, String id) {
		super();
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.id = id;
	}
}
