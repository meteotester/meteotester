package com.meteotester;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.meteotester.dao.PlacesDAO;
import com.meteotester.dao.SourcesDAO;
import com.meteotester.entities.Place;
import com.meteotester.entities.Source;
import com.meteotester.entities.WeatherCache;
import com.meteotester.util.Aggregator;
import com.meteotester.util.Geocoder;


/**
 * Servlet implementation class WeatherSummaryServlet
 */
@WebServlet("/weathersummary")
public class WeatherSummaryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(WeatherSummaryServlet.class);
	
	PlacesDAO placesHelper = null;
	SourcesDAO sourcesHelper = null;
	Geocoder geocoder = null;       
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WeatherSummaryServlet() {
        super();
        // TODO Auto-generated constructor stub
        placesHelper = new PlacesDAO();
        sourcesHelper = new SourcesDAO();
        geocoder = new Geocoder();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
	
		String query = new String(request.getParameter("q").getBytes("iso-8859-1"), "UTF-8");
		
		String sourceName = request.getParameter("source");

		
		Place place = placesHelper.getPlaceByName(query);
		if (place == null) {
			log.debug("Calling geocoder for query: " + query);
			place = geocoder.getPlace(query);
		}
		
		// TODO Places with the same name in different countries. Places with the same name in the same country
		
		log.info("Query: "+ query + ", place: " + place.getName());
		/*
		String result = getFromCache(request.getServletContext(), place.getId());
		
		// if the place id has been cached in the last hour return the cached result
		if (result == null) {
		// else {
		
			HashMap<String, Source> sources = sourcesHelper.getForecastSources();
		//Place place = placesHelper.getPlaceById("Madrid");
			result = Aggregator.generateWeatherSummaryMedian(sources, place);
			addToCache(request.getServletContext(), new WeatherCache(result, new Date(), place.getId()));
		}*/
		String result = null;
		Source source = sourcesHelper.getForecastSourceById(sourceName);
		if (source != null) {
			result = Aggregator.generateWeatherSummary(source, place);
		} else {
			HashMap<String, Source> sources = sourcesHelper.getForecastSources();
			result = Aggregator.generateWeatherSummaryMedian(sources, place);
		}
		log.debug(result);
		out.println(result);

	}

	
	private String getFromCache(ServletContext ctx, int placeId) {
		
		String result = null;
		WeatherCache weathercache = null;
		@SuppressWarnings("unchecked")
		Map<Integer, WeatherCache> cache = (Map<Integer, WeatherCache>) ctx.getAttribute("cache");
		if (cache != null)
			weathercache = cache.get(placeId);
		if (weathercache != null) {
			Date now = new Date();
			if (now.getTime() - weathercache.getTimestamp().getTime() > 1800*1000) { // > 30 min
				cache.remove(placeId);
				//ctx.setAttribute("cache", cache); ?? 
			}
			else {
				log.info("Weather summary was in cache");
				result = weathercache.getJson();
			}
		}
		return result;
	}
	
	private void addToCache(ServletContext ctx, WeatherCache weathercache) {
		@SuppressWarnings("unchecked")
		Map<Integer, WeatherCache> cache = (Map<Integer, WeatherCache>) ctx.getAttribute("cache");
		
		if (cache == null)
			cache = new HashMap<Integer, WeatherCache>();
		
		cache.put(weathercache.getPlaceId(), weathercache);
		ctx.setAttribute("cache", cache);	
    }
		
		
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
