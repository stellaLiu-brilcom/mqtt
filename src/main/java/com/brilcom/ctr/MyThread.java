
package com.brilcom.ctr;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;
import org.jepetto.bean.Facade;
import org.jepetto.proxy.HomeProxy;
import org.jepetto.sql.JsonTransfer;
import org.jepetto.sql.XmlConnection;
import org.jepetto.util.PropertyReader;

import com.mysql.cj.protocol.Resultset;

public class MyThread extends Thread {

	String arr[] = null;
	String raw = null;
	String delemeter = "|";
	String unixDate = null;
	String pm25 = null;
	String pm10 = null;
	String temperature = null;
	String humid = null;
	String co2 = null;
	String tvoc = null;
	String ip = null;
	String serialNum = null;
	String selectDate = null;
	InetAddress address = null;
	String Latitude = null;
	String Longitude = null;

	static PropertyReader reader = PropertyReader.getInstance();
	private Logger logger = LogManager.getLogger(MyThread.class);

	private static final String dataSource = reader.getProperty("mqtt_mysql_datasource");
	private static final String QUERY_FILE = reader.getProperty("mqtt_query_file");
	private static final String queryKey = "recordQirQuality";
	private static final String hexdata = "0x";

	HomeProxy proxy = null;

	String queryKeys[] = null;
	String queryFiles[] = null;
	int size = 0;

	public MyThread(HomeProxy proxy, String arr[]) {
		this.proxy = proxy;
		this.arr = arr;
		this.size = arr.length;
		queryKeys = new String[size];
		queryFiles = new String[size];
		for (int i = 0; i < size; i++) {
			queryKeys[i] = queryKey;
			queryFiles[i] = QUERY_FILE;
		}
	}

	@Override
	public void run() {
		int updatedCnt = -1;
		HashMap<String, String> dummy = new HashMap<String, String>();
		try {
			int count = 0;
			int index = 0;
			String unixTime = null;

			Facade remote = proxy.getFacade();
			String unixDate = null;

			String pm25 = null;
			String pm10 = null;
			String temperature = null;
			String humid = null;
			String co2 = null;
			String tvoc = null;
			String ip = null;
			String serialNum = null;
			String selectDate = null;
			String _arr[][] = new String[size][];

			// for (int i = 0; i < arr.length; i++) {
			for (int i = 0; i < size; i++) {
				try {
					raw = arr[i];
					unixDate = raw.substring(0, 8); // unixTime (8)
					unixTime = unixDate;
					unixDate = getTimestampToDate(unixDate);
					unixTime = getTimestampToTime(unixTime);

					pm25 = raw.substring(8, 12); // pm25 (4)
					pm25 = get(pm25);

					pm10 = raw.substring(12, 16); // pm10 (4)
					pm10 = get(pm10);

					temperature = raw.substring(16, 20); // temperature (4)
					temperature = getTemp(temperature);

					humid = raw.substring(20, 24); // humid (4)
					humid = getHumid(humid);

					co2 = raw.substring(24, 28); // co2 (4)
					co2 = get(co2);

					tvoc = raw.substring(28, 32); // tvoc (4)
					tvoc = get(tvoc);

					ip = raw.substring(32, 40); // ip (8)
					ip = getIpaddress(ip);

					serialNum = raw.substring(54); // serialNum (12)

					selectDate = unixDate + unixTime;

					_arr[i] = new String[] { unixDate, unixTime, pm25, pm10, temperature, humid, co2, tvoc, ip,
							serialNum, selectDate };

				} catch (java.lang.StringIndexOutOfBoundsException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			} // end of for-loop

			updatedCnt = remote.executeUpdateX(dataSource, queryFiles, queryKeys, _arr);
			// logger.info("updatedCnt " + updatedCnt);
			if (updatedCnt != 15) {
				System.out.println("time : " + selectDate);
			}
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (java.lang.NullPointerException e) {
			e.printStackTrace();
		} catch (javax.naming.NameNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			logger.info(updatedCnt + "updated count ");
		}
	}

	private static String getTimestampToDate(String unixTime) {
		String hex1 = hexdata + unixTime;
		int covertedValue = Integer.decode(hex1);
		long timestamp = Long.parseLong(String.valueOf(covertedValue));
		Date date = new java.util.Date(covertedValue * 1000L);
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
		sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+0"));
		String formattedDate = sdf.format(date);
		return formattedDate;
	}

	private static String getTimestampToTime(String unixTime) {
		String hex1 = hexdata + unixTime;
		int covertedValue = Integer.decode(hex1);
		long timestamp = Long.parseLong(String.valueOf(covertedValue));
		Date date = new java.util.Date(covertedValue * 1000L);
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("HHmmss");
		sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+0"));
		String formattedDate = sdf.format(date);
		return formattedDate;
	}

	public static String get(String str) {
		String hex = hexdata + str;
		int covertedValue = Integer.decode(hex);
		String data = String.valueOf(covertedValue);
		return data;
	}

	private static String getIpaddress(String ip) {
		String ipAddress = "";
		for (int i = 0; i < ip.length(); i = i + 2) {
			ipAddress = ipAddress + Integer.valueOf(ip.substring(i, i + 2), 16) + ".";
		}
		ipAddress = ipAddress.substring(0, ipAddress.length() - 1);
		return ipAddress;
	}

	private static String getTemp(String str) {
		String data = "";
		String hex = hexdata + str;
		double covertedValue = Integer.decode(hex);
		DecimalFormat form = new DecimalFormat("#.#");
		if (covertedValue > 32768) {
			double validatordata = (covertedValue - 65536) / 10;
			String Temp = String.format("%.1f", validatordata);
		} else {
			data = String.format("%.1f", (covertedValue / 10));
		}
		return data;
	}

	private static String getHumid(String str) {
		String hex = hexdata + str;
		float covertedValue = Integer.decode(hex);
		String data = String.format("%.1f", (covertedValue / 10));
		return data;

	}

}
