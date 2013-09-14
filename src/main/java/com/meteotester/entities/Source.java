package com.meteotester.entities;

public class Source {

	String name;
	String type;
	String url;
	//String json;
	String jpLat;
	String jpLong;
	String jpUnixtime;
	String jpYear;
	String jpMon;
	String jpDay;
	String[] jpVariables;
	String jpIcon;
	
	
	String domain;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	/*
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}*/
	public String getJpLat() {
		return jpLat;
	}
	public void setJpLat(String jpLat) {
		this.jpLat = jpLat;
	}
	public String getJpLong() {
		return jpLong;
	}
	public void setJpLong(String jpLong) {
		this.jpLong = jpLong;
	}
	public String getJpUnixtime() {
		return jpUnixtime;
	}
	public void setJpUnixtime(String jpUnixtime) {
		this.jpUnixtime = jpUnixtime;
	}
	public String getJpYear() {
		return jpYear;
	}
	public void setJpYear(String jpYear) {
		this.jpYear = jpYear;
	}
	public String getJpMon() {
		return jpMon;
	}
	public void setJpMon(String jpMon) {
		this.jpMon = jpMon;
	}
	public String getJpDay() {
		return jpDay;
	}
	public void setJpDay(String jpDay) {
		this.jpDay = jpDay;
	}
	public String[] getJpVariables() {
		return jpVariables;
	}
	public void setJpVariables(String[] jpVariables) {
		this.jpVariables = jpVariables;
	}
	
	public String getJpIcon() {
		return jpIcon;
	}
	public void setJpIcon(String jpIcon) {
		this.jpIcon = jpIcon;
	}
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
}
