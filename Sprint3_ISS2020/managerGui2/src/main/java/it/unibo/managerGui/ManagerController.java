package it.unibo.managerGui;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.HtmlUtils;
import connQak.ConnQakCoapMine;
import pojos.ResourceRepMine;

@Controller
public class ManagerController {
	
	String appName     ="managerGui";
    String htmlPage  = "welcome";
    ConnQakCoapMine connQakSupportT;
    ConnQakCoapMine connQakSupportB;
    ConnQakCoapMine connQakSupportW;
    
    String stateT ="Tearoom state";
    String stateB ="Barman state";
    String stateW ="Waiter state";
//    ConnQakCoapMine connQakSupportW;
//    MqttConfig mqttSupport;
    
    @Autowired
    SimpMessagingTemplate smt;
    
    public ManagerController() {
    	connQakSupportT = new ConnQakCoapMine();
    	connQakSupportB = new ConnQakCoapMine();
    	connQakSupportW = new ConnQakCoapMine();
        stateT = connQakSupportT.createConnection("localhost", "8015", "ctxtearoom", "tearoom");
        stateB = connQakSupportB.createConnection("localhost", "8015", "ctxtearoom", "barman");
        stateW = connQakSupportW.createConnection("127.0.0.1", "8029", "ctxwaiter", "waiter");
    	
    }
    
    @GetMapping
    public String welcomePage(Model m) {
    	System.out.println("________________________Welcome page requested... " + m);
    	
    	return htmlPage;
    }
    
    @PostMapping("/monitor")
    public String requestMonitorPage(Model m) {
    	System.out.println("________________________Monitor page requested... " + m);
    	peparePageUpdating();
    	m.addAttribute("st", stateT);
    	m.addAttribute("sb", stateB);
    	m.addAttribute("sw", stateW);
    	return "monitor";
    }
    
    
    /*---------------------------------------------------------------------------------------------------------*/
    
    private void peparePageUpdating() {
    	connQakSupportT.getClient().observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				System.out.println("MonitorController --> CoapClient changed ->" + response.getResponseText());
				smt.convertAndSend(WebSocketConfig.topicForClient1, 
						new ResourceRepMine("" + HtmlUtils.htmlEscape(response.getResponseText())  ));
			}

			@Override
			public void onError() {
				System.out.println("MonitorController --> CoapClient error!");
			}
		});
    	
    	connQakSupportB.getClient().observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				System.out.println("MonitorController --> CoapClient changed ->" + response.getResponseText());
				smt.convertAndSend(WebSocketConfig.topicForClient2, 
						new ResourceRepMine("" + HtmlUtils.htmlEscape(response.getResponseText())  ));
			}

			@Override
			public void onError() {
				System.out.println("MonitorController --> CoapClient error!");
			}
		});
    	connQakSupportW.getClient().observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				System.out.println("MonitorController --> CoapClient changed ->" + response.getResponseText());
				smt.convertAndSend(WebSocketConfig.topicForClient3, 
						new ResourceRepMine("" + HtmlUtils.htmlEscape(response.getResponseText())  ));
			}

			@Override
			public void onError() {
				System.out.println("MonitorController --> CoapClient error!");
			}
		});
	}
	

}
