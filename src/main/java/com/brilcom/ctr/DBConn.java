package com.brilcom.ctr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {

	private static Connection con;

	public static Connection getConnection() throws SQLException, ClassNotFoundException {

		if (con == null) {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://database-mqtt.cbr4uubmqr4e.ap-northeast-2.rds.amazonaws.com:3306/mqttairconditioner?characterEncoding=UTF-8&amp";
			
			String user = "picohome";
			String password = "admin4mqtt";
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
		}
		return con;
	}
	
	public static void close() throws SQLException{
		if (con != null) {
			if(!con.isClosed()) {
				con.close();
			}
			
		}
	}
}
