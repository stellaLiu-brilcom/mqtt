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
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jni.Time;
import org.jdom2.JDOMException;
import org.jepetto.bean.Facade;
import org.jepetto.proxy.HomeProxy;
import org.jepetto.sql.XmlConnection;
import org.jepetto.util.PropertyReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.brilcom.ctr.DBConn;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AirQualitySender1M2 extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final static PropertyReader reader = PropertyReader.getInstance();
	private final String dataSource = reader.getProperty("mqtt_mysql_datasource");
	private final String QUERY_FILE = reader.getProperty("mqtt_query_file");
	private static final String recordAirqualityByDay = "recordAirqualityByDay";

	private final String dataType = "dataType";
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

	private final String code = "code";
	private final String message = "message";

	private static final int a = 404;
	private static final int b = 200;

	public AirQualitySender1M2() {
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			doIt(request, response);
		} catch (IOException | SQLException | NamingException | JDOMException e) {

			e.printStackTrace();
		}

		/*
		 * response.setContentType("application/json;charset=utf-8");
		 * 
		 * PrintWriter out = response.getWriter(); JSONObject obj = new JSONObject();
		 * 
		 * HomeProxy proxy = HomeProxy.getInstance(); Facade remote = proxy.getFacade();
		 * 
		 * String condition = null; String hourdate = null; String daydate = null;
		 * String startDate = null; String endDate = null; String cmd = "cmd"; Calendar
		 * cal = Calendar.getInstance();
		 * 
		 * org.jdom2.Document doc; HashMap<String, String> dummy = new HashMap<String,
		 * String>();
		 * 
		 * 
		 * if (cmd.equalsIgnoreCase("day")) { SimpleDateFormat dateFormatGmt = new
		 * SimpleDateFormat("yyyyMMdd");
		 * dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT")); condition =
		 * dateFormatGmt.format(new Date()) + "%"; endDate = condition;
		 * System.out.println("endDate : " + endDate);
		 * 
		 * cal.add(Calendar.DATE, -1); startDate = dateFormatGmt.format(cal.getTime());
		 * System.out.println("startDate : " + startDate); } try { String arr[] = new
		 * String[] { startDate, endDate}; System.out.println(startDate + '+' +
		 * endDate); doc = remote.executeQuery(dataSource, QUERY_FILE,
		 * recordAirqualityByDay, dummy, arr);
		 * 
		 * } catch (SQLException | NamingException | JDOMException | IOException e) {
		 * obj.put(code, a); obj.put(message, e.getMessage());
		 * out.println(obj.toString()); }
		 * 
		 * //
		 */

	}

	private String getCondition(String cmd) {
		String condition = null;
		String hourdate = null;
		String daydate = null;
		String startDate = null;
		String endDate = null;
		Calendar cal = Calendar.getInstance();

		if (cmd.equalsIgnoreCase("hour")) {
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMddHH");
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
			condition = dateFormatGmt.format(new Date()) + "%";
			endDate = condition;
			System.out.println("endDate : " + endDate);

			cal.add(Calendar.HOUR, -3);
			String condition2 = dateFormatGmt.format(cal.getTime());
			startDate = condition2 + "%";
			System.out.println("startDate : " + startDate);

		} else {

		}
		return condition;
	}

	private void doIt(HttpServletRequest req, HttpServletResponse res)
			throws IOException, SQLException, NamingException, JDOMException {
		PrintWriter out = res.getWriter();
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();

		HomeProxy proxy = HomeProxy.getInstance();
		Facade remote = proxy.getFacade();

		HashMap<String, String> dummy = new HashMap<String, String>();
		String cmd = (String) req.getAttribute("cmd");
		String arr[] = new String[] { getCondition(cmd) };

		org.jdom2.Document doc = null;
		//doc = remote.executeQuery(dataSource, QUERY_FILE, recordAirqualityByDay, dummy, arr);
		
		try {
			int updatedCnt = remote.executeUpdate(dataSource, QUERY_FILE, recordAirqualityByDay, dummy, arr);
			if (updatedCnt > 0) {
				obj.put(code, b);
				obj.put(message, "Registed..");
				out.println(obj.toString());
			} else {
				obj.put(code, a);
				obj.put(message, "Disabled..");
				out.println(obj.toString());
			}
		} catch (SQLException | NamingException | JDOMException | IOException e) {
			obj.put(code, a);
			obj.put(message, e.getMessage());
			out.println(obj.toString());
		}
		

		/*
		 * if (cmd.equalsIgnoreCase("day")) { doc = remote.executeQuery(dataSource,
		 * QUERY_FILE, recordAirqualityByDay, dummy, arr); } else { //doc =
		 * remote.executeQuery(dataSource, QUERY_FILE, sendAirqualityByDay, dummy, arr);
		 * } //
		 */

		Connection con = new XmlConnection(doc);
		PreparedStatement stmt = con.prepareStatement("//recordset/row");
		ResultSet rs = stmt.executeQuery();
		int index = 0;
		//JSONObject obj = null;
		JSONObject temp = null;
		RequestBody body = null;
		MediaType mediaType = MediaType.parse("application/json");
		OkHttpClient client = new OkHttpClient().newBuilder().build();

		Request request = null;
		Response response = null;

		/*
		 * while (rs.next()) { obj = new JSONObject(); obj.put(dataType, "D");
		 * obj.put(dataRegTime, rs.getString(dataRegTime)); obj.put(dataType,
		 * rs.getString(dataType)); obj.put(dataPm1, i); obj.put(dataPm25,
		 * rs.getString(dataPm25)); obj.put(dataPm10, rs.getString(dataPm10));
		 * obj.put(dataTemp, rs.getString(dataTemp)); obj.put(dataHumi,
		 * rs.getString(dataHumi)); obj.put(dataPress, rs.getString(dataPress));
		 * obj.put(dataCo, i); obj.put(dataCo2, rs.getString(dataCo2));
		 * obj.put(dataTvoc, rs.getString(dataTvoc)); obj.put(dataFirmwareVersion, i);
		 * try { temp = get(rs.getString(ip)); }catch(IOException e) {
		 * e.printStackTrace(); continue; }catch(ParseException e) {
		 * e.printStackTrace(); continue; } Double lat = (Double) temp.get(latitude);
		 * Double lng = (Double) temp.get(longitude);
		 * 
		 * obj.put(dataDeviceLat, lat == null ? 0 : lat); obj.put(dataDeviceLng, lng ==
		 * null ? 0 : lng);
		 * 
		 * System.out.println(obj.toJSONString()); body =
		 * RequestBody.create(obj.toJSONString(),mediaType);
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * }// end of while
		 * 
		 * //
		 */

	}

	public static void main(String[] args) {

		PreparedStatement pstmt = null;
		Connection con = null;
		try {
			con = DBConn.getConnection();

			StringBuffer sb = new StringBuffer();
			sb.append(
					"insert ignore into airqualityData(serialNum, ip, selectDate, pm25, pm10, temperature, humid, co2, tvoc, latitude, longitude, timezone) ");
			sb.append("SELECT ");
			sb.append("serialNum, ");
			sb.append("ANY_VALUE(ip) AS ip, ");
			sb.append("LEFT(DATE_FORMAT(selectDate, '%Y%m%d%H%i00'),15) AS `selectDate`, ");
			sb.append("ROUND(AVG(pm25),1) AS pm25, ");
			sb.append("ROUND(AVG(pm10),1) AS pm10, ");
			sb.append("ROUND(AVG(temperature),1) AS temperature, ");
			sb.append("ROUND(AVG(humid),1) AS humid, ");
			sb.append("ROUND(AVG(co2),1) AS co2, ");
			sb.append("ROUND(AVG(tvoc),1) AS tvoc, ");
			sb.append("ANY_VALUE(latitude) AS latitude, ");
			sb.append("ANY_VALUE(longitude) AS longitude, ");
			sb.append("ANY_VALUE(timezone) AS timezone ");
			sb.append("FROM airquality a ");
			sb.append("WHERE 1 = 1 ");
			sb.append("and selectDate > ? ");
			sb.append("and selectDate <= ? ");
			sb.append(
					"GROUP BY serialNum, HOUR(`selectDate`), FLOOR(MINUTE(`selectDate`)/1)");

			String sql = sb.toString();

			pstmt = con.prepareStatement(sql);
			System.out.println(sql);

			pstmt.setString(1, "2022091500%");
			pstmt.setString(2, "2022091501%");

			SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
			Date time = new Date();
			
			String time1 = format1.format(time);
			
			

			System.out.println(time1);
			int cnt = pstmt.executeUpdate();
			Date time2 = new Date();
			
			String ntime2 = format1.format(time2);
			System.out.println("UpdateCnt TIme: " + "+" + ntime2);

			if (cnt == 1) {
				System.out.println("insert into~");
				System.out.println(time2);
			}
			System.out.println(time2);
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
					pstmt = null;
				} catch (SQLException ex) {
				}
			;
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
				}
			;
		}

	}
}
