package com.meteotester.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.meteotester.entities.Place;
import com.meteotester.entities.Source;
import com.meteotester.entities.WeatherCache;
import com.meteotester.entities.WeatherSummary;
import com.meteotester.util.Parser;


public class WeatherSummaryDAO {

	private static Logger log = Logger.getLogger(WeatherSummaryDAO.class);
	
	static Map<String, WeatherSummary> cache = new HashMap<String, WeatherSummary>();

	public WeatherSummaryDAO() {
		/*if (weathersummaries == null)
			weathersummaries = createPlaces();*/
	}
/*
	public WeatherSummary getWeatherSummaryFirst(HashMap<String, Source> sources, Place place) {
		WeatherSummary weathersummary = null;
		try {
			int placeId = place.getId();
			String key = placeId + " first";
			weathersummary = getWeatherSummary(key);
			if (weathersummary == null) {
				String firstSourceId = "openweather";
				Source firstSource = sources.get(firstSourceId);

				weathersummary = Parser.parse2web(firstSource, place);
				weathersummary.setTimestamp(new Date());
				cache.put(key, weathersummary);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return weathersummary;
	}*/
/*
	private WeatherSummary getWeatherSummaryAll(HashMap<String, Source> sources, Place place) {
		WeatherSummary weathersummary = null;
		try {
			int placeId = place.getId();
			String key = placeId + " all";
			weathersummary = getWeatherSummary(key);

			if (weathersummary == null) {
				// Get first source
				weathersummary = getWeatherSummaryFirst(sources, place);

				// get the other sources

				for (String sourceId: sources.keySet()) {
					Source source = sources.get(sourceId);

					weathersummary = Parser.parse2web(source, place);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return weathersummary;
	}
*/
	public static WeatherSummary getWeatherSummary(Source source, Place place) {
		String sourceId = source.getName();
		WeatherSummary weathersummary = null;
		try {
			int placeId = place.getId();
			String key = placeId + " "+sourceId;
			weathersummary = getWeatherSummary(key);

			if (weathersummary == null) {
				weathersummary = Parser.parse2web(source, place);
				setWeatherSummary(key, weathersummary);
			}
			else log.debug("Summary for " + key + " was in cache");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return weathersummary;

	}

	private static WeatherSummary getWeatherSummary(String key) {
		WeatherSummary weathersummary = cache.get(key);
		if (weathersummary != null) {
			Date now = new Date();
			if (now.getTime() - weathersummary.getTimestamp().getTime() > 1800*1000) { // > 30 min
				cache.remove(key);
				weathersummary = null;
				//ctx.setAttribute("cache", cache); ?? 
			}
		}
		return weathersummary;
	}



	private static void setWeatherSummary(String key, WeatherSummary weathersummary) {
		weathersummary.setTimestamp(new Date());
		cache.put(key, weathersummary);
	}
}


