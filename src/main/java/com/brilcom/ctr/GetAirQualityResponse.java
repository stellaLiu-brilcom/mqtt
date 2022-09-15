package com.brilcom.ctr;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom2.JDOMException;
import org.json.simple.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class GetAirQualityResponse {

	private static final String url = "http://mqtt.brilcom.com:8080/mqtt/GetAirQuality";
	private static final String Content = "Content-Type";
	private static final String json = "application/json";

	private static String serialNum = "serialNum";
	private static String startTime = "startTime";
	private static String endTime = "endTime";

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			doPost(req, res);
		} catch (IOException | ServletException e) {

			e.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm00");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		

		Calendar cal = Calendar.getInstance();
		System.out.println(sdf.format(cal.getTime()));
		
		cal.add(Calendar.MINUTE, -3);
		String startDate = sdf.format(cal.getTime());
		System.out.println(startDate);
		
		cal.add(Calendar.MINUTE, +1);
		String endDate = sdf.format(cal.getTime());
		System.out.println(endDate);

		String Num = "246F283C7CF6";
		//String Num2 = "AC67B25CC502";
		
		JSONObject obj = new JSONObject();

		obj.put(serialNum, Num);
		obj.put(startTime, startDate);
		obj.put(endTime, endDate);
		obj.put("type","Tvoc,Temperature,Pm25,Humid,Co2,Pm10");
		
		System.out.println(obj.toJSONString());
		
		try {
			Unirest.setTimeouts(0, 0);
			HttpResponse<String> res = Unirest.post(url)
			  .header(Content, json)
			  .body(obj.toJSONString())
			  .asString();
			System.out.println(res.getBody());
			if(res.getBody().contains("success")) {
				System.out.println(new java.util.Date());
				System.out.println(res.getBody());
				
			}else{
				System.out.println("error");
				System.out.println(new java.util.Date());
			}	
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm00");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		

		Calendar cal = Calendar.getInstance();
		System.out.println(sdf.format(cal.getTime()));
		
		cal.add(Calendar.MINUTE, -3);
		String startDate = sdf.format(cal.getTime());
		System.out.println(startDate);
		
		cal.add(Calendar.MINUTE, +1);
		String endDate = sdf.format(cal.getTime());
		System.out.println(endDate);

		//String Num = "AC67B25CC502";
		String Num = "246F283C7CF6";
		
		JSONObject obj = new JSONObject();

		obj.put(serialNum, Num);
		obj.put(startTime, startDate);
		obj.put(endTime, endDate);
		obj.put("type","Tvoc,Temperature,Pm25,Humid,Co2,Pm10");
		
		System.out.println(obj.toJSONString());
		
		try {
			Unirest.setTimeouts(0, 0);
			HttpResponse<String> response = Unirest.post(url)
			  .header(Content, json)
			  .body(obj.toJSONString())
			  .asString();

			if(response.getBody().contains("success")) {
				System.out.println("success");
				System.out.println(new java.util.Date());
				System.out.println(response.getBody());
				
			}else{
				System.out.println("error");
				System.out.println(new java.util.Date());
			}	
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
