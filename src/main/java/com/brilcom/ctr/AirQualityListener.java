package com.brilcom.ctr;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.jepetto.proxy.HomeProxy;
import org.jepetto.util.PropertyReader;


public class AirQualityListener extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	static PropertyReader reader = PropertyReader.getInstance();	
	private final String dataSource		= reader.getProperty("mqtt_mysql_datasource");
	private final String QUERY_FILE		= reader.getProperty("mqtt_query_file");
	
	final String MQTT_BROKER_IP = "tcp://mqtt.brilcom.com";	

	private boolean isRunning = true;

    public AirQualityListener() {
        super();
    }
  
    public void init() {
		try {
			HomeProxy proxy = HomeProxy.getInstance();
			MqttClient client = new MqttClient(MQTT_BROKER_IP,	MqttClient.generateClientId(),	new MemoryPersistence());
			client.connect();
			client.subscribe("Pico-Home/ack/#", 1); 
			client.setCallback(new MqttCallback(proxy)); //
		}
		catch (MqttException e) {
			e.printStackTrace();
			isRunning = false;
		}    	
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		if(isRunning) {
			out.print("Receiving");
		}else {
			out.print("No Receiving....");
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
