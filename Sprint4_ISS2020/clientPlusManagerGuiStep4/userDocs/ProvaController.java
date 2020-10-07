package it.unibo.clientGui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import it.unibo.kactor.ApplMessage;
import it.unibo.kactor.MsgUtil;
import pojos.ClientAttributes;
import pojos.ResourceRepMine;

import utils.KotParser;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.springframework.beans.factory.annotation.Autowired;

import connQak.ConnQakCoapMine;



@Controller
public class ProvaController {
	
	String appName     ="provaGui";
    String htmlPage  = "welcome";
    ConnQakCoapMine connQakSupportS;
    ConnQakCoapMine connQakSupportW;
    ConnQakCoapMine connQakSupportT;
    ConnQakCoapMine connQakSupportB;
   
    @Autowired
    ClientAttributes ca;
    
    @Autowired
    SimpMessagingTemplate smt;
    
    String[] tavoliClienti = {"",""};
    String stateT ="";
    String stateW ="";
    String stateB ="";
    ProvaController controller=this;
   
	public ProvaController() {
        connQakSupportS = new ConnQakCoapMine();
        connQakSupportW = new ConnQakCoapMine();
        connQakSupportT = new ConnQakCoapMine();
        connQakSupportB = new ConnQakCoapMine();
       
        connQakSupportS.createConnection("localhost", "8015", "ctxtearoom", "smartbell");
        stateB = connQakSupportB.createConnection("localhost", "8015", "ctxtearoom", "barman");
        stateW = connQakSupportW.createConnection("127.0.0.1", "8029", "ctxwaiter", "waiter");
        stateT = connQakSupportT.createConnection("localhost", "8015", "ctxtearoom", "tearoom");
  
	}
	
	
	/*--------------------------------------------MAPPING-----------------------------------------------------------*/
	
