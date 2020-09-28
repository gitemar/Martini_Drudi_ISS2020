package connMqtt;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import it.unibo.clientGui.ClientController;
import utils.KotParser;

public final class EventHandler {
	
	public static void handleWaitEvent(String message, ClientController pc) {
		
		String [] els = getMessageElements(message, "wait");
		System.out.println("__________Waiter said that client " + els[0] + " has to wait " + els[1] + "mins");
		
		try{
			if(Double.parseDouble(els[1]) == 0.0) {
				System.out.println("__________Client can enter now");
				pc.updateWaiterResp("Waiter :- Please, follow me...", "/topic/displaywaiter");
			}
			else {
				double temp = Double.parseDouble(els[1]);
				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.HALF_UP);
				System.out.println("__________Client has to wait");
				pc.updateWaiterResp("You have to wait at maximum " + df.format(temp)+" minutes. We are sorry for the waiting...", "/topic/displaywaiter");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			e.getCause();
			System.err.println("ERRORE");
		}
		
	}
	
	public static void handleSitEvent(String message, ClientController pc) {
		
		String[] elements = getMessageElements(message.toString(),"sitPlease");
		pc.settID(elements[1]);
		System.out.println("__________________Client "+elements[0]+" was brought to teatable "+elements[1]);
		pc.updateWaiterResp("showmenu", "/topic/displaywaiter");
		
	}
	
	public static void handleTeaServedEvent(String message, ClientController pc) {
		
		String[] elements = getMessageElements(message.toString(),"teaServed");
		System.out.println("__________________"+elements[1]+" tea was brought to teatable "+elements[0]);
		pc.updateWaiterResp("teaserved", "/topic/displaywaiter");
		
	}
	
	public static void handleMaxTimeExceededEvent(String message, ClientController pc) {
		
		String[] elements = getMessageElements(message.toString(),"maxTimeExceeded");
		System.out.println("__________________Max time exceeded for teatable "+ elements[0]);
		pc.updateWaiterResp("maxtime", "/topic/displaywaiter");
		
	}
	
	public static void handleExitEvent(String message, ClientController pc) {
		
		String[] elements = getMessageElements(message.toString(),"exitPlease");
		System.out.println("__________________Client "+elements[0]+" can now leave ");
		pc.updateWaiterResp("exitplease", "/topic/displaywaiter");
		
	}
	
	public static String[] getMessageElements(String message, String kw) {
		
		String waiterEvent = KotParser.getMessageArg(message,kw);
		System.out.println("_________________________MqttClient "+kw+" event arrived | "+waiterEvent);
		String[] elements = KotParser.getPayloadElements(waiterEvent);
		
		return elements;
		
	}
	
	

}
