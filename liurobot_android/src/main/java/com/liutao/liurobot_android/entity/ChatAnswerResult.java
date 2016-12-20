/** 
 *
 */
package com.liutao.liurobot_android.entity;

public class ChatAnswerResult {
	private String text;
	private ChatResponse answer;
	
	public ChatAnswerResult() {
		super();
	}

	public ChatAnswerResult(String text, ChatResponse answer) {
		super();
		this.text = text;
		this.answer = answer;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ChatResponse getAnswer() {
		return answer;
	}

	public void setAnswer(ChatResponse answer) {
		this.answer = answer;
	}
	
}
