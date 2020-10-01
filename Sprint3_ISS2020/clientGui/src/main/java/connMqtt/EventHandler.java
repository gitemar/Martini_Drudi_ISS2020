package connMqtt;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import it.unibo.clientGui.ProvaController;
import utils.KotParser;

public final class EventHandler {
	
	public static void handleWaitEvent(String message, ProvaController pc) {
		
		String [] els = getMessageElements(message, "wait");
		System.out.println("__________Waiter said that client " + els[0] + " has to wait " + els[1] + "mins");
		String resp="";
		
		try{
			if(Long.parseLong(els[1]) == 0L) {
				System.out.println("__________Client "+els[0]+" can enter now");
				resp="Waiter :- Please, follow me...";
			}
			else {
				long temp = Long.parseLong(els[1]);
				long minutes = temp / (1000L*60L);
				long seconds = ( temp / 1000L ) % 60L;
				System.out.println("__________Client "+els[0]+" has to wait");
				resp="You have to wait at maximum " + minutes +" minutes and "+seconds+" seconds. Please, come again later. We are sorry for the waiting...";
			}
			pc.updateWaiterResp(resp, "/topic/"+els[0]);
		}
		catch(Exception e) {
			e.printStackTrace();
			e.getCause();
			System.err.println("ERRORE");
		}
		
	}
	
	public static void handleSitEvent(String message, ProvaController pc) {
		
		String[] elements = getMessageElements(message.toString(),"sitPlease");
		int table = Integer.parseInt(elements[1]) -1;
		pc.getTavoliClienti()[table] = elements[0];
		System.out.println("__________________Client "+elements[0]+" was brought to teatable "+elements[1]);
		pc.updateWaiterResp("showmenu", "/topic/"+elements[0]);
		
	}
	
	public static void handleOrderEvent(String message, ProvaController pc) {
		
		String[] elements = getMessageElements(message.toString(),"orderPlease");
		System.out.println("__________________Client at teatable "+ elements[0] +" can now order");
		pc.updateWaiterResp("orderplease", "/topic/"+elements[0]);
		
	}
	
	public static void handleTeaServedEvent(String message, ProvaController pc) {
		
		String[] elements = getMessageElements(message.toString(),"teaServed");
		System.out.println("__________________"+elements[1]+" tea was brought to teatable "+elements[0]);
		pc.updateWaiterResp("teaserved", "/topic/"+elements[0]);
		
	}
	
	public static void handleCardEvent(String message, ProvaController pc) {
		
		String[] elements = getMessageElements(message.toString(),"cardPlease");
		System.out.println("__________________Client at teatable "+ elements[0] +" can now pay");
		pc.updateWaiterResp("cardplease", "/topic/"+elements[0]);
		
	}
	
	public static void handleMaxTimeExceededEvent(String message, ProvaController pc) {
		
		String[] elements = getMessageElements(message.toString(),"maxTimeExceeded");
		System.out.println("__________________Max time exceeded for teatable "+ elements[0]);
		int table = Integer.parseInt(elements[0]);
		String id=pc.getTavoliClienti()[ table - 1];
		pc.updateWaiterResp("maxtime", "/topic/"+id);
		
	}
	
	public static void handleExitEvent(String message, ProvaController pc) {
		
		String[] elements = getMessageElements(message.toString(),"exitPlease");
		System.out.println("__________________Client "+elements[0]+" can now leave ");
		pc.updateWaiterResp("exitplease", "/topic/"+elements[0]);
		
	}
	
	public static String[] getMessageElements(String message, String kw) {
		
		String waiterEvent = KotParser.getMessageArg(message,kw);
		System.out.println("_________________________MqttClient "+kw+" event arrived | "+waiterEvent);
		String[] elements = KotParser.getPayloadElements(waiterEvent);
		
		return elements;
		
	}
	
	

}
