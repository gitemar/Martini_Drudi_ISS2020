%====================================================================================
% waiter description   
%====================================================================================
mqttBroker("localhost", "1883", "unibo/polar").
context(ctxwaiter, "127.0.0.1",  "TCP", "8029").
context(ctxtearoom, "localhost",  "TCP", "8015").
 qactor( tearoom, ctxtearoom, "external").
  qactor( barman, ctxtearoom, "external").
  qactor( smartbell, ctxtearoom, "external").
  qactor( datacleaner, ctxwaiter, "rx.dataCleaner").
  qactor( distancefilter, ctxwaiter, "rx.distanceFilter").
  qactor( basicrobot, ctxwaiter, "it.unibo.basicrobot.Basicrobot").
  qactor( trustingwalker, ctxwaiter, "it.unibo.trustingwalker.Trustingwalker").
  qactor( mover, ctxwaiter, "it.unibo.mover.Mover").
  qactor( mastertimer, ctxwaiter, "it.unibo.mastertimer.Mastertimer").
  qactor( waiter, ctxwaiter, "it.unibo.waiter.Waiter").
