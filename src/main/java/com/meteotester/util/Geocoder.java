package com.meteotester.util;

import org.apache.log4j.Logger;


import com.jayway.jsonpath.JsonPath;
import com.meteotester.config.Config;
import com.meteotester.entities.Place;

public class Geocoder {
	
	private static Logger log = Logger.getLogger(Geocoder.class);
	
	public Place getPlace(String query) {
		
		//String filename = "/home/milhouse/json_geocoder/geocoder.json";
		//String json = Util.getContentFromFile(filename);
		
		String sURL = "http://api.openweathermap.org/data/2.5/find?q="+query+"&cnt=0&APPID="+Config.OPENWEATHER_KEY;
		String json = Util.getContentFromURL(sURL);
		log.debug(sURL);
		
		log.debug(json);
		Place place = jsonGeocoder2Place(json); 
		
		return place;
	}
	
	private static Place jsonGeocoder2Place(String json) {
		
		Integer id = JsonPath.read(json, "list[0].id");
		String name = JsonPath.read(json, "list[0].name");
		Object latitude = JsonPath.read(json, "list[0].coord.lat");
		Object longitude = JsonPath.read(json, "list[0].coord.lon");
		String coords = latitude + "," +longitude;
		String country = JsonPath.read(json, "list[0].sys.country");
		
		if (country.equals("GB"))
			country = "UK";
		
		log.debug("Geocoded: " + id + ", "+ name + ", " + country + ", " + coords);
		Place place = new Place(id, name, country, coords);
		return place;

	}


}
