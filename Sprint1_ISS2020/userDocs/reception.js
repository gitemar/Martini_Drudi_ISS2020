var stompClient = null;
var hostAddr = "http://localhost:8080/ring";


function connectSocket() {
    var socket = new SockJS('/it-unibo-iss');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/displaywaiter', function (msg) {
             showMsg(msg);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function showMsg(message) {
	var testo = message.body
	
		
	if(testo === 'showmenu'){
		$('#waitingDiv').remove();
		$('#menuDiv').css('visibility', 'visible');
		disconnect();
	}
	else if(testo === 'teaserved'){
		$('#titleConsume').html("Consume");
		$('#waitingTea').remove();
		$('#consumingTea').css('visibility', 'visible');
	}
	else if(testo === 'exitplease'){
		$('#titleExit').html("Exit");
		$('#resp').html("<h3>Thank you for being our customer! Good bye!</h3>");
		disconnect();
	}
	else if(testo === 'maxtime'){
		$('#titleConsume').html("Max time exceeded");
		$('#consumingTea').remove();
		$('#resp').css('visibility', 'visible');
		$('#resp').html("<h3>Your max stay time finished. The waiter is performing the payment transaction and taking you to the exit door.</h3><h4>Please, wait...</h4>")
	}
	else{
		console.log(message);
	    $("#waiterResp").html(message.body);
	}
}