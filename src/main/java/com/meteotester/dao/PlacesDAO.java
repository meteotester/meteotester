package com.meteotester.dao;

import java.util.HashMap;

import com.meteotester.entities.Place;


public class PlacesDAO {

	private static HashMap<String, Place> places = null;

	public PlacesDAO() {
		if (places == null)
			places = createPlaces();
	}
	private HashMap<String, Place> createPlaces() {
		HashMap<String, Place> places	= new HashMap<String, Place>();

		Place place = new Place(3128832, "Madrid", "ES", "40.472,-3.561"); //Madrid Barajas ES
		//Place place = new Place(3117735, "Madrid", "ES", "40.432049,-3.697454");
		places.put(place.getName(), place);
		place = new Place(3128760, "Barcelona", "ES", "41.389,2.159");
		places.put(place.getName(), place);
		place = new Place(3128026, "Bilbao", "ES", "43.262711,-2.92528");
		places.put(place.getName(), place);
		
		place = new Place(1821275, "Macau", "MO", "22.155,113.576"); //Macau international airport Ka-ho MO
		places.put(place.getName(), place);
		
		place = new Place(4503665, "Atlantic City", "US", "39.458,-74.577"); //Atlantic City international airport US
		places.put(place.getName(), place);
		
		
		return places;
	}

	public HashMap<String, Place> getPlaces() {
		return places;
	}



	public Place getPlaceByName(String name) {
		HashMap<String, Place> places = getPlaces();
		return places.get(name);
	}

}
