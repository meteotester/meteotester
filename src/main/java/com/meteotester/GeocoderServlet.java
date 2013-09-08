package com.meteotester;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.meteotester.config.Config;
import com.meteotester.util.Util;


/**
 * Servlet implementation class GeocoderServlet
 */
@WebServlet("/geocoder")
public class GeocoderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(GeocoderServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GeocoderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		String lat = request.getParameter("lat");
		String lon = request.getParameter("lon");
		
		String sURL = "http://api.geonames.org/findNearbyJSON?formatted=true&lat="+lat+"&lng="+lon+"&username="+Config.GEONAMES_ID;
		log.debug(sURL);
		String result = Util.getContentFromURL(sURL);
		out.println(result);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
