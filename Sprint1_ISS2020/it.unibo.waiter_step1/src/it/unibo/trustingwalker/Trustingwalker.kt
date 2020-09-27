/* Generated by AN DISI Unibo */ 
package it.unibo.trustingwalker

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Trustingwalker ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
			var XT = "0"
			var YT = "0"
			var CurrentPlannedMove = ""
			var StepTime    	   = 450L
			val BackTime           = 2 * StepTime / 3
			var obstacleFound      = false  
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
					}
					 transition(edgeName="t00",targetState="start",cond=whenEvent("brstarted"))
				}	 
				state("start") { //this:State
					action { //it:State
						forward("cmd", "cmd(l)" ,"basicrobot" ) 
						delay(500) 
						forward("cmd", "cmd(r)" ,"basicrobot" ) 
						delay(500) 
						emit("twstarted", "twstarted(0)" ) 
						println("&&&  trustingwalker STARTS")
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("waitCmd") { //this:State
					action { //it:State
						println("&&&  trustingwalker waits for a command 'movetoCell'")
					}
					 transition(edgeName="t01",targetState="walk",cond=whenRequest("movetoCell"))
				}	 
				state("walk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("movetoCell(X,Y)"), Term.createTerm("movetoCell(X,Y)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 XT = payloadArg(0)
											   YT = payloadArg(1)			  
								println("&&&  trustingwalker  | MOVING to ($XT,$YT)")
								itunibo.planner.plannerUtil.planForGoal( "$XT", "$YT"  )
						}
					}
					 transition( edgeName="goto",targetState="execPlannedMoves", cond=doswitchGuarded({ itunibo.planner.plannerUtil.existActions()  
					}) )
					transition( edgeName="goto",targetState="noPlan", cond=doswitchGuarded({! ( itunibo.planner.plannerUtil.existActions()  
					) }) )
				}	 
				state("noPlan") { //this:State
					action { //it:State
						println("&&&  trustingwalker | NO PLAN FOUND for MOVING to ($XT,$YT)")
						answer("movetoCell", "walkbreak", "walkbreak($XT,$YT)"   )  
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("execPlannedMoves") { //this:State
					action { //it:State
						  CurrentPlannedMove = itunibo.planner.plannerUtil.getNextPlannedMove()  
					}
					 transition( edgeName="goto",targetState="wMove", cond=doswitchGuarded({ CurrentPlannedMove == "w"  
					}) )
					transition( edgeName="goto",targetState="otherPlannedMove", cond=doswitchGuarded({! ( CurrentPlannedMove == "w"  
					) }) )
				}	 
				state("wMove") { //this:State
					action { //it:State
						request("step", "step($StepTime)" ,"basicrobot" )  
					}
					 transition(edgeName="t02",targetState="stepDone",cond=whenReply("stepdone"))
					transition(edgeName="t03",targetState="stepFailed",cond=whenReply("stepfail"))
				}	 
				state("stepDone") { //this:State
					action { //it:State
						updateResourceRep( itunibo.planner.plannerUtil.getMapOneLine()  
						)
						itunibo.planner.plannerUtil.updateMap( "w"  )
					}
					 transition( edgeName="goto",targetState="execPlannedMoves", cond=doswitchGuarded({ CurrentPlannedMove.length > 0  
					}) )
					transition( edgeName="goto",targetState="sendSuccessAnswer", cond=doswitchGuarded({! ( CurrentPlannedMove.length > 0  
					) }) )
				}	 
				state("stepFailed") { //this:State
					action { //it:State
						 obstacleFound = true  
						println("trustingwalker | stepFailed")
						if( checkMsgContent( Term.createTerm("stepfail(DURATION,CAUSE)"), Term.createTerm("stepfail(DURATION,CAUSE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 val D = payloadArg(0).toLong()  ; val Dt = Math.abs(StepTime-D); val BackT = D/2  
								println("trustingwalker stepFail D= $D, BackTime = ${BackTime}")
								if(  D > BackTime  
								 ){forward("cmd", "cmd(s)" ,"basicrobot" ) 
								delay(BackT)
								forward("cmd", "cmd(h)" ,"basicrobot" ) 
								}
						}
						itunibo.planner.plannerUtil.updateMapObstacleOnCurrentDirection(  )
					}
					 transition( edgeName="goto",targetState="sendFailureAnswer", cond=doswitch() )
				}	 
				state("otherPlannedMove") { //this:State
					action { //it:State
						if(  CurrentPlannedMove == "l" || CurrentPlannedMove == "r"   
						 ){forward("cmd", "cmd($CurrentPlannedMove)" ,"basicrobot" ) 
						itunibo.planner.plannerUtil.updateMap( "$CurrentPlannedMove"  )
						}
					}
					 transition( edgeName="goto",targetState="execPlannedMoves", cond=doswitchGuarded({ CurrentPlannedMove.length > 0  
					}) )
					transition( edgeName="goto",targetState="sendSuccessAnswer", cond=doswitchGuarded({! ( CurrentPlannedMove.length > 0  
					) }) )
				}	 
				state("sendSuccessAnswer") { //this:State
					action { //it:State
						println("&&&  trustingwalker POINT ($XT,$YT) REACHED")
						itunibo.planner.plannerUtil.showCurrentRobotState(  )
						answer("movetoCell", "atcell", "atcell($XT,$YT)"   )  
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("sendFailureAnswer") { //this:State
					action { //it:State
						println("&&&  trustingwalker FAILS")
						 val Curx = itunibo.planner.plannerUtil.getPosX()
							       val Cury = itunibo.planner.plannerUtil.getPosY()	
						itunibo.planner.plannerUtil.showCurrentRobotState(  )
						answer("movetoCell", "walkbreak", "walkbreak($Curx,$Cury)"   )  
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
			}
		}
}
