package it.unibo.mastertimer

import kotlinx.coroutines.CoroutineScope
import it.unibo.kactor.QakContext
import it.unibo.kactor.ActorBasicFsm
import it.unibo.kactor.TimerActor
import alice.tuprolog.Term

/*
 * startSubtimer : startTimer(TIME)
 * stopSubtimer : stopTimer(ARG)
 * resumeSubtimer : resumeTimer(ARG)
 * endSubtimer : endTimer(ARG)
 * timeoutSubtimer : timeout (TEATABLE_ID)
 */

class Subtimer (name: String, scope: CoroutineScope, val ctx : QakContext , tid : Int ) : ActorBasicFsm( name, scope ){

	 var Teatable : Int = -1
	 //var name = name
	 
	 init {
		 println("$name CREATED")
		// this.context = ctx			//context Injection
		 ctx.addActor(this)	// inject context and abilitate MQTT if possible
		 println("subtimer Context is $context")
		 //this.actor.
		 Teatable = tid
	}
	 
	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
		
		var RemainingTime : Long = -1
		var Record : Long = -1
		
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						discardMessages = true //we want to ignore messages sent in the wrong order. Anyway it SHOULD NEVER happen!
					}
					transition( edgeName="goto",targetState="waitingForCommand", cond=doswitch() )
				}
			
				state("waitingForCommand") { //this:State
					action { //it:State
						println("$name | waiting for master's command...")
						
					}
				transition(edgeName="t05",targetState="startTimer",cond=whenDispatch("startSubtimer"))
				}	 
				state("startTimer") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("startSubtimer(MAX_TIME)"), Term.createTerm("startSubtimer(MAX_TIME)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("$name | starting timer for teatable $Teatable and timeout ${payloadArg(0)} ms")
								
								RemainingTime = payloadArg(0).toLong()
								Record = getCurrentTime()
						}
						stateTimer = TimerActor("$name" + "_startSubtimer", 
							scope, context!!, "local_tout_$name" + "_startSubtimer", RemainingTime )
						println("$name | started TimerActor for $RemainingTime ms!")
					}
					transition(edgeName="t07",targetState="handleTimeout",cond=whenTimeout("local_tout_$name" + "_startSubtimer"))   
					transition(edgeName="t08",targetState="stopTimer",cond=whenDispatch("stopSubtimer"))
					transition(edgeName="t09",targetState="endTimer",cond=whenDispatch("endSubtimer"))
				}	 
				state("stopTimer") { //this:State
					action { //it:State
						println("$name | stopping timer...")
						 var previousInstant = Record 
						 Record = getCurrentTime()
						 RemainingTime = RemainingTime - (Record - previousInstant)  
					}
					 transition(edgeName="t010",targetState="resumeTimer",cond=whenDispatch("resumeSubtimer"))
				}	 
				state("resumeTimer") { //this:State
					action { //it:State
						println("$name | resuming timer for $RemainingTime ms...")
						stateTimer = TimerActor("$name" + "_resumeSubtimer", 
							scope, context!!, "local_tout_$name" + "_resumeSubtimer", RemainingTime )
					}
					 transition(edgeName="t011",targetState="handleTimeout",cond=whenTimeout("local_tout_$name" + "_resumeSubtimer"))   
					transition(edgeName="t012",targetState="endTimer",cond=whenDispatch("endSubtimer"))
				}	 
				state("handleTimeout") { //this:State
					action { //it:State
						println("$name | TIMEOUT! time expired for teatable $Teatable!")
						forward("timeoutSubtimer", "timeoutSubtimer($Teatable)" ,"mastertimer" ) 
					}
					 transition( edgeName="goto",targetState="waitingForCommand", cond=doswitch() )
				}	 
				state("endTimer") { //this:State
					action { //it:State
						println("$name | reset timer")
						
					}
					 transition( edgeName="goto",targetState="waitingForCommand", cond=doswitch() )
				}	 
			} // return
		} // getBody
} // class