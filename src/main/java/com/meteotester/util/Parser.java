package com.meteotester.util;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;


import com.jayway.jsonpath.JsonPath;
import com.meteotester.config.Config;
import com.meteotester.entities.Forecast;
import com.meteotester.entities.Observation;
import com.meteotester.entities.Place;
import com.meteotester.entities.Source;
import com.meteotester.entities.WeatherSummary;

public class Parser {
	
	private static Logger log = Logger.getLogger(Parser.class);

	static int N = Config.NUM_DAYS_WEB;
	static int NS = Config.NUM_DAYS_STORE;
	
	public static WeatherSummary parse2web(Source source, Place place) throws Exception {
	
		String sURL = source.getUrl();
		sURL = sURL.replaceFirst(Pattern.quote("${COORDS}"), place.getCoords());
		sURL = sURL.replaceFirst(Pattern.quote("${PLACE}"), place.getName());
		sURL = sURL.replaceFirst(Pattern.quote("${COUNTRY}"), place.getCountry());
		sURL = sURL.replaceFirst(Pattern.quote("${ID}"), String.valueOf(place.getId()));
		log.debug(sURL);

		//String json = Util.getContentFromMockFile(source);

		String json = Util.getContentFromURL(sURL);

		WeatherSummary summary = jsonForecasts2summary(json, source, place);

		return summary;
	}

	public static String parse2store(Source source, Place place) {
		String type = source.getType();
		String result = source.getName() + " "+type+ " "+ place.getName()+" [OK]\n";

		if (!Util.isProcessed(type, source, place)) {

			String sURL = source.getUrl();
			sURL = sURL.replaceFirst(Pattern.quote("${COORDS}"), place.getCoords());
			sURL = sURL.replaceFirst(Pattern.quote("${PLACE}"), place.getName());
			sURL = sURL.replaceFirst(Pattern.quote("${COUNTRY}"), place.getCountry());
			sURL = sURL.replaceFirst(Pattern.quote("${ID}"), String.valueOf(place.getId()));
			log.debug(sURL);

			//String json = Util.getContentFromMockFile(source);

			String json = Util.getContentFromURL(sURL);

			File file = Util.saveToFile(json, type+"_json", source, place);
			S3Util.saveFileToS3(file);

			String tsv = "";
			if (type.equals("forecasts"))
				tsv = jsonForecasts2tsv(json, source, place);
			else tsv = jsonObservations2tsv(json, source, place);
			file = Util.saveToFile(tsv, type, source, place);
			S3Util.saveFileToS3(file);
		}

		return result;
	}

