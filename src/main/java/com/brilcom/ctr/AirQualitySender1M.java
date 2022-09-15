package com.brilcom.ctr;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom2.JDOMException;
import org.jepetto.bean.Facade;
import org.jepetto.proxy.HomeProxy;
import org.jepetto.util.PropertyReader;
import org.json.simple.JSONObject;

public class AirQualitySender1M extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final static PropertyReader reader = PropertyReader.getInstance();
	private final String dataSource = reader.getProperty("mqtt_mysql_datasource");
	private final String QUERY_FILE = reader.getProperty("mqtt_query_file");
	private static final String recordAirqualityByDay = "recordAirqualityByDay";

	
	Calendar cal = Calendar.getInstance();
	String condition = null;
	String startTime = null;
	String endTime = null;
       

    public AirQualitySender1M() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("airQualitySender1M!");

	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json;charset=utf-8");

		PrintWriter out = response.getWriter();
		JSONObject obj = new JSONObject();
		
		HomeProxy proxy = HomeProxy.getInstance();
		Facade remote = proxy.getFacade();
		
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMddHH");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		condition = dateFormatGmt.format(new Date()) + "%";
		endTime = condition;
		System.out.println("endDate : " + endTime);

		cal.add(Calendar.HOUR, -1);
		String condition2 = dateFormatGmt.format(cal.getTime());
		startTime = condition2 + "%";
		System.out.println("startDate : " + startTime);
		
		String arr[] = new String [] {startTime, endTime};
		HashMap<String, String> dummy = new HashMap<String, String>();
		
		try {
			int updatedCnt = remote.executeUpdate(dataSource, QUERY_FILE, recordAirqualityByDay, dummy, arr);
			
			SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
			Date time = new Date();
			
			String time1 = format1.format(time);
			
			if (updatedCnt > 0) {
				System.out.println("Registed Time : " + time1);
			} else {
				System.out.println("Registed Time : " + time1);
			}
		} catch (SQLException | NamingException | JDOMException | IOException e) {
			System.out.println(e.getMessage());
		}
		
		
	}

}
