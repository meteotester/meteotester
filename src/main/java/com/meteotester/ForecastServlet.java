package com.meteotester;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

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
import com.meteotester.util.Parser;


/**
 * Servlet implementation class ForecastServlet
 */
@WebServlet("/forecasts")
public class ForecastServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ForecastServlet.class);
	PlacesDAO placesHelper = null;
	SourcesDAO sourcesHelper = null;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ForecastServlet() {
		super();
		// TODO Auto-generated constructor stub
		placesHelper = new PlacesDAO();
		sourcesHelper = new SourcesDAO();
	}

	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		// Use "out" to send content to browser
			
		String result = "";
		Place place = null;
		String sPlace = request.getParameter("place");

		if ((sPlace != null) && (!sPlace.equals(""))) {
			place = placesHelper.getPlaceByName(sPlace);
		}
		if (place == null)
			place = placesHelper.getPlaceByName("Madrid");

		String sourceId = request.getParameter("source");
		if ((sourceId == null) || (sourceId.equals(""))) {
			// The request parameter 'param' was not present in the query string
			// e.g. http://hostname.com?a=b
			HashMap<String, Source> sources = sourcesHelper.getForecastSources();
			
			for (String key: sources.keySet()) {
				Source source = sources.get(key);
				result += Parser.parse2store(source, place);
			}
		} else {
			Source source = sourcesHelper.getForecastSourceById(sourceId);
			result += Parser.parse2store(source, place);
		}
		
		log.info(result);
		out.println(result);
	}

	

}
