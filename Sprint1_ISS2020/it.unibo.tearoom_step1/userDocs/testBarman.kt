package test

import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mapRoomKotlin.mapUtil
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.MsgUtil
import it.unibo.kactor.MqttUtils
 

class TestRobotboundary {
	var barman             : ActorBasic? = null
	val mqttTest   	      = MqttUtils("test") 
	val initDelayTime     = 1000L   // 

	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi
	@Before
	fun systemSetUp() {
		println("Preparing test for barman actor...")
   		kotlin.concurrent.thread(start = true) {
			it.unibo.ctxbarman.main() 
		}
	}

	@After
	fun terminate() {
		println("%%%  TestBarman terminate ")
	}
	
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun forwardToBarman(msgId: String, payload:String){
		println(" --- forwardToBarman --- $msgId:$payload")
		if( barman != null )  MsgUtil.sendMsg( "test",msgId, payload, barman!!  )
	}
	
	fun checkResource(value: String){		
		if( barman != null ){
			println(" --- checkResource --- ${barman!!.geResourceRep()}")
			assertTrue( barman!!.geResourceRep() == value)
		}  
	}



	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi
	@Test
	fun testBarman(){
	 	runBlocking{
 			while( barman == null ){
				println("testBarman wait for barman ... ")
				delay(initDelayTime)  //time for robot to start
				barman = it.unibo.kactor.sysUtil.getActor("barman")
 			}
			
			//first the barman is waiting for an order
 			checkResource("idle")
			
			forwardToBarman("order","order(1,peach)")
			delay(200)
			
			//check if barman is preparing the tea
			checkResource("preparing(1,peach)")
			delay(1000)
			
			//after some time the barman should be idle again
			checkResource("idle")
			
 			if( robot != null ) robot!!.waitTermination()
  		}
	 	println("testBarman BYE  ")  
	}
}