package com.meteotester.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.apache.log4j.Logger;

import com.meteotester.config.Config;
import com.meteotester.dao.WeatherSummaryDAO;
import com.meteotester.entities.Place;
import com.meteotester.entities.Source;
import com.meteotester.entities.WeatherSummary;

public class Aggregator {

	private static Logger log = Logger.getLogger(Aggregator.class);
	static int N = Config.NUM_DAYS_WEB;
	
	public static String generateWeatherSummary(Source source, Place place) {
		WeatherSummary summary = WeatherSummaryDAO.getWeatherSummary(source, place);
		List<String> sourceDomains = new ArrayList<String>();
		sourceDomains.add(summary.getSourceDomain());
		List<String> icons = new ArrayList<String>();
		icons.add(summary.getIcon()[0]);
		
		JSONObject obj = new JSONObject();
		obj.put("days", getListOfNDays(summary.getToday(), N, place));
		obj.put("mintemp", summary.getMintemp());
		obj.put("maxtemp", summary.getMaxtemp());
		obj.put("qpf", summary.getQpf());
		obj.put("icon", icons);
		obj.put("sources", sourceDomains);
		obj.put("place", place.getName() + " ("+place.getCountry()+")");
		String json = obj.toString();

		return json;
	}
	
	/* Return a JSON string */
	public static String generateWeatherSummaryMedian(HashMap<String, Source> sources, Place place) {

		long initTime = new Date().getTime();
		List<WeatherSummary> summaries = new ArrayList<WeatherSummary>();
		List<String> sourceDomains = new ArrayList<String>();
		List<String> icons = new ArrayList<String>();
		Date today = null;

		for (String key: sources.keySet()) {
			Source source = sources.get(key);
			try {
				WeatherSummary summary = WeatherSummaryDAO.getWeatherSummary(source, place);
				//WeatherSummary summary = Parser.parse2web(source, place);
				if (today == null)
					today = summary.getToday();
				if (Util.isSameDay(today, summary.getToday())) {
					summaries.add(summary); // If dates are not synchronized we ignore the last one.
					sourceDomains.add(summary.getSourceDomain());
					String icon = iconTranslator(summary.getIcon()[0]);
					if (icon != null)
						icons.add(icon);
				}
				else {
					log.debug("Date in last source is "+today + ", date in "+source.getName() + " is "+ summary.getToday());
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}

		List<Double> mintempMedian = new ArrayList<Double>();
		List<ArrayList<Double>> mintempError = new ArrayList<ArrayList<Double>>();

		List<Double> maxtempMedian = new ArrayList<Double>();
		List<ArrayList<Double>> maxtempError = new ArrayList<ArrayList<Double>>();

		List<Double> qpfMedian = new ArrayList<Double>();
		List<ArrayList<Double>> qpfError = new ArrayList<ArrayList<Double>>();

		for (int j=0; j<N; j++) {
			double[] mintempSummary = new double[summaries.size()];
			double[] maxtempSummary = new double[summaries.size()];
			double[] qpfSummary = new double[summaries.size()];

			for (int i=0; i < summaries.size(); i++) {
				mintempSummary[i] = summaries.get(i).getMintemp()[j];
				maxtempSummary[i] = summaries.get(i).getMaxtemp()[j];
				qpfSummary[i] = summaries.get(i).getQpf()[j];
			}

			mintempMedian.add(Util.median(mintempSummary));
			ArrayList<Double> range = new ArrayList<Double>();
			range.add(mintempSummary[0]);
			range.add(mintempSummary[summaries.size()-1]);
			mintempError.add(range);

			maxtempMedian.add(Util.median(maxtempSummary));
			range = new ArrayList<Double>();
			range.add(maxtempSummary[0]);
			range.add(maxtempSummary[summaries.size()-1]);
			maxtempError.add(range);

			qpfMedian.add(Util.median(qpfSummary));
			range = new ArrayList<Double>();
			range.add(qpfSummary[0]);
			range.add(qpfSummary[summaries.size()-1]);
			qpfError.add(range);
		}

		// Json Object is an HashMap<String, Object> extends
		JSONObject obj = new JSONObject();
		obj.put("days", getListOfNDays(today, N, place));
		obj.put("mintemp", mintempMedian);
		obj.put("mintemp_error", mintempError);
		obj.put("maxtemp", maxtempMedian);
		obj.put("maxtemp_error", maxtempError);
		obj.put("qpf", qpfMedian);
		obj.put("qpf_error", qpfError);
		obj.put("icon", icons);
		obj.put("sources", sourceDomains);
		obj.put("place", place.getName() + " ("+place.getCountry()+")");
		String json = obj.toString();

		long endTime = new Date().getTime();
		log.debug(endTime-initTime +  " miliseconds");
		return json;
	}

	private static List<String> getListOfNDays(Date today, int N, Place place) {
		Date utcDate = new Date();

		if (!Util.isSameDay(today, utcDate)) {
			log.debug("UTC date is "+utcDate + ", data date is "+today + ", country is "+ place.getCountry());
		}

		SimpleDateFormat fmt = new SimpleDateFormat("EEE");
		ArrayList<String> days = new ArrayList<String>();
		days.add("Today");
		for (int i=1; i<N; i++) {
			Date nextdate = new Date(today.getTime() + 24*i*3600*1000);
			days.add(fmt.format(nextdate));
		}

		return days;
	}

	private static String iconTranslator(String icon) {

		Map<String, String> translator = new HashMap<String, String>();
		translator.put("01d", "01d");
		translator.put("clear", "01d");
		translator.put("sunny", "01d");
		translator.put("clear-day", "01d");
		translator.put("Sunny", "01d");

		translator.put("02d", "02d");
		translator.put("partlycloudy", "02d");
		translator.put("partlysunny", "02d");
		translator.put("mostlysunny", "02d");
		translator.put("partly-cloudy-day", "02d");
		translator.put("Partly Cloudy", "02d");
		

		translator.put("03d", "03d");
		translator.put("cloudy", "03d");
		translator.put("mostlycloudy", "03d");
		translator.put("Cloudy", "03d");

		translator.put("04d", "04d"); // broken clouds 5/8 to 7/8 clouds, or overcast 8/8
		translator.put("Overcast", "04d");
		
		translator.put("09d", "09d"); // Shower rain
		translator.put("rain", "09d");
		translator.put("rain", "09d");
		

		translator.put("10d", "10d"); // rain
		translator.put("Moderate rain", "10d");

		translator.put("11d", "11d"); // thunderstorm
		translator.put("tstorms", "11d");
		translator.put("chancetstorms", "11d");
		translator.put("thunderstorm", "11d");

		translator.put("13d", "13d"); // snow
		translator.put("snow", "13d");
		translator.put("chancesnow", "13d");

		translator.put("50d", "50d"); // mist
		translator.put("fog", "50d");
		translator.put("hazy", "50d");

		return translator.get(icon);
	}

}
