package com.brilcom.ctr;

import java.io.IOException;
import java.io.PrintWriter;
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
import org.jepetto.util.PropertyReader;
import org.json.simple.JSONObject;

public class SensorRegistry extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	static PropertyReader reader = PropertyReader.getInstance();	
	private final String dataSource		= reader.getProperty("mqtt_mysql_datasource");
	private final String QUERY_FILE		= reader.getProperty("mqtt_query_file");
	
	private final String code = null;
	private final String message = null;
	private static final int b = 500;
	private static final int c = 200;
       
    public SensorRegistry() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json;charset=utf-8");
		
		PrintWriter out = response.getWriter();
		JSONObject obj = new JSONObject();
		
		String serialNum	= (String)request.getAttribute("serialNum");
		String writeApiKey	= (String)request.getAttribute("writeApiKey");
		String userInfoSeq	= (String)request.getAttribute("userInfoSeq");
		String restApiKey	= (String)request.getAttribute("restApiKey");
		
		HomeProxy proxy = HomeProxy.getInstance();
        Facade remote	= proxy.getFacade();
        String arr[] = new String[] {
        	serialNum,
        	writeApiKey,
        	userInfoSeq,
        	restApiKey
        };
        HashMap<String,String> dummy = new HashMap<String,String>();
        try {
			int updatedCnt = remote.executeUpdate(dataSource, QUERY_FILE, "registSensorWithKey", dummy , arr);
			if(updatedCnt > 0) {
				obj.put(code, c);
				obj.put(message, "Registed..");
				out.println(obj.toString());
			}else {
				obj.put(code, b);
				obj.put(message, "Disabled..");
				out.println(obj.toString());
			}
		} catch (SQLException | NamingException | JDOMException | IOException e) {
			obj.put(code, b);
			obj.put(message, e.getMessage());
			out.println(obj.toString());
		}        
	}

}