	private static WeatherSummary jsonForecasts2summary(String json, Source source, Place place) {

		String[] icon = new String[N];
		
		double[] mintemp = new double[N];
		double[] maxtemp = new double[N];
		double[] qpf = new double[N];
		double[] values = mintemp;
		Object value = 0;
		
		String jpUnixtime = source.getJpUnixtime();
		String[] jpVariables = source.getJpVariables();

		Object epoch = JsonPath.read(json, jpUnixtime.replace("[1]", "[0]"));
		long unixtime = Util.epoch2unixtime(epoch);
		Date today=new Date(unixtime*1000);
		
		if (source.getName().equals("wunderground")) { //epoch in wunderground is UTC, not local
			Integer day = JsonPath.read(json, "$.forecast.simpleforecast.forecastday[0].date.day");
			Integer month = JsonPath.read(json, "$.forecast.simpleforecast.forecastday[0].date.month");
			Integer year = JsonPath.read(json, "$.forecast.simpleforecast.forecastday[0].date.year");

			String strDate=String.format("%02d", day) + "-"+ String.format("%02d", month) + "-" + year;
			DateFormat df  = new SimpleDateFormat("dd-MM-yyyy");
			try {
				today = df.parse(strDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (source.getName().equals("forecastio")) { //unixtime in forecastio is UTC, not local
			Integer offset = JsonPath.read(json,"$.offset");
			unixtime+=offset*3600;
			today = new Date(unixtime*1000);
		}
		
		for (int i=0; i<N; i++) {
			String iconpath = "$.list["+i+"].weather[0].icon";
			if (source.getName().equals("forecastio"))
				iconpath = "$.daily.data["+i+"].icon";
			else if (source.getName().equals("wunderground"))
				iconpath = "$.forecast.simpleforecast.forecastday["+i+"].icon";
			icon[i] = JsonPath.read(json,iconpath);
			
			for (int j=0; j<jpVariables.length; j++) {
				String[] variable = jpVariables[j].split(",");

				if (variable[0].equals("qpf") || variable[0].equals("mintemp") || variable[0].equals("maxtemp")) {
					if (variable[0].equals("qpf")) {
						values = qpf;
					} else if (variable[0].equals("mintemp")) {
						values = mintemp;
					} else if (variable[0].equals("maxtemp")) {
						values = maxtemp;
					}

					variable[2] = variable[2].replace("[1]", "["+i+"]");
					try {
						value = JsonPath.read(json,variable[2]);
					}
					catch (Exception e) { 
						// this variable is optional in json like qpf in openweather
						//e.printStackTrace();
						value = 0;
					}

					values[i] = Double.valueOf(value.toString());
					if (variable[0].equals("qpf") && source.getName().equals("forecastio"))
						values[i]*=24;
					
				}
			}

		}

		WeatherSummary summary = new WeatherSummary(mintemp, maxtemp, qpf);
		summary.setToday(today);
		summary.setSourceName(source.getDomain());
		summary.setIcon(icon);

		return summary;		

	}
	
	
	private static String jsonForecasts2tsv(String json, Source source, Place place) {
		String result = "";

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");

		String name = source.getName();
		//String json = source.getJson();
		String jpLat = source.getJpLat();
		String jpLong = source.getJpLong();
		String jpUnixtime = source.getJpUnixtime();
		String[] jpVariables = source.getJpVariables();

		Object latitude = (jpLat == null)?null:JsonPath.read(json, jpLat);
		Object longitude = (jpLong == null)?null:JsonPath.read(json, jpLong);
		Forecast forecast = new Forecast(name, latitude, longitude,place.getName(), place.getCountry());

		for (int i=1; i<=NS; i++) {
			Object epoch = JsonPath.read(json, jpUnixtime.replace("[1]", "["+i+"]"));
			long unixtime = Util.epoch2unixtime(epoch);
			Date date=new Date(unixtime*1000);
		
			if (source.getName().equals("wunderground")) { //epoch in wunderground is UTC, not local
				Integer day = JsonPath.read(json, "$.forecast.simpleforecast.forecastday["+i+"].date.day");
				Integer month = JsonPath.read(json, "$.forecast.simpleforecast.forecastday["+i+"].date.month");
				Integer year = JsonPath.read(json, "$.forecast.simpleforecast.forecastday["+i+"].date.year");

				String strDate=String.format("%02d", day) + "-"+ String.format("%02d", month) + "-" + year;
				DateFormat df  = new SimpleDateFormat("dd-MM-yyyy");
				try {
					date = df.parse(strDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (source.getName().equals("forecastio")) { //unixtime in forecastio is UTC, not local
				Integer offset = JsonPath.read(json,"$.offset");
				unixtime+=offset*3600;
				date = new Date(unixtime*1000);
			}
			
			
			forecast.setTargetdate(sdf.format(date));
			forecast.setDaysbefore(i);

			for (int j=0; j<jpVariables.length; j++) {
				String[] variable = jpVariables[j].split(",");
				forecast.setVariable(variable[0]);
				forecast.setUnits(variable[1]);

				variable[2] = variable[2].replace("[1]", "["+i+"]");
				try {
					forecast.setValue(JsonPath.read(json,variable[2]));
				}
				catch (Exception e) { // this variable is optional in json
					forecast.setValue(0);
				}

				result += forecast;
			}

		}

		//result += forecast;
		return result;		

	}

	private static String jsonObservations2tsv(String json, Source source, Place place) {
		String result = "";

		String name = source.getName();
		//String json = source.getJson();
		String jpLat = source.getJpLat();
		String jpLong = source.getJpLong();

		String[] jpVariables = source.getJpVariables();

		Object latitude = (jpLat == null)?null:JsonPath.read(json, jpLat);
		Object longitude = (jpLong == null)?null:JsonPath.read(json, jpLong);
		Observation observation = new Observation(name, latitude, longitude, place.getName(), place.getCountry());

		String year = JsonPath.read(json, source.getJpYear());
		String month = JsonPath.read(json, source.getJpMon());
		String day = JsonPath.read(json, source.getJpDay());
		observation.setDate(year+"/"+month+"/"+day);

		for (int j=0; j<jpVariables.length; j++) {
			String[] variable = jpVariables[j].split(",");
			observation.setVariable(variable[0]);
			observation.setUnits(variable[1]);
			observation.setValue(JsonPath.read(json,variable[2]));
			result += observation;
		}

		//result += forecast;
		return result;		

	}
	
}
