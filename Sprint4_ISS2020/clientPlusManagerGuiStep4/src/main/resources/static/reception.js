var cid="";
var tid="";

function logCookies(){
	console.log("Client ID : "+cid);
	console.log("Table : "+tid);
}

function getCookie(cname) {
	var name = cname + "=";
	console.log(name);

	var decodedCookie = decodeURIComponent(document.cookie);
	console.log(decodedCookie);

	var ca = decodedCookie.split(';');
	for(var i = 0; i < ca.length; i++) {
		
		var c = ca[i];
		console.log(c);

		while (c.charAt(0) == ' ') {
			c = c.substring(1);
			console.log(c);
		}

		if (c.indexOf(name) == 0) {
			console.log(c.substring(name.length, c.length));
			return c.substring(name.length, c.length);
		}
	}
	return "";
}

function connectSocket() {
    var socket = new SockJS('/it-unibo-iss');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
		var topic1="/topic/"+cid;
		var topic2="/topic/"+tid;
        stompClient.subscribe(topic1, function (msg) {
            showMsg(msg);
        });
		if(tid==="0"){
			console.log("Not connecting to table socket - not sitted");
		}
		else{
			stompClient.subscribe(topic2, function (msg) {
				showMsg(msg);
			});
		}
		stompClient.subscribe('/topic/displaytearoom', function (msg) {
             showTearoom(msg);
        });
		stompClient.subscribe('/topic/displaybarman', function (msg) {
             showBarman(msg);
        });
		stompClient.subscribe('/topic/displaywaiterstate', function (msg) {
             showWaiter(msg);
        });
    });
}

function showTearoom(message) {
	var testo = message.body;
	
	console.log(testo);
	var resp = JSON.parse(testo);
	$('#tearoom').html(resp.content);
}

function showBarman(message) {
	var testo = message.body
	
	console.log(testo);
	var resp = JSON.parse(testo);
	$('#barman').html(resp.content);
}

function showWaiter(message) {
	var testo = message.body
	
	console.log(testo);
	var resp = JSON.parse(testo);
	$('#waiter').html(resp.content);
}


function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function showMsg(message) {
	var testo = message.body
	console.log(message);
		
	if(testo === 'showmenu'){
		$('#waitingDiv').remove();
		$('#menuDiv').css('visibility', 'visible');
	}
	else if(testo === 'orderplease'){
		$('#waitStatus').html("Click on the order button near the tea you want.");
		$('.orderbutton').css('visibility', 'visible');
	}
	else if(testo === 'teaserved'){
		$('.titleBar').html("Consume");
		$('#waitingTea').remove();
		$('#consumingTea').css('visibility', 'visible');
	}
	else if(testo === 'cardplease'){
		$('.titleBar').html("Payment");
		$('#resp').html("<h3>Waiter is executing the transaction and taking you to the exit door.</h3><h4>Please, wait...</h4>");
	}
	else if(testo === 'exitplease'){
		$('.titleBar').html("Exit");			
		$('#resp').html("<h3>Thank you for being our customer! Good bye!</h3>");
	}
	else if(testo === 'maxtime'){
		$('.titleBar').html("Paying");
		
		if( $('#consumingTea').length ){
			$('#consumingTea').remove();
		}
		else{
			$('#menuDiv').remove();
		}
		$('#resp').css('visibility', 'visible');
		$('#resp').html("<h3>Your max stay time finished. The waiter is performing the payment transaction and taking you to the exit door.</h3><h4>Please, wait...</h4>")
	}
	else{
		
	    $("#waiterResp").html(message.body);
	}
}