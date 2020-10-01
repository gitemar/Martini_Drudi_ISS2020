function connectMonitorSocket() {
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

function disconnectMonitorSocket() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
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

function showTearoom(message) {
	var testo = message.body;
	
	console.log(testo);
	var resp = JSON.parse(testo);
	$('#tearoom').html(resp.content);
}