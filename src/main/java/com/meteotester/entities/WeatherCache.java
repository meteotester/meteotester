package com.meteotester.entities;

import java.util.Date;

public class WeatherCache {

	String json;
	Date timestamp;
	int placeId;
	
	public WeatherCache(String json, Date timestamp, int placeId) {
		super();
		this.json = json;
		this.timestamp = timestamp;
		this.placeId = placeId;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getPlaceId() {
		return placeId;
	}

	public void setPlaceId(int placeId) {
		this.placeId = placeId;
	}
}
