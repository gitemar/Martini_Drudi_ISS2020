# Martini_Drudi_ISS2020


To be able to run and test manually the system, please refer to the following steps:

1. import in Eclipse the following projects:
	- Sprint4_ISS2020/it.unibo.waiter_step4		: waiter project (it includes Waiter, Mover and Mastertimer)
	- Sprint4_ISS2020/it.unibo.tearoom_step4	: tearoom project (it includes Tearoom, Barman and Smartbell)
	- Sprint4_ISS2020/clientPlusManagerGuiStep4	: client GUI for testing purposes
	- Sprint3_ISS2020/managerGui2			: manager GUI (optional)

2. run the virtual robot (refer to it.unibo.virtualRobot20): it.unibo.virtualRobot2020\node\WEnv\server\src\node main 8999

3. on the browser open (virtual robot view): localhost:8090 

4. run the mosquitto MQTT broker (if not present you may change the mqtt path in the aforementioned projects): mosquitto\mosquitto -v

5. run the tearoom : it.unibo.tearoom_step4 package it.unibo.ctxtearoom -> MainCtxtearoom.kt -> Right click -> Run as... -> Kotlin Application

6. run the waiter: it.unibo.waiter_step4 -> package it.unibo.ctxwaiter -> MainCtxwaiter.kt -> Right click -> Run as... -> Kotlin Application

7. run the client Spring web server: clientPlusManagerGuiStep4 -> package it.unibo.clientGui -> ClientGuiApplication.java -> Right click -> Run as... -> Java application

8. on the browser open (client view): localhost:8085

To also open the manager view:

9. run the client Spring web server: managerGui2 -> package it.unibo.managerGui -> ManagerGuiApplication.java -> Right click -> Run as... -> Java application

10. on the browser open (manager view): localhost:8080

