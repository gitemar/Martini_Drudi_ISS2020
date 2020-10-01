%====================================================================================
% mover description   
%====================================================================================
mqttBroker("localhost", "1883", "unibo/polar").
context(ctxmover, "127.0.0.1",  "TCP", "8029").
 qactor( datacleaner, ctxmover, "rx.dataCleaner").
  qactor( distancefilter, ctxmover, "rx.distanceFilter").
  qactor( basicrobot, ctxmover, "it.unibo.basicrobot.Basicrobot").
  qactor( trustingwalker, ctxmover, "it.unibo.trustingwalker.Trustingwalker").
  qactor( mover, ctxmover, "it.unibo.mover.Mover").
