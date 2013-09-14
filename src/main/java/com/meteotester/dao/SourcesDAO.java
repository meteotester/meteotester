package com.meteotester.dao;

import java.util.HashMap;

import com.meteotester.config.Config;
import com.meteotester.entities.Source;


public class SourcesDAO {

	private static HashMap<String, Source> forecastSources = null;
	private static HashMap<String, Source> observationSources = null;	

	public SourcesDAO() {
		if (forecastSources == null)
			forecastSources = createForecastSources();
		if (observationSources == null)
			observationSources = createObservationSources();
	}

	private HashMap<String, Source> createForecastSources() {

		HashMap<String, Source> sources = new HashMap<String, Source>();

		Source source = createSourceForecastio();
		sources.put(source.getName(), source);
		source = createSourceOpenweather();
		sources.put(source.getName(), source);
		source = createSourceWunderground();
		sources.put(source.getName(), source);
		source = createSourceWorldweatheronl();
		sources.put(source.getName(), source);

		return sources;
	}
	
	private HashMap<String, Source> createObservationSources() {

		HashMap<String, Source> sources = new HashMap<String, Source>();

		Source source = createSourceWundergroundObservations();
		sources.put(source.getName(), source);

		return sources;
	}
	
	private Source createSourceForecastio() {
		Source source = new Source();
		source.setName("forecastio");
		source.setType("forecasts");
		String jpLat = "$.latitude";
		String jpLong = "$.longitude";
		String jpUnixtime = "$.daily.data[1].time";
		String[] jpVariables = {"mintemp,ºC,$.daily.data[1].temperatureMin",
				"maxtemp,ºC,$.daily.data[1].temperatureMax",
				"qpf,mm (1h),$.daily.data[1].precipIntensity",
				"pop,prob.,$.daily.data[1].precipProbability",
		};

		source.setJpLat(jpLat);
		source.setJpLong(jpLong);
		source.setJpUnixtime(jpUnixtime);
		source.setJpVariables(jpVariables);
		source.setJpIcon("$.daily.data[0].icon");
		source.setUrl("https://api.forecast.io/forecast/"+Config.FORECASTIO_KEY+"/${COORDS}?units=si&exclude=minutely,hourly,alerts");
		source.setDomain("forecast.io");
		
		return source;
	}

	private Source createSourceOpenweather() {
		Source source = new Source();
		source.setName("openweather");
		source.setType("forecasts");
		String jpLat = "$.city.coord.lat";
		String jpLong = "$.city.coord.lon";
		String jpUnixtime = "$.list[1].dt";
		String[] jpVariables = {"mintemp,ºC,$.list[1].temp.min",
				"maxtemp,ºC,$.list[1].temp.max",
				"qpf,mm (3h),$.list[1].rain"
		};

		source.setJpLat(jpLat);
		source.setJpLong(jpLong);
		source.setJpUnixtime(jpUnixtime);
		source.setJpVariables(jpVariables);
		source.setJpIcon("$.list[0].weather[0].icon");
		
		source.setUrl("http://api.openweathermap.org/data/2.5/forecast/daily?id=${ID}&units=metric&cnt=8&APPID="+Config.OPENWEATHER_KEY);
		//source.setUrl("http://api.openweathermap.org/data/2.5/forecast/daily?q=${PLACE},${COUNTRY}&units=metric&cnt=8&APPID="+Config.OPENWEATHER_KEY);
		source.setDomain("openweathermap.org");
		return source;
	}

	private Source createSourceWunderground() {
		Source source = new Source();
		source.setName("wunderground");
		source.setType("forecasts");
		String[] jpVariables = {"mintemp,ºC,$.forecast.simpleforecast.forecastday[1].low.celsius",
				"maxtemp,ºC,$.forecast.simpleforecast.forecastday[1].high.celsius",
				"pop,%,$.forecast.simpleforecast.forecastday[1].pop",
				"qpf,mm (24h),$.forecast.simpleforecast.forecastday[1].qpf_allday.mm"
		};


		source.setJpUnixtime("$.forecast.simpleforecast.forecastday[1].date.epoch");
		source.setJpVariables(jpVariables);
		source.setJpIcon("$.forecast.simpleforecast.forecastday[0].icon");
		
		//source.setUrl("http://api.wunderground.com/api/"+Config.WUNDERGROUND_KEY+"/forecast10day/q/${COUNTRY}/${PLACE}.json");
		source.setUrl("http://api.wunderground.com/api/"+Config.WUNDERGROUND_KEY+"/forecast10day/q/${COORDS}.json");

		source.setDomain("wunderground.com");
		return source;
	}
	
	private Source createSourceWundergroundObservations() {
		Source source = new Source();
		source.setName("wunderground");
		source.setType("observations");
		String[] jpVariables = {"mintemp,ºC,$.history.dailysummary[0].mintempm",
				"maxtemp,ºC,$.history.dailysummary[0].maxtempm",
				"rain,prob.,$.history.dailysummary[0].rain",
				"qpf,mm (24h),$.history.dailysummary[0].precipm"
		};

		//source.setUrl("http://api.wunderground.com/api/"+Config.WUNDERGROUND_KEY+"/yesterday/q/${COUNTRY}/${PLACE}.json");
		source.setUrl("http://api.wunderground.com/api/"+Config.WUNDERGROUND_KEY+"/yesterday/q/${COORDS}.json");

		
		source.setJpYear("$.history.dailysummary[0].date.year");
		source.setJpMon("$.history.dailysummary[0].date.mon");
		source.setJpDay("$.history.dailysummary[0].date.mday");
		source.setJpVariables(jpVariables);
		source.setDomain("wunderground.com");
		return source;

	}

	private Source createSourceWorldweatheronl() {
		Source source = new Source();
		source.setName("worldweatheronl");
		source.setType("forecasts");
		String[] jpVariables = {"mintemp,ºC,$.data.weather[1].tempMinC",
				"maxtemp,ºC,$.data.weather[1].tempMaxC",
				"qpf,mm (24h),$.data.weather[1].precipMM"
		};


		source.setJpVariables(jpVariables);
		source.setJpIcon("$.data.weather[0].weatherDesc[0].value");
		
		source.setUrl("http://api.worldweatheronline.com/free/v1/weather.ashx?q=${COORDS}&format=json&num_of_days=6&cc=no&key="+Config.WORLDWEATHERONL_KEY);
		source.setDomain("worldweatheronline.com");
		return source;
	}
	
	
	public HashMap<String, Source> getForecastSources() {
		//return (HashMap<String, Source>) getServletContext().getAttribute("sources");
		return forecastSources;
	}
	public Source getForecastSourceById(String id) {
		return forecastSources.get(id);	
	}

	public HashMap<String, Source> getObservationSources() {
		return observationSources;
	}
	
	public Source getObservationSourceById(String id) {
		return observationSources.get(id);	
	}

}
