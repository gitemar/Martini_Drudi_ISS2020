/* Generated by AN DISI Unibo */ 
package it.unibo.smartbell

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Smartbell ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						discardMessages = false
					}
					 transition( edgeName="goto",targetState="waitRing", cond=doswitch() )
				}	 
				state("waitRing") { //this:State
					action { //it:State
						println("smartbell | waiting for clients to ring ... ")
					}
					 transition(edgeName="t03",targetState="handleRing",cond=whenRequest("ring"))
				}	 
				state("handleRing") { //this:State
					action { //it:State
						println("smartbell | a CLIENT rang the bell! ... ")
						
									var temperature = SmartbellUtils.getTemperature()	
									//var temperature = 36.0
									var CLIENT_ID = "NO"
									if (temperature < 37.5) {
										CLIENT_ID = SmartbellUtils.getClientID()
									}
						println("$name in ${currentState.stateName} | $currentMsg")
						answer("ring", "sendClientID", "sendClientID($CLIENT_ID)"   )  
						println("Scanning client... it's temperature is $temperature and ID is $CLIENT_ID")
						if(  temperature < 37.5  
						 ){println("Smartbell | notifyng waiter that a client want to enter")
						forward("enter", "enter($CLIENT_ID)" ,"waiter" ) 
						}
					}
					 transition( edgeName="goto",targetState="waitRing", cond=doswitch() )
				}	 
			}
		}
}
