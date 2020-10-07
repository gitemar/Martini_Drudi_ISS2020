package connMqtt;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.UUID;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.springframework.messaging.MessagingException;
import org.springframework.web.util.HtmlUtils;

import it.unibo.clientGui.ProvaController;
import utils.KotParser;


public class MqttConfig {
	
	private MqttAsyncClient subscriber;


	public MqttConfig(String url) {
		super();
		try {
			this.subscriber = new MqttAsyncClient("tcp://"+url, "client");
			System.out.println("_________________________MqttClient created | "+ subscriber.getClientId());
			
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void connect() {
		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(10);
		try {
			IMqttToken tok = subscriber.connect(options);
			tok.waitForCompletion();
			System.out.println("_________________________MqttClient connected | "+ options.toString());
		} catch (MqttSecurityException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setReceivingHandler(String topicMsg, ProvaController pc) {
		
			try {
				IMqttToken t = subscriber.subscribe(topicMsg, 0);
				System.out.println("_________________________MqttClient subscribed to "+topicMsg + " | "+t.toString());
				subscriber.setCallback(new MqttCallback() {

					@Override
					public void connectionLost(Throwable cause) {
						System.out.println("_________________________MqttClient connection lost | " + cause.getMessage());
					}

					@Override
					public void messageArrived(String topic, MqttMessage message) throws Exception {
						
						System.out.println("_________________________MqttClient event arrive on "+ topic +" | "+message.toString());
						
						if(message.toString().contains("wait,")) {
							EventHandler.handleWaitEvent(message.toString(), pc);			
						}else if(message.toString().contains("sitPlease")) {
							EventHandler.handleSitEvent(message.toString(), pc);
						}else if(message.toString().contains("orderPlease")) {
							EventHandler.handleOrderEvent(message.toString(), pc);
						}else if(message.toString().contains("teaServed")) {
							EventHandler.handleTeaServedEvent(message.toString(), pc);
						}else if(message.toString().contains("cardPlease")) {
							EventHandler.handleCardEvent(message.toString(), pc);
						}else if(message.toString().contains("exitPlease")) {
							EventHandler.handleExitEvent(message.toString(), pc);
						}else if(message.toString().contains("maxTimeExceeded")) {
							EventHandler.handleMaxTimeExceededEvent(message.toString(), pc);
						}else{
							System.out.println("_________________Event not parsed: "+message.toString());
						}					
					}

					@Override
					public void deliveryComplete(IMqttDeliveryToken token) {
						System.out.println("_________________________MqttClient delivery complete | "+token.toString());
						
					}
					
				});
				System.out.println("_________________________MqttClient set callback function");
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			
	}
	
//	public String[] getMessageElements(String message, String kw) {
//		
//		String waiterEvent = KotParser.getMessageArg(message,kw);
//		System.out.println("_________________________MqttClient wait event arrived | "+waiterEvent);
//		String[] elements = KotParser.getPayloadElements(waiterEvent);
//		
//		return elements;
//		
//	}

	
	

}
