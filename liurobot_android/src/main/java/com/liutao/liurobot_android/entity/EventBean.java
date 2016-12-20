package com.liutao.liurobot_android.entity;

public class EventBean {
	private String eventName;
	private String eventId;
	private String voiceId;
	private String eventContent;
	private String eventDelay;
	public EventBean() {
		super();
	}
	
	public String getEventDelay() {
		return eventDelay;
	}

	public void setEventDelay(String eventDelay) {
		this.eventDelay = eventDelay;
	}

	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getVoiceId() {
		return voiceId;
	}
	public void setVoiceId(String voiceId) {
		this.voiceId = voiceId;
	}
	public String getEventContent() {
		return eventContent;
	}
	public void setEventContent(String ecentContent) {
		this.eventContent = ecentContent;
	}
	public EventBean(String eventName, String eventId, String voiceId, String ecentContent,String eventDelay) {
		super();
		this.eventName = eventName;
		this.eventId = eventId;
		this.voiceId = voiceId;
		this.eventContent = eventContent;
		this.eventDelay = eventDelay;
	}
	@Override
	public String toString() {
		return "EventBean [eventName=" + eventName + ", eventId=" + eventId + ", voiceId=" + voiceId + ", ecentContent="
				+ eventContent + "]";
	}
	
}
