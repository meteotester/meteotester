package com.meteotester.entities;

public class Place {
	
	
	int id; // Geonames identifier
	String name;
	String country;
	String coords; //lat,long
	
	public Place(int id, String name, String country, String coords) {
		this.id = id;
		this.name = name;
		this.country = country;
		this.coords = coords;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCoords() {
		return coords;
	}
	public void setCoords(String coords) {
		this.coords = coords;
	}
	
}
