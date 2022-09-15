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
