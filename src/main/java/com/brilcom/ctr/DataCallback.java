package com.brilcom.ctr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.jepetto.proxy.HomeProxy;

public class DataCallback {

	final String MQTT_BROKER_IP = "tcp://mqtt.brilcom.com";	

	private boolean isRunning = true;
	
	static int size = 60;
	
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
	
	public static void main(String[] args) throws IOException {
		
		Hashtable<String, String> ht = new Hashtable<String, String>(); 
		
		try {
			new DataCallback().init();
			FileWriter writer = new FileWriter("log.txt");
			BufferedWriter bw = new BufferedWriter(writer);
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while((line = br.readLine()) !=null) {
				buffer.append(line);
				bw.write(line);
				bw.flush();
				for(int i=0; i <= size; i++) {
					
					
					
					
				}
			}		
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		String line = "";
		
		File file = new File("log.txt");
		PrintStream print = new PrintStream(new FileOutputStream(file));
	}

}
