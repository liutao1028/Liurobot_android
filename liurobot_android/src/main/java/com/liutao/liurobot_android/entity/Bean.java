package com.liutao.liurobot_android.entity;


public class Bean {
	String mapId;
	String mapName;
	String mapNote;
	String mapUrl;
	public Bean() {
		super();
	}
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	public String getMapName() {
		return mapName;
	}
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	public String getMapNote() {
		return mapNote;
	}
	public void setMapNote(String mapNote) {
		this.mapNote = mapNote;
	}
	public String getMapUrl() {
		return mapUrl;
	}
	public void setMapUrl(String mapUrl) {
		this.mapUrl = mapUrl;
	}
	@Override
	public String toString() {
		return "Bean [mapId=" + mapId + ", mapName=" + mapName + ", mapNote=" + mapNote + ", mapUrl=" + mapUrl + "]";
	}
	
	
	

}
