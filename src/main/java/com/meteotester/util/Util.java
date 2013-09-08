package com.meteotester.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.meteotester.entities.Place;
import com.meteotester.entities.Source;


public class Util {

	private static Logger log = Logger.getLogger(Util.class);

	public static boolean isSameDay(Date date1, Date date2) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		return fmt.format(date1).equals(fmt.format(date2));
	}
	
	public static double median(double[] l) {
		Arrays.sort(l);
		int middle = l.length / 2;
		if (l.length % 2 == 0)
		{
			double left = l[middle - 1];
			double right = l[middle];
			return (left + right) / 2;
		}
		else
		{
			return l[middle];
		}
	}

	

	
	public static long epoch2unixtime(Object epoch) {	
		Long unixtime = null;
		if (epoch instanceof String)
			unixtime = Long.valueOf((String) epoch);
		if (epoch instanceof Integer)
			unixtime = Long.valueOf((Integer) epoch);
		return unixtime;
	}
	
	

	public static boolean isProcessed(String type, Source source, Place place) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String sDate = sdf.format(date);
		String year = sDate.substring(0, 4);
		String month = sDate.substring(4,6);
		String path = type + "/" + place.getName() + "/" + year + "/" + month + "/";
		String extension = (type.contains("json"))?".json":".csv";

		String filename = path + type + "_" + source.getName() + "_" + sDate + extension;
		File f = new File(filename);
		return f.exists();
	}

	public static File saveToFile(String tsv, String type, Source source, Place place) {

		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String sDate = sdf.format(date);
		String year = sDate.substring(0, 4);
		String month = sDate.substring(4,6);
		String path = type + "/" + place.getName() + "/" + year + "/" + month + "/";

		File f=new File(path);
		if (!f.isDirectory()) {
			f.mkdirs();
		}

		// path = forecasts|observations|scores /place / year / month / source
		String extension = (type.contains("json"))?".json":".csv";

		String filename = path + type + "_" + source.getName() + "_" + sDate + extension;
		f = new File(filename);
		if (!f.exists()) {
			log.info("Saving file "+filename);

			FileWriter fw = null;
			try {
				fw = new FileWriter(filename);

				PrintWriter pw = new PrintWriter(fw);

				pw.print(tsv);

				//Flush the output to the file
				pw.flush();

				//Close the Print Writer
				pw.close();

				//Close the File Writer
				fw.close(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		f.deleteOnExit();
		return f;
	}

	public static String getContentFromURL(String sURL) {
		String content = "ERROR";

		URL url = null;
		try {
			url = new URL(sURL);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			Scanner s;
			if (connection.getResponseCode() != 200) {
				s = new Scanner(connection.getErrorStream());
			} else {
				s = new Scanner(connection.getInputStream());
			}
			s.useDelimiter("\\Z");
			content = s.next();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return content;
	}

	public static String getContentFromMockFile(Source source) {
		String filename = source.getType() + "_json/Madrid/2013/08/"+source.getType() + "_json_"+
				source.getName() + "_20130815.json";
		log.info(filename);
		return getContentFromFile(filename);
	}

	public static String getContentFromFile(String filename) {
		String content = "ERROR";
		try {
			content = new Scanner( new File(filename) ).useDelimiter("\\A").next();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}

}
