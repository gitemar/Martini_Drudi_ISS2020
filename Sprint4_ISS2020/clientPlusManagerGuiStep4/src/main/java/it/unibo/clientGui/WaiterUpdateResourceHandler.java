package it.unibo.clientGui;

import java.util.regex.Pattern;

public final class WaiterUpdateResourceHandler {
	
	public static void parseUpdate(ProvaController pc, String update) {
		
		if(update.contains("clientSitting")) {
			parseSit(pc,update);
		}
		else if(update.contains("clientOrdering")) {
			parseOrder(pc,update);
		}
		else if(update.contains("teaServed")) {
			parseServed(pc,update);
		}
		else if(update.contains("clientPaying")) {
			parsePayment(pc,update);
		}
		else if(update.contains("clientExiting")) {
			parseExit(pc,update);
		}
		else if(update.contains("maxTimeExceeded")) {
			parseTimeExceed(pc,update);
		}
		else if(update.contains("clientEntering")) {
			parseEnter(pc,update);
		}
		else if(update.contains("clientWaiting")) {
			parseWait(pc,update);
		}
		else {
			System.out.println("________________________Update resource not for client web gui");
		}
	}

	private static void parseEnter(ProvaController pc, String update) {
		String[] elem = getElements(update,":");
		System.out.println("__________Client "+elem[2]+" can enter now");
		String resp="Waiter :- Please, follow me...";
		pc.updateWaiterResp(resp, "/topic/"+elem[2]);
		
	}

	private static void parseWait(ProvaController pc, String update) {
		String[] elem = getElements(update,":");
		long temp = Long.parseLong(elem[3]);
		long minutes = temp / (1000L*60L);
		long seconds = ( temp / 1000L ) % 60L;
		System.out.println("__________Client "+elem[2]+" has to wait");
		String resp="You have to wait at maximum " + minutes +" minutes and "+seconds+" seconds. Please, come again later. We are sorry for the waiting...";
		pc.updateWaiterResp(resp, "/topic/"+elem[2]);
	}

	private static void parseTimeExceed(ProvaController pc, String update) {
		String[] elem = getElements(update,":");
		System.out.println("__________________Update resource : Max time exceeded for teatable "+ elem[2]);
		int table = Integer.parseInt(elem[2]);
		String id=pc.getTavoliClienti()[ table - 1];
		pc.updateWaiterResp("maxtime", "/topic/"+id);
		
	}

	private static void parseExit(ProvaController pc, String update) {
		String[] elem = getElements(update,":");
		System.out.println("__________________Update resource : Client "+elem[2]+" can now leave ");
		pc.updateWaiterResp("exitplease", "/topic/"+elem[2]);
		
	}

	private static void parsePayment(ProvaController pc, String update) {
		String[] elem = getElements(update,":");
		System.out.println("__________________Update resource: Client at teatable "+ elem[2] +" can now pay");
		pc.updateWaiterResp("cardplease", "/topic/"+elem[2]);
		
	}

	private static void parseServed(ProvaController pc, String update) {
		String[] elem = getElements(update,":");
		System.out.println("__________________"+elem[3]+" tea was brought to teatable "+elem[2]);
		pc.updateWaiterResp("teaserved", "/topic/"+elem[2]);
		
	}

	private static void parseOrder(ProvaController pc, String update) {
		String[] elem = getElements(update,":");
		System.out.println("__________________Update resource: Client at teatable "+ elem[2] +" can now order");
		pc.updateWaiterResp("orderplease", "/topic/"+elem[2]);
	}

	private static void parseSit(ProvaController pc, String update) {
		String[] elem = getElements(update,":");
		System.out.println("_______Update resource: client/table | "+elem[2]+"/"+elem[3]);
		int table = Integer.parseInt(elem[3]) -1;
		System.out.println("Table: "+table);
		pc.getTavoliClienti()[table] = elem[2];
		System.out.println("Client id: "+pc.getTavoliClienti()[table] );
		System.out.println("__________________Client "+elem[2]+" was brought to teatable "+elem[3]);
		pc.updateWaiterResp("showmenu", "/topic/"+elem[2]);
		
	}

	private static String[] getElements(String update, String sep) {
		String [] e = Pattern.compile(sep).split(update);
		return e;
	}

}
