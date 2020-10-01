/* Generated by AN DISI Unibo */ 
package it.unibo.mastertimer

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Mastertimer ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
				val map = mutableMapOf<Int, ActorBasic?>()
				var Teatable : Int
				
				val endedTimers = mutableMapOf<Int, Boolean?>()
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						discardMessages = false
						
									//map init: create timer to be immediately ready when Waiter want to use timers
									map.put(1, Subtimer("subtimer1", scope, context!!, 1))
									map.put(2, Subtimer("subtimer2", scope, context!!, 2))
						
									endedTimers.put(1, false)
									endedTimers.put(2, false)
									
									// waiting for subtimer to be created
									delay(1000)
					}
					 transition( edgeName="goto",targetState="waitForCommand", cond=doswitch() )
				}	 
				state("waitForCommand") { //this:State
					action { //it:State
						println("mastertimer | waiting for waiter's command...")
					}
					 transition(edgeName="t05",targetState="startTimer",cond=whenDispatch("startTimer"))
					transition(edgeName="t06",targetState="stopTimer",cond=whenDispatch("stopTimer"))
					transition(edgeName="t07",targetState="resumeTimer",cond=whenDispatch("resumeTimer"))
					transition(edgeName="t08",targetState="endTimer",cond=whenDispatch("endTimer"))
					transition(edgeName="t09",targetState="handleTimeout",cond=whenDispatch("timeoutSubtimer"))
				}	 
				state("startTimer") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("startTimer(TEATABLE_ID,MAX_TIME)"), Term.createTerm("startTimer(TABLE,TIME)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("mastertimer | starting timer for teatable ${payloadArg(0)} and timeout ${payloadArg(1)} ms")
								
										    	Teatable = payloadArg(0).toInt()
										    	endedTimers.put(Teatable, false)  //forget past end for simulaneus timeout/endTimer message handling
										    	forward("startSubtimer", "startSubtimer(${payloadArg(1)})" ,"${map.get(Teatable)!!.name}" )
						}
					}
					 transition( edgeName="goto",targetState="waitForCommand", cond=doswitch() )
				}	 
				state("stopTimer") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("stopTimer(TEATABLE_ID)"), Term.createTerm("stopTimer(TABLE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("mastertimer | stopping timer for teatable ${payloadArg(0)}...")
								 Teatable = payloadArg(0).toInt() 
								if(  endedTimers.get(Teatable)!! == true  
								 ){println("mastertimer | [stopTimer] ignoring message already handled...")
								}
								else
								 {println("mastertimer | stopping timer for teatable ${payloadArg(0)}...")
								  forward("stopSubtimer", "stopSubtimer(arg)" ,"${map.get(Teatable)!!.name}" )  
								 }
						}
					}
					 transition( edgeName="goto",targetState="waitForCommand", cond=doswitch() )
				}	 
				state("resumeTimer") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("resumeTimer(TEATABLE_ID)"), Term.createTerm("resumeTimer(TABLE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("mastertimer | resuming timer for teatable ${payloadArg(0)}...")
								
										    	Teatable = payloadArg(0).toInt()
										    	forward("resumeSubtimer", "resumeSubtimer(arg)" ,"${map.get(Teatable)!!.name}" )
						}
					}
					 transition( edgeName="goto",targetState="waitForCommand", cond=doswitch() )
				}	 
				state("endTimer") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("endTimer(TEATABLE_ID)"), Term.createTerm("endTimer(TABLE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("mastertimer | ending timer for teatable ${payloadArg(0)}...")
								 Teatable = payloadArg(0).toInt() 
								if(  endedTimers.get(Teatable)!! == true  
								 ){println("mastertimer | [endTimer] ignoring message already handled...")
								}
								else
								 {println("mastertimer | ending timer for teatable ${payloadArg(0)}...")
								  forward("endSubtimer", "endSubtimer(arg)" ,"${map.get(Teatable)!!.name}" )  
								  endedTimers.put(Teatable, true)  
								 }
						}
					}
					 transition( edgeName="goto",targetState="waitForCommand", cond=doswitch() )
				}	 
				state("handleTimeout") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("timeoutSubtimer(TEATABLE_ID)"), Term.createTerm("timeoutSubtimer(TABLE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Teatable = payloadArg(0).toInt() 
								if(  endedTimers.get(Teatable)!! == true  
								 ){println("mastertimer | [timeoutSubtimer] ignoring message already handled...")
								}
								else
								 {println("mastertimer | timeout for teatable $Teatable...")
								 forward("timeout", "timeout($Teatable)" ,"waiter" ) 
								 	endedTimers.put(Teatable, true)  
								 }
						}
					}
					 transition( edgeName="goto",targetState="waitForCommand", cond=doswitch() )
				}	 
			}
		}
}
