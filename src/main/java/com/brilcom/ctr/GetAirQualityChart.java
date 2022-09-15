package com.brilcom.ctr;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom2.JDOMException;
import org.jepetto.bean.Facade;
import org.jepetto.proxy.HomeProxy;
import org.jepetto.sql.JsonTransfer;
import org.jepetto.sql.XmlConnection;
import org.jepetto.util.PropertyReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class GetAirQualityChart extends HttpServlet {
	private static final long serialVersionUID = 1L;

	static PropertyReader reader = PropertyReader.getInstance();
	private final String dataSource = reader.getProperty("mqtt_mysql_datasource");
	private final String QUERY_FILE = reader.getProperty("mqtt_query_file");
	private final String code = "code";
	private final String message = "result";
	private static final int a = 404;
	private static final int b = 500;

	private static final String url = "http://mqtt.brilcom.com:8080/mqtt/GetAirQualityChart";
	private static final String Content = "Content-Type";
	private static final String json = "application/json";

	private static String serialNum = "serialNum";
	private static String startTime = "startTime";
	private static String endTime = "endTime";

	public GetAirQualityChart() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("Hello World !");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject obj = new JSONObject();

		HomeProxy proxy = HomeProxy.getInstance();
		String serialNum = (String) request.getAttribute("serialNum");
		// serialNum = request.getParameter("serialNum");
		String startTime = (String) request.getAttribute("startTime");
		String endTime = (String) request.getAttribute("endTime");
		long s = Long.parseLong(startTime);
		long e = Long.parseLong(endTime);

		Facade remote = proxy.getFacade();

		HashMap<String, String> dummy = new HashMap<String, String>();
		String arr[] = new String[] { serialNum };

		org.jdom2.Document doc;
		org.jdom2.Document _doc;

		try {
			doc = remote.executeQuery(dataSource, QUERY_FILE, "findDeviceBySerialNum", dummy, arr);
			Connection con = new XmlConnection(doc);
			PreparedStatement stmt = con.prepareStatement("//recordset/row");
			ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {
				obj.put(code, a);
				obj.put(message, "Device Not Found, check your serial number !");
				out.println(obj.toJSONString());
				return;
			}
		} catch (SQLException | NamingException | JDOMException | IOException e1) {
			e1.printStackTrace();
		}

		long limit = 1 * 1000 * 60 * 60 * 24; // 1 hour 1 * 1000 * 60 * 60 * 24;
		if (e - s > limit) {
			obj.put(code, b);
			obj.put(message, "Query condition time period 24 Hour within");
			out.println(obj.toJSONString());
			return;
		}

		arr = new String[] { serialNum, startTime, endTime };

		try {
			doc = remote.executeQuery(dataSource, QUERY_FILE, "queryBySelectDateChart", dummy, arr);
			_doc = doc.clone();
			out.println(new JsonTransfer().transferDom2JSON(doc).toString());
			Connection con = new XmlConnection(_doc);
			PreparedStatement stmt = con.prepareStatement("//recordset/row");
			ResultSet rs = stmt.executeQuery();

			JSONArray list = new JSONArray();

			while (rs.next()) {
				if ("AC67B25CCCD6".equals(rs.getString("serialNum"))
						|| "246F283C7E16".equals(rs.getString("serialNum"))) {
					// out.println(new JsonTransfer().transferDom2JSON(_doc).toString());
					System.out.println(new JsonTransfer().transferDom2JSON(_doc).toString());
				}
			}

		} catch (SQLException | NamingException | JDOMException | IOException err) {
			err.printStackTrace();
			obj.put(code, b);
			obj.put(message, err.getMessage());
			out.println(obj.toJSONString());
		}
/*		
		serialNum = "AC67B25CC502";
		
		JSONObject obj = new JSONObject();
		obj.put(serialNum, serialNum);
		obj.put(startTime, startTime);
		obj.put(endTime, endTime);
	  //obj.put("type","Tvoc,Temperature,Pm25,Humid,Co2,Pm10");
		
		System.out.println(obj.toJSONString());
 * 
		try {
			Unirest.setTimeouts(0, 0);
			HttpResponse<String> resp = Unirest.post(url)
					.header(Content, json).body(obj.toJSONString()).asString();
			System.out.println("time : " + startTime + '\n&' + resp.getBody());
		} catch (Exception se) {
			se.printStackTrace();
		}
*/
	}

}
