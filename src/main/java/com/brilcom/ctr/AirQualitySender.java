package com.brilcom.ctr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import org.jepetto.sql.XmlConnection;
import org.jepetto.util.PropertyReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AirQualitySender extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final static PropertyReader reader = PropertyReader.getInstance();
	private final String dataSource = reader.getProperty("mqtt_mysql_datasource");
	private final String QUERY_FILE = reader.getProperty("mqtt_query_file");
	private static final String sendAirqualityByHour = "sendAirqualityByHour";
	private static final String sendAirqualityByDay = "sendAirqualityByDay";
	private final String writeApiKey = "writeApiKey";
	private final String dataType= "dataType";
	private final String D = "D";
	private final String dataRegTime = "dataRegTime";
	private final String dataPm1 = "dataPm1";
	private final String dataPm25 = "dataPm25";
	private final String dataPm10 = "dataPm10";
	private final String dataTemp = "dataTemp";
	private final String dataHumi = "dataHumi";
	private final String dataPress = "dataPress";
	private final String dataCo = "dataCo";
	private final String dataCo2 = "dataCo2";
	private final String dataTvoc = "dataTvoc";
	private final String dataFirmwareVersion = "dataFirmwareVersion";
	private final int i = -1;
	
	private final String ip = "ip";
	private final String latitude = "latitude";
	private final String longitude = "longitude";
	private final String dataDeviceLat = "dataDeviceLat";
	private final String dataDeviceLng = "dataDeviceLng";
	
	private static final String url = "https://api.mise.today/rest/v1/data/datas/picoHome";
	

	public AirQualitySender() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			doIt(request, response);
		} catch (IOException | SQLException | NamingException | JDOMException e) {

			e.printStackTrace();
		}
	}

	
	private String getCondition(String cmd) {
		String condition = null;
		String hourdate = null;
		String daydate = null;
		Calendar cal = Calendar.getInstance();

		if (cmd.equalsIgnoreCase("hour")) {
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMddHH");
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
			condition = dateFormatGmt.format(new Date()) + "%";

			cal.add(Calendar.HOUR, -1);
			hourdate = dateFormatGmt.format(cal.getTime());
			System.out.println("hourdate : " + hourdate);

		} else {
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMdd");
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
			condition = dateFormatGmt.format(new Date()) + "%";

			cal.add(Calendar.DATE, -1);
			daydate = dateFormatGmt.format(cal.getTime());
			System.out.println("daydate : " + daydate);
		}
		return condition;
	}	

	private void doIt(HttpServletRequest req, HttpServletResponse res) throws IOException, SQLException, NamingException, JDOMException {
		PrintWriter out = res.getWriter();
		JSONParser parser = new JSONParser();

		HomeProxy proxy = HomeProxy.getInstance();
		Facade remote = proxy.getFacade();
		
		HashMap<String, String> dummy = new HashMap<String, String>();
		String cmd = (String) req.getAttribute("cmd");
		String arr[] = new String[] { getCondition(cmd)	};
		//System.out.println("dfsdkfjlsdkj");
		org.jdom2.Document doc = null;
		if (cmd.equalsIgnoreCase("hour")) {
			doc = remote.executeQuery(dataSource, QUERY_FILE, sendAirqualityByHour, dummy, arr);
		} else {
			doc = remote.executeQuery(dataSource, QUERY_FILE, sendAirqualityByDay, dummy, arr);
		}

		Connection con = new XmlConnection(doc);
		PreparedStatement stmt = con.prepareStatement("//recordset/row");
		ResultSet rs = stmt.executeQuery();
		int index = 0;
		JSONObject obj = null;
		JSONObject temp = null;
		RequestBody body = null;
		MediaType mediaType = MediaType.parse("application/json");
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		
		Request request = null;
		Response response = null;
		
		while (rs.next()) {
			obj = new JSONObject();
			obj.put(writeApiKey, rs.getString(writeApiKey));
			obj.put(dataType, "D");
			obj.put(dataRegTime, rs.getString(dataRegTime));
			obj.put(dataType, rs.getString(dataType));
			obj.put(dataPm1, i);
			obj.put(dataPm25, rs.getString(dataPm25));
			obj.put(dataPm10, rs.getString(dataPm10));
			obj.put(dataTemp, rs.getString(dataTemp));
			obj.put(dataHumi, rs.getString(dataHumi));
			obj.put(dataPress, rs.getString(dataPress));
			obj.put(dataCo, i);
			obj.put(dataCo2, rs.getString(dataCo2));
			obj.put(dataTvoc, rs.getString(dataTvoc));
			obj.put(dataFirmwareVersion, i);
			try {
				temp = get(rs.getString(ip));
			}catch(IOException e) {
				e.printStackTrace();
				continue;
			}catch(ParseException e) {
				e.printStackTrace();
				continue;
			}
			Double lat = (Double) temp.get(latitude);
			Double lng = (Double) temp.get(longitude);

			obj.put(dataDeviceLat, lat == null ? 0 : lat);
			obj.put(dataDeviceLng, lng == null ? 0 : lng);
			
			System.out.println(obj.toJSONString());
			body = RequestBody.create(obj.toJSONString(),mediaType);
			
			request = new Request.Builder().url(url).method("POST", body).addHeader("Content-Type", "application/json").build();
			try {
				response = client.newCall(request).execute();
				
				System.out.println(response.toString());
			} catch (IOException e1) {
				e1.printStackTrace();
			}finally {
				response.close();	
			}
			
			
			
			
		}// end of while
		
		

	}
	
	public static JSONObject get(String ip) throws IOException , ParseException {
		URL ipurl = null;
		URLConnection conn = null;
		BufferedReader reader = null;
		JSONObject obj = null;		
		try {
			
			ipurl = new URL("https://ipapi.co/" + ip + "/json/");
			conn = ipurl.openConnection();
			conn.setRequestProperty("User-Agent", "java-ipapi-v1.01");
			
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
	
			JSONParser parser = new JSONParser();
			obj = (JSONObject) parser.parse(buffer.toString());
			
		}catch(IOException | ParseException e) {
			throw e;
		}finally {
			try {
				reader.close();
			}catch(Exception e) {
				
			}
			
		}
		return obj;
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			doIt(request, response);
		} catch (IOException | SQLException | NamingException | JDOMException e) {
			e.printStackTrace();
		}
	}

}
