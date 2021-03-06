/* Generated by AN DISI Unibo */ 
package it.unibo.tearoom

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Tearoom ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

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
						solve("consult('state.pl')","") //set resVar	
					}
					 transition( edgeName="goto",targetState="waitingForCommand", cond=doswitch() )
				}	 
				state("waitingForCommand") { //this:State
					action { //it:State
						println("tearoom | waiting for command...")
					}
					 transition(edgeName="t00",targetState="handleRequest",cond=whenRequest("getRoomState"))
					transition(edgeName="t01",targetState="handleUpdate",cond=whenDispatch("updateState"))
				}	 
				state("handleRequest") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("getRoomState(REQUEST,ARG1)"), Term.createTerm("getRoomState(getFreeTable,A)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var Table = 0  
								solve("teatable(T,clean)","") //set resVar	
								if( currentSolution.isSuccess() ) { Table = getCurSol("T").toString().toInt()  
								println("tearoom | table $Table is free")
								}
								else
								{}
								answer("getRoomState", "state", "state($Table)"   )  
						}
						if( checkMsgContent( Term.createTerm("getRoomState(REQUEST,ARG1)"), Term.createTerm("getRoomState(getNumFreeTables,A)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var Num = 0  
								solve("numfreetables(N)","") //set resVar	
								if( currentSolution.isSuccess() ) { Num = getCurSol("N").toString().toInt()  
								}
								else
								{}
								println("tearoom | number of free table: $Num")
								answer("getRoomState", "state", "state($Num)"   )  
						}
						if( checkMsgContent( Term.createTerm("getRoomState(REQUEST,ARG1)"), Term.createTerm("getRoomState(getTableFromClient,A)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 
												var Client_id = payloadArg(1)
												var ID = 0			  
								solve("bound(N,$Client_id)","") //set resVar	
								if( currentSolution.isSuccess() ) { ID = getCurSol("N").toString().toInt()  
								println("tearoom | client $Client_id is at table $ID")
								}
								else
								{}
								answer("getRoomState", "state", "state($ID)"   )  
						}
						if( checkMsgContent( Term.createTerm("getRoomState(REQUEST,ARG1)"), Term.createTerm("getRoomState(getClientFromTable,A)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("tearoom | INSIDE GET_CLIENT_FORM_TABLE")
								 
												var C = "NO"
												var ID = payloadArg(1).toInt()		  
								solve("bound($ID,N)","") //set resVar	
								if( currentSolution.isSuccess() ) { C = getCurSol("N").toString()  
								}
								else
								{}
								println("tearoom | table $ID is used by client $C")
								answer("getRoomState", "state", "state($C)"   )  
						}
					}
					 transition( edgeName="goto",targetState="waitingForCommand", cond=doswitch() )
				}	 
				state("handleUpdate") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("updateState(UPDATE_REQUEST,ARG1,ARG2,ARG3)"), Term.createTerm("updateState(updateTableState,ID,clean,A)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var T =  payloadArg(1).toInt()  
								solve("setCleanTable($T)","") //set resVar	
								println("tearoom | update State: table $T is now clean")
						}
						if( checkMsgContent( Term.createTerm("updateState(UPDATE_REQUEST,ARG1,ARG2,ARG3)"), Term.createTerm("updateState(updateTableState,ID,dirty,A)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 
												var T =  payloadArg(1).toInt() 
												var C = "NO"
								solve("setDirtyTable($T)","") //set resVar	
								solve("assign($T,$C)","") //set resVar	
								println("tearoom | update State: table $T is now dirty")
						}
						if( checkMsgContent( Term.createTerm("updateState(UPDATE_REQUEST,ARG1,ARG2,ARG3)"), Term.createTerm("updateState(updateTableState,ID,busy,A)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 
												var T =  payloadArg(1).toInt() 
												var C = payloadArg(3)
								solve("setBusyTable($T)","") //set resVar	
								solve("assign($T,$C)","") //set resVar	
								println("tearoom | update State: table $T is now busy and assigned to client $C")
						}
					}
					 transition( edgeName="goto",targetState="waitingForCommand", cond=doswitch() )
				}	 
			}
		}
}
