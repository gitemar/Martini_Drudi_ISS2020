var stompClient = null;
var hostAddr = "http://localhost:8080/ring";


function connectSocket() {
    var socket = new SockJS('/it-unibo-iss');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
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

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function showTearoom(message) {
	var testo = message.body;
	
	console.log(testo);
	var resp = JSON.parse(testo);
	$('#tearoom').html("Tearoom state |    " + resp.content);
}

function showBarman(message) {
	var testo = message.body
	
	console.log(testo);
	var resp = JSON.parse(testo);
	$('#barman').html("Barman state |    " + resp.content);
}

function showWaiter(message) {
	var testo = message.body
	
	console.log(testo);
	var resp = JSON.parse(testo);
	$('#waiter').html("Waiter state |    " + resp.content);
}