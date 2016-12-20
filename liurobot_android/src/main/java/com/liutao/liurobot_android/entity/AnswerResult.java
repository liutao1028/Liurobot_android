/** 
 *
 */
package com.liutao.liurobot_android.entity;

import java.util.List;


public class AnswerResult {
	private int sn;
	private Boolean ls;
	private int bg;
	private int ed;
	private List<WordGroup> ws;
	public AnswerResult() {
		super();
	}
	public AnswerResult(int sn, Boolean ls, int bg, int ed, List<WordGroup> ws) {
		super();
		this.sn = sn;
		this.ls = ls;
		this.bg = bg;
		this.ed = ed;
		this.ws = ws;
	}
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public Boolean getLs() {
		return ls;
	}
	public void setLs(Boolean ls) {
		this.ls = ls;
	}
	public int getBg() {
		return bg;
	}
	public void setBg(int bg) {
		this.bg = bg;
	}
	public int getEd() {
		return ed;
	}
	public void setEd(int ed) {
		this.ed = ed;
	}
	public List<WordGroup> getWs() {
		return ws;
	}
	public void setWs(List<WordGroup> ws) {
		this.ws = ws;
	}

	
	
}