	@GetMapping("/")
	public String welcomePage(Model model, HttpSession s){
		
		System.out.println("________________________Welcome page requested: " + s.getId());
		
		preparePageUpdating();
		try {
			Thread.sleep(350);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("ts", stateT);
		model.addAttribute("ws", stateW);
		model.addAttribute("bs", stateB);
		return "welcome";
		
	}
	
	@PostMapping("/enter")
	public String afterRingPage(Model model, HttpServletResponse r) {
		System.out.println("________________________Client rang the smartbell... "+model);
		
		try {
			ApplMessage msg = MsgUtil.buildRequest("clientWebPage", "ring", "ring()", "smartbell");
			String answer = connQakSupportS.request( msg );
			
			while(!(model!=null)) {
				System.out.println("________________________Waiting for smartbell reply...");
				Thread.sleep(200);
			}
			
			System.out.println("_______________________Smartbell reply: "+answer);
			String html = this.getAccessResult(answer, model);
			r.addCookie(new Cookie("id",ca.getId()));
			r.addCookie(new Cookie("table","0"));
//			preparePageUpdating();
			try {
				Thread.sleep(350);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			model.addAttribute("ts", stateT);
			model.addAttribute("ws", stateW);
			model.addAttribute("bs", stateB);
			return ""+html;	
		}
		catch(Exception e) {
			System.out.println("_________________________ERROR=" + e.getMessage());
			e.printStackTrace();
//			preparePageUpdating();
			try {
				Thread.sleep(350);
			} catch (InterruptedException er) {
				// TODO Auto-generated catch block
				er.printStackTrace();
			}
			model.addAttribute("ts", stateT);
			model.addAttribute("ws", stateW);
			model.addAttribute("bs", stateB);
			return "welcome";
		}
	}
	
	@PostMapping("/order")
	public String wantToOrder(Model model, HttpServletResponse r){
		
		int tid = indexOf(ca.getId()) + 1;		
		ca.setTable(tid);
		System.out.println("_______________________Client wants to order...");
		connQakSupportW.forward(MsgUtil.buildDispatch(ca.getId(), "wantToOrder", "wantToOrder("+ca.getTable()+")", "waiter"));
		r.addCookie(new Cookie("id",ca.getId()));
		r.addCookie(new Cookie("table",""+ca.getTable()));
		preparePageUpdating();
		try {
			Thread.sleep(350);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("ts", stateT);
		model.addAttribute("ws", stateW);
		model.addAttribute("bs", stateB);
		return "order";
	}
	
	@PostMapping("/consume")
	public String afterOrder(@RequestParam String type, Model model, HttpServletResponse r){
		
		System.out.println("_______________________Client has requested " + type + " tea...");		
		connQakSupportW.forward(MsgUtil.buildDispatch("clientWebPage", "tea", "tea("+ca.getTable()+","+type+")", "waiter"));
		r.addCookie(new Cookie("id",ca.getId()));
		r.addCookie(new Cookie("table",""+ca.getTable()));
		model.addAttribute("teaOrdered", type);
//		preparePageUpdating();
		try {
			Thread.sleep(350);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("ts", stateT);
		model.addAttribute("ws", stateW);
		model.addAttribute("bs", stateB);
		return "consume";
	}
	

	@PostMapping("/exit")
	public String afterOrder(Model model, HttpServletResponse r){
		
		System.out.println("_______________________Client has requested  the bill...");		
		connQakSupportW.forward(MsgUtil.buildDispatch(ca.getId(), "billPlease", "billPlease("+ca.getTable()+")", "waiter"));
		r.addCookie(new Cookie("id",ca.getId()));
		r.addCookie(new Cookie("table",""+ca.getTable()));
//		preparePageUpdating();
		try {
			Thread.sleep(350);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("ts", stateT);
		model.addAttribute("ws", stateW);
		model.addAttribute("bs", stateB);
		return "exit";
	}
	
	
	/*-------------------------------------------UTILITIES------------------------------------------------------------*/
	
	public void updateWaiterResp(String testo, String topic) {
		smt.convertAndSend(topic, testo);
		System.out.println("____________________________Updating client web-view...");
		System.out.println("TOPIC: "+topic+"\nTESTO: "+testo);
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
			ca.setId(msgArg);
			ret = "enter";
		}
		
		return ret;
		
	}


	public String[] getTavoliClienti() {
		return tavoliClienti;
	}

	private int indexOf(String id) {
		if(tavoliClienti[0].equals(id)) {
			return 0;
		}
		else {
			if(tavoliClienti[1].equals(id)) {
				return 1;
			}
			else
				return -1;
		}
	}
	
	 private void preparePageUpdating() {
	    	connQakSupportT.getClient().observe(new CoapHandler() {
				@Override
				public void onLoad(CoapResponse response) {
					stateT = response.getResponseText();
					System.out.println("MonitorController --> CoapClient changed ->" + stateT);
					System.out.println("_____________________Updating manager web view...");
					System.out.println("TOPIC: "+WebSocketConfig.topicForManager1+"\nTESTO: "+stateT);
					smt.convertAndSend(WebSocketConfig.topicForManager1, 
							new ResourceRepMine("" + HtmlUtils.htmlEscape(stateT)));
				}

				@Override
				public void onError() {
					System.out.println("MonitorController --> CoapClient error!");
				}
			});
	    	connQakSupportW.getClient().observe(new CoapHandler() {
				@Override
				public void onLoad(CoapResponse response) {
					stateW = response.getResponseText();
					System.out.println("MonitorController --> CoapClient changed ->" + stateW);
					System.out.println("_____________________Updating manager web view...");
					System.out.println("TOPIC: "+WebSocketConfig.topicForManager3+"\nTESTO: "+stateW);
					smt.convertAndSend(WebSocketConfig.topicForManager3, 
							new ResourceRepMine("" + HtmlUtils.htmlEscape(stateW)  ));
					
					WaiterUpdateResourceHandler.parseUpdate(controller, stateW);		
					
				}

				@Override
				public void onError() {
					System.out.println("MonitorController --> CoapClient error!");
				}
			});
	    	connQakSupportB.getClient().observe(new CoapHandler() {
				@Override
				public void onLoad(CoapResponse response) {
					stateB = response.getResponseText();
					System.out.println("MonitorController --> CoapClient changed ->" + stateB);
					System.out.println("_____________________Updating manager web view...");
					System.out.println("TOPIC: "+WebSocketConfig.topicForManager2+"\nTESTO: "+stateB);
					smt.convertAndSend(WebSocketConfig.topicForManager2, 
							new ResourceRepMine("" + HtmlUtils.htmlEscape(stateB)  ));
				}

				@Override
				public void onError() {
					System.out.println("MonitorController --> CoapClient error!");
				}
			});
		}
	


}
