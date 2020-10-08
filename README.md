# Martini_Drudi_ISS2020


To be able to run and test manually the system, please refer to the following steps:

1. import in Eclipse the following projects:
	- <b>Sprint4_ISS2020/it.unibo.waiter_step4</b>		: waiter project (it includes Waiter, Mover and Mastertimer)
	- <b>Sprint4_ISS2020/it.unibo.tearoom_step3</b>	: tearoom project (it includes Tearoom, Barman and Smartbell)
	- <b>Sprint4_ISS2020/clientPlusManagerGuiStep4</b>	: client GUI for testing purposes
	- <b>Sprint3_ISS2020/managerGui2</b>			: manager GUI (optional)

2. run the virtual robot (refer to it.unibo.virtualRobot20): <b>it.unibo.virtualRobot2020\node\WEnv\server\src\node main 8999</b>

3. on the browser open (virtual robot view): <b>localhost:8090</b> 

4. run the mosquitto MQTT broker (if not present you may change the mqtt path in the aforementioned projects): <b>mosquitto\mosquitto -v</b>

5. run the tearoom : <b>it.unibo.tearoom_step3 -> package it.unibo.ctxtearoom -> MainCtxtearoom.kt -> Right click -> Run as... -> Kotlin Application</b>

6. run the waiter: <b>it.unibo.waiter_step4 -> package it.unibo.ctxwaiter -> MainCtxwaiter.kt -> Right click -> Run as... -> Kotlin Application</b>

7. run the client Spring web server: <b>clientPlusManagerGuiStep4 -> package it.unibo.clientGui -> ClientGuiApplication.java -> Right click -> Run as... -> Java application</b>

8. on the browser open (client view): <b>localhost:8085</b>

To also open the manager view:

9. run the client Spring web server: <b>managerGui2 -> package it.unibo.managerGui -> ManagerGuiApplication.java -> Right click -> Run as... -> Java application</b>

10. on the browser open (manager view): <b>localhost:8080</b>

