/** 
 *
 */
package com.liutao.liurobot_android.entity;



public class Word {
	private String w;
	private int sc;
	public Word() {
		super();
	}
	public Word(String w, int sc) {
		super();
		this.w = w;
		this.sc = sc;
	}
	public String getW() {
		return w;
	}
	public void setW(String w) {
		this.w = w;
	}
	public int getSc() {
		return sc;
	}
	public void setSc(int sc) {
		this.sc = sc;
	}
}
