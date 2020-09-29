package it.unibo.clientGui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import connMqtt.MqttConfig;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import it.unibo.kactor.ApplMessage;
import it.unibo.kactor.MsgUtil;
import pojos.ResourceRepMine;

import utils.KotParser;

import org.springframework.beans.factory.annotation.Autowired;

import connQak.ConnQakCoapMine;



@Controller
public class ProvaController {
	
	String appName     ="provaGui";
    String htmlPage  = "welcome";
    ConnQakCoapMine connQakSupportS;
    ConnQakCoapMine connQakSupportW;
    MqttConfig mqttSupport;
    String cID="";
    String tID="";
    
    @Autowired
    SimpMessagingTemplate smt;
    
   
	public ProvaController() {
        connQakSupportS = new ConnQakCoapMine();
        connQakSupportW = new ConnQakCoapMine();
        mqttSupport = new MqttConfig("localhost:1883");
        connQakSupportS.createConnection("localhost", "8015", "ctxtearoom", "smartbell");
        connQakSupportW.createConnection("127.0.0.1", "8029", "ctxwaiter", "waiter");
        mqttSupport.connect();
        mqttSupport.setReceivingHandler("unibo/polar", this);
        
	}
	
	
	/*--------------------------------------------MAPPING-----------------------------------------------------------*/
	
	@GetMapping("/")
	public String welcomePage(Model model){
		
		System.out.println("________________________Welcome page requested... " + model);
		model.addAttribute("attr", "${stringawelcome}");
		//peparePageUpdating();
		return "welcome";
		
	}
	
	@PostMapping("/enter")
	public String afterRingPage(Model model) {
		System.out.println("________________________Client rang the smartbell... "+model);
		
		try {
			ApplMessage msg = MsgUtil.buildRequest("clientWebPage", "ring", "ring()", "smartbell");
			String answer = connQakSupportS.request( msg );
			
			this.cID = answer;
			
			while(!(model!=null)) {
				System.out.println("________________________Waiting for smartbell reply...");
				Thread.sleep(200);
			}
//			ResourceRepMine rep = getWebPageRep(0);
//			
//			if(!(rep.getContent().contains("sendClientID"))) {
//				System.out.println("_______________________Smartbell | Primo stato scartato: "+rep.getContent());
//				rep = getWebPageRep(0);
//			}
			
			System.out.println("_______________________Smartbell reply: "+answer);
			String html = this.getAccessResult(answer, model);
			
			
			return ""+html;	
		}
		catch(Exception e) {
			System.out.println("_________________________ERROR=" + e.getMessage());
			e.printStackTrace();
			return "welcome";
		}
	}
	
	@PostMapping("/order")
	public String wantToOrder(Model model){
		
		System.out.println("_______________________Client wants to order...");
		
		String answer = connQakSupportW.request(MsgUtil.buildRequest("clientWebPage", "wantToOrder", "wantToOrder("+this.tID+")", "waiter"));
//		while(!(model!=null)) {
//			System.out.println("________________________Waiting for waiter reply...");
//			try {
//				Thread.sleep(200);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		System.out.println("Waiter arrived at table... | " + answer);
//		ResourceRepMine rep = getWebPageRep(1);
		return "order";
	}
	
	
	@PostMapping("/consume")
	public String afterOrder(@RequestParam String type, Model model){
		
		System.out.println("_______________________Client has requested " + type + " tea...");		
		connQakSupportW.forward(MsgUtil.buildDispatch("clientWebPage", "tea", "tea("+this.tID+","+type+")", "waiter"));
		model.addAttribute("teaOrdered", type);
		return "consume";
	}
	
	@PostMapping("/exit")
	public String afterOrder(Model model){
		
		System.out.println("_______________________Client has requested  the bill...");		
		String answer = connQakSupportW.request(MsgUtil.buildRequest("clientWebPage", "billPlease", "billPlease("+this.tID+")", "waiter"));
//		while(!(model!=null)) {
//			System.out.println("________________________Waiting for waiter reply...");
//			try {
//				Thread.sleep(200);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		System.out.println("Waiter arrived at the table... | answer");
//		ResourceRepMine rep = getWebPageRep(1);
		return "exit";
	}
	
	/*-------------------------------------------UTILITIES------------------------------------------------------------*/
	
	public void updateWaiterResp(String testo, String topic) {
		smt.convertAndSend(topic, testo);
		System.out.println("____________________________Updating client web-view...");
	}
	
	
	
	public ResourceRepMine getWebPageRep(int con)   {
		if(con==0) {
			String resourceRep = connQakSupportS.readRep();
			System.out.println("__________________________Controller resourceRep=" + resourceRep  );
			return new ResourceRepMine("" + HtmlUtils.htmlEscape(resourceRep)  );	
		}
		else{
			String resourceRep = connQakSupportW.readRep();
			System.out.println("__________________________Controller resourceRep=" + resourceRep  );
			return new ResourceRepMine("" + HtmlUtils.htmlEscape(resourceRep)  );
			
		}
	}
	
	
	public String getAccessResult(String msg, Model m) {
		
		String msgArg = KotParser.getMessageArg(msg, "sendClientID");
		System.out.println(msgArg);
		String ret = "welcome";
		
		if(msgArg.equals("NO")) {
			System.out.println("________________________The client cannot enter... "+m);
			ret = "noAccess";
		}
		else {
			System.out.println("________________________The client can enter... "+m);
			m.addAttribute("waiterResp", "Waiting for waiter instructions...");
			cID=msgArg;
			ret = "enter";
		}
		
		return ret;
		
	}


	public String getcID() {
		return cID;
	}


	public void setcID(String cID) {
		this.cID = cID;
	}


	public String gettID() {
		return tID;
	}


	public void settID(String tID) {
		this.tID = tID;
	}

}
