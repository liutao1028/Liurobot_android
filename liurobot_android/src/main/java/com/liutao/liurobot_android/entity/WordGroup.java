/** 
 *
 */
package com.liutao.liurobot_android.entity;

import java.lang.reflect.Array;
import java.util.List;

public class WordGroup {
	private int bg;
	private List<Word> cw;
	public WordGroup() {
		super();
	}
	public WordGroup(int bg, List<Word> cw) {
		super();
		this.bg = bg;
		this.cw = cw;
	}
	public int getBg() {
		return bg;
	}
	public void setBg(int bg) {
		this.bg = bg;
	}
	public List<Word> getCw() {
		return cw;
	}
	public void setCw(List<Word> cw) {
		this.cw = cw;
	}
	
}
