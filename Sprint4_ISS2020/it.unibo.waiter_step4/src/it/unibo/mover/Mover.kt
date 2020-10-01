/* Generated by AN DISI Unibo */ 
package it.unibo.mover

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Mover ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
			var X = 0
			var Y = 0
			var correct = true
			var CurrentPlannedMove = ""
			var StepTime    	   = 450L
			val BackTime           = 2 * StepTime / 3
			//var obstacleFound      = false  
			val inmapname          = "teaRoomExplored" 
			
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						itunibo.planner.plannerUtil.initAI(  )
						itunibo.planner.plannerUtil.loadRoomMap( inmapname  )
						itunibo.planner.plannerUtil.showCurrentRobotState(  )
						solve("consult('positions.pl')","") //set resVar	
					}
					 transition(edgeName="t00",targetState="waitingForCommand",cond=whenEvent("twstarted"))
				}	 
				state("waitingForCommand") { //this:State
					action { //it:State
						println("mover | waiting for waiter's commands...")
						updateResourceRep( "${itunibo.planner.plannerUtil.get_curPos().first},${itunibo.planner.plannerUtil.get_curPos().second}"  
						)
						 correct = false  
					}
					 transition(edgeName="t11",targetState="move",cond=whenRequest("moveTo"))
					transition(edgeName="t12",targetState="end",cond=whenDispatch("end"))
				}	 
				state("move") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("moveTo(KEY_POSITION)"), Term.createTerm("moveTo(P)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var Pos = payloadArg(0)  
								solve("pos($Pos,X,Y)","") //set resVar	
								if( currentSolution.isSuccess() ) { 
														X = getCurSol("X").toString().toInt() 
														Y = getCurSol("Y").toString().toInt()
														correct = true	
								}
								else
								{}
								if(  correct == true  
								 ){println("mover | correct request: [${payloadArg(0)}] correspond to ($X,$Y)")
								request("movetoCell", "movetoCell($X,$Y)" ,"trustingwalker" )  
								}
								else
								 {println("mover | ERROR: required position doesn't exist")
								 println("$name in ${currentState.stateName} | $currentMsg")
								 }
						}
					}
					 transition( edgeName="goto",targetState="waitingForReply", cond=doswitchGuarded({ correct == true  
					}) )
					transition( edgeName="goto",targetState="unexpected", cond=doswitchGuarded({! ( correct == true  
					) }) )
				}	 
				state("waitingForReply") { //this:State
					action { //it:State
						println("mover | waiting for reply...")
					}
					 transition(edgeName="t23",targetState="success",cond=whenReply("atcell"))
					transition(edgeName="t24",targetState="unexpected",cond=whenReply("walkbreak"))
				}	 
				state("success") { //this:State
					action { //it:State
						answer("moveTo", "done", "done($X,$Y)"   )  
						if( checkMsgContent( Term.createTerm("atcell(X,Y)"), Term.createTerm("atcell(X,Y)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("Mover replying to waiter...")
						}
					}
					 transition( edgeName="goto",targetState="waitingForCommand", cond=doswitch() )
				}	 
				state("unexpected") { //this:State
					action { //it:State
						println("There is something wrong ...")
						println("$name in ${currentState.stateName} | $currentMsg")
						 var F = -1  
						answer("moveTo", "done", "done($F,$F)"   )  
					}
					 transition( edgeName="goto",targetState="waitingForCommand", cond=doswitch() )
				}	 
				state("end") { //this:State
					action { //it:State
						println("mover | terminating...")
						terminate(1)
					}
				}	 
			}
		}
}
