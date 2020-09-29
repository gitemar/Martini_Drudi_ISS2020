%====================================================================================
% tearoom description   
%====================================================================================
mqttBroker("localhost", "1883", "unibo/polar").
context(ctxtearoom, "localhost",  "TCP", "8015").
context(ctxwaiter, "127.0.0.1",  "TCP", "8029").
 qactor( waiter, ctxwaiter, "external").
  qactor( tearoom, ctxtearoom, "it.unibo.tearoom.Tearoom").
  qactor( barman, ctxtearoom, "it.unibo.barman.Barman").
  qactor( smartbell, ctxtearoom, "it.unibo.smartbell.Smartbell").
