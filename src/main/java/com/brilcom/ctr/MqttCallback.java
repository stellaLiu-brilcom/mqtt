package com.brilcom.ctr;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jepetto.logger.DisneyLogger;
import org.jepetto.proxy.HomeProxy;

public class MqttCallback implements org.eclipse.paho.client.mqttv3.MqttCallback {

	DisneyLogger cat = new DisneyLogger(DisneyLogger.class.getName());
	int count;
	int size = 60;
	String arr[] = new String[size];

	HomeProxy proxy;
	
	public MqttCallback(HomeProxy proxy)  {
		this.proxy = proxy;
	}

	@Override
	public void connectionLost(Throwable arg0) {
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) 
	{
		String raw = null;	
		try {
			raw = message.toString();
			//System.out.println(raw);
			//cat.info(raw);
			
			try {
				arr[count] = raw + topic;
				count++;
				
			}catch(ArrayIndexOutOfBoundsException e) {
				MyThread m = new MyThread(proxy,arr);
				m.start();
				arr = new String[size];
				count = 0;
			}
			//
		} catch (java.lang.NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}
