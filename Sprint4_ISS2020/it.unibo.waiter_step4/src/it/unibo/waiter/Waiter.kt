/* Generated by AN DISI Unibo */ 
package it.unibo.waiter

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Waiter ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 
				var MaxWaitTime = 0L
				val MaxStayTime = 20000L   	// 1min
				val IdleTime = 200L	
				val MaxCleanTime = 5000L			// 200 ms
				
				var ClientToConvoy = ""
				var DestTable = -1
				var CurDrink = ""
				var Price = 3
				
				var TimeToClean = MaxCleanTime
				var TimeCleanBegan = 0L
				var WasCleaning = false
				var TableToClean = 0
				
				val stopHandlingClientAtTable = mutableMapOf<Int, Boolean?>()
				var stopCheck = false
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("waiter | beep beep boop...START...")
						 stopHandlingClientAtTable.put(1, false)  
						 stopHandlingClientAtTable.put(2, false)  
						discardMessages = false
					}
					 transition( edgeName="goto",targetState="restingAtHome", cond=doswitch() )
				}	 
				state("restingAtHome") { //this:State
					action { //it:State
						println("waiter | Chilling out at home...")
						updateResourceRep("atPosition(0,0,home)" 
						)
					}
					 transition(edgeName="t010",targetState="handleEnterPhase1",cond=whenDispatch("enter"))
					transition(edgeName="t011",targetState="handleOrderFromClientPhase0",cond=whenDispatch("wantToOrder"))
					transition(edgeName="t012",targetState="serveTeaToClientPhase0",cond=whenDispatch("ready"))
					transition(edgeName="t013",targetState="handleTimeoutPhase0",cond=whenDispatch("timeout"))
					transition(edgeName="t014",targetState="handlePaymentPhase0",cond=whenDispatch("billPlease"))
					transition(edgeName="t015",targetState="cleanTeatablePhase1",cond=whenDispatch("cleanTable"))
				}	 
				state("doATask") { //this:State
					action { //it:State
						println("waiter | checking if there is a task to do...")
						stateTimer = TimerActor("timer_doATask", 
							scope, context!!, "local_tout_waiter_doATask", IdleTime )
					}
					 transition(edgeName="t016",targetState="goHome",cond=whenTimeout("local_tout_waiter_doATask"))   
					transition(edgeName="t017",targetState="handleEnterPhase1",cond=whenDispatch("enter"))
					transition(edgeName="t018",targetState="handleOrderFromClientPhase0",cond=whenDispatch("wantToOrder"))
					transition(edgeName="t019",targetState="serveTeaToClientPhase0",cond=whenDispatch("ready"))
					transition(edgeName="t020",targetState="handleTimeoutPhase0",cond=whenDispatch("timeout"))
					transition(edgeName="t021",targetState="handlePaymentPhase0",cond=whenDispatch("billPlease"))
					transition(edgeName="t022",targetState="cleanTeatablePhase1",cond=whenDispatch("cleanTable"))
				}	 
				state("goHome") { //this:State
					action { //it:State
						updateResourceRep("goingHome" 
						)
						request("moveTo", "moveTo(home)" ,"mover" )  
					}
					 transition(edgeName="t023",targetState="restingAtHome",cond=whenReply("done"))
				}	 
				state("handleEnterPhase1") { //this:State
					action { //it:State
						println("waiter | enter message arrived")
						if( checkMsgContent( Term.createTerm("enter(CLIENT_ID)"), Term.createTerm("enter(CLIENT_ID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  WasCleaning == true  
								 ){ var TempTime = 0L  
								TempTime = getCurrentTime()
								 TimeToClean = TimeToClean - (TempTime - TimeCleanBegan)  
								println("waiter | interrupted cleaning to handle enter request")
								}
								ClientToConvoy = payloadArg(0) 
								println("waiter | a client with client_id ${payloadArg(0)} asked to enter the safe tearoom...")
								println("waiter | checking if the safe tearoom has a free table ...")
								request("getRoomState", "getRoomState(getFreeTable,arg1)" ,"tearoom" )  
						}
					}
					 transition(edgeName="t024",targetState="handleEnterPhase2",cond=whenReply("state"))
				}	 
				state("handleEnterPhase2") { //this:State
					action { //it:State
						println("waiter | state response arrived")
						if( checkMsgContent( Term.createTerm("state(STATE)"), Term.createTerm("state(S)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if( payloadArg(0).toInt() == 0  
								 ){MaxWaitTime = 1L 
								println("waiter | But there is no free and clean teatable")
								}
								else
								 { 
								 					MaxWaitTime =  0L
								 					DestTable = payloadArg(0).toInt()
								 }
						}
						println("waiter | waittime: $MaxWaitTime")
					}
					 transition( edgeName="goto",targetState="convoyClientToTablePhase1", cond=doswitchGuarded({ MaxWaitTime == 0L  
					}) )
					transition( edgeName="goto",targetState="requestTableStates", cond=doswitchGuarded({! ( MaxWaitTime == 0L  
					) }) )
				}	 
				state("requestTableStates") { //this:State
					action { //it:State
						println("waiter | requesting teatable states to tearoom...")
						request("getRoomState", "getRoomState(getBusyAndDirtyTables,arg1)" ,"tearoom" )  
					}
					 transition(edgeName="t025",targetState="analyzeTableStates",cond=whenReply("numBusyAndDirty"))
				}	 
				state("analyzeTableStates") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("numBusyAndDirty(BUSY,DIRTY)"), Term.createTerm("numBusyAndDirty(B,D)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("waiter | tearoom replied with ${payloadArg(0)}-${payloadArg(1)} (busy-dirty)")
								
												val b=payloadArg(0).toInt()
												val d=payloadArg(1).toInt()
												var totTime = 0L
												if(d>0){
													totTime = MaxCleanTime
												}
												else{
													totTime = MaxStayTime
												}
												MaxWaitTime = totTime
								println("waiter | client has to wait at least $MaxWaitTime minutes")
								updateResourceRep("newClient($ClientToConvoy):clientWaiting:$ClientToConvoy:$MaxWaitTime" 
								)
								delay(800) 
						}
					}
					 transition( edgeName="goto",targetState="doATask", cond=doswitchGuarded({ WasCleaning == false  
					}) )
					transition( edgeName="goto",targetState="checkForOtherPrioritizedTasks", cond=doswitchGuarded({! ( WasCleaning == false  
					) }) )
				}	 
				state("convoyClientToTablePhase1") { //this:State
					action { //it:State
						updateResourceRep("reachingPosition(entrance)" 
						)
						println("waiter | reaching entrance door to convoy client $ClientToConvoy to teatable $DestTable ...")
						request("moveTo", "moveTo(entrance)" ,"mover" )  
					}
					 transition(edgeName="t026",targetState="convoyClientToTablePhase2",cond=whenReply("done"))
				}	 
				state("convoyClientToTablePhase2") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("done(X,Y)"), Term.createTerm("done(X,Y)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								updateResourceRep("atPosition(entrance,${payloadArg(0)},${payloadArg(1)}):clientEntering:$ClientToConvoy:$MaxWaitTime" 
								)
								delay(500) 
								updateResourceRep("convoyingClientToTable($DestTable,$ClientToConvoy)" 
								)
								println("waiter | convoying client $ClientToConvoy to teatable $DestTable ...")
								var Dest =  "teatable" + DestTable.toString()  
								request("moveTo", "moveTo($Dest)" ,"mover" )  
						}
					}
					 transition(edgeName="t027",targetState="convoyClientToTablePhase3",cond=whenReply("done"))
				}	 
				state("convoyClientToTablePhase3") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("done(X,Y)"), Term.createTerm("done(X,Y)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("waiter | Start mastertimer, update tearoom and telling client to sit")
								forward("updateState", "updateState(updateTableState,$DestTable,busy,$ClientToConvoy)" ,"tearoom" ) 
								updateResourceRep( "atPosition(teatable$DestTable,${payloadArg(0)},${payloadArg(1)}):clientSitting:$ClientToConvoy:$DestTable"  
								)
								 stopHandlingClientAtTable.put(DestTable, false)  
								delay(500) 
								forward("startTimer", "startTimer($DestTable,$MaxStayTime)" ,"mastertimer" ) 
						}
					}
					 transition( edgeName="goto",targetState="doATask", cond=doswitchGuarded({ WasCleaning == false  
					}) )
					transition( edgeName="goto",targetState="checkForOtherPrioritizedTasks", cond=doswitchGuarded({! ( WasCleaning == false  
					) }) )
				}	 
				state("cleanTeatablePhase1") { //this:State
					action { //it:State
						 var Dest=""  
						if( checkMsgContent( Term.createTerm("cleanTable(TEATABLE_ID)"), Term.createTerm("cleanTable(T)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("Waiter moving towards teatable to clean (${payloadArg(0)})")
								
											  Dest =  "teatable" + payloadArg(0) 
											  DestTable = payloadArg(0).toInt()
											  TableToClean = payloadArg(0).toInt()
						}
						if(  WasCleaning == true  
						 ){println("Waiter moving towards teable $TableToClean to resume cleaning")
						
									  Dest =  "teatable" + TableToClean
									  DestTable = TableToClean
						}
						updateResourceRep("cleaning($DestTable)" 
						)
						request("moveTo", "moveTo($Dest)" ,"mover" )  
					}
					 transition(edgeName="t028",targetState="cleanTeatablePhase2",cond=whenReply("done"))
				}	 
				state("cleanTeatablePhase2") { //this:State
					action { //it:State
						println("waiter | cleaning teatable $TableToClean")
						
									WasCleaning = true
						TimeCleanBegan = getCurrentTime()
						stateTimer = TimerActor("timer_cleanTeatablePhase2", 
							scope, context!!, "local_tout_waiter_cleanTeatablePhase2", TimeToClean )
					}
					 transition(edgeName="t129",targetState="finishedClean",cond=whenTimeout("local_tout_waiter_cleanTeatablePhase2"))   
					transition(edgeName="t130",targetState="serveTeaToClientPhase0",cond=whenDispatch("ready"))
					transition(edgeName="t131",targetState="handleEnterPhase1",cond=whenDispatch("enter"))
					transition(edgeName="t132",targetState="handleOrderFromClientPhase0",cond=whenDispatch("wantToOrder"))
					transition(edgeName="t133",targetState="handlePaymentPhase0",cond=whenDispatch("billPlease"))
				}	 
				state("finishedClean") { //this:State
					action { //it:State
						println("waiter | finished cleaning teatable $TableToClean")
						forward("updateState", "updateState(updateTableState,$TableToClean,clean,ARG3)" ,"tearoom" ) 
						 
									WasCleaning = false
									TimeToClean = 2000L
									TableToClean = 0
									TimeCleanBegan = 0L
					}
					 transition( edgeName="goto",targetState="doATask", cond=doswitch() )
				}	 
				state("checkForOtherPrioritizedTasks") { //this:State
					action { //it:State
						stateTimer = TimerActor("timer_checkForOtherPrioritizedTasks", 
							scope, context!!, "local_tout_waiter_checkForOtherPrioritizedTasks", IdleTime )
					}
					 transition(edgeName="t034",targetState="cleanTeatablePhase1",cond=whenTimeout("local_tout_waiter_checkForOtherPrioritizedTasks"))   
					transition(edgeName="t035",targetState="serveTeaToClientPhase1",cond=whenDispatch("ready"))
					transition(edgeName="t036",targetState="handleEnterPhase1",cond=whenDispatch("enter"))
					transition(edgeName="t037",targetState="handleOrderFromClientPhase1",cond=whenDispatch("wantToOrder"))
					transition(edgeName="t038",targetState="handlePaymentPhase1",cond=whenDispatch("billPlease"))
				}	 
				state("checkIfWasCleaning") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="cleanTeatablePhase1", cond=doswitchGuarded({ WasCleaning == true  
					}) )
					transition( edgeName="goto",targetState="doATask", cond=doswitchGuarded({! ( WasCleaning == true  
					) }) )
				}	 
				state("handleOrderFromClientPhase0") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("wantToOrder(TEATABLE_ID)"), Term.createTerm("wantToOrder(TEATABLE_ID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  WasCleaning == true  
								 ){ var TempTime = 0L  
								TempTime = getCurrentTime()
								 TimeToClean = TimeToClean - (TempTime - TimeCleanBegan)  
								println("waiter | interrupted cleaning to handle order request")
								}
								 var Teatable = payloadArg(0).toInt()  
								if(  stopHandlingClientAtTable.get(Teatable)!! == true  
								 ){println("waiter | ignoring wantToOrder message for table $Teatable since it's client timed-out...")
								 stopCheck = true  
								}
								else
								 {forward("stopTimer", "stopTimer(${payloadArg(0)})" ,"mastertimer" ) 
								 println("waiter | client at teatable $Teatable want to order! Reaching table $Teatable...")
								  stopCheck = false  
								 
								 						DestTable = Teatable
								 }
						}
					}
					 transition( edgeName="goto",targetState="handleOrderFromClientPhase1", cond=doswitchGuarded({ stopCheck == false  
					}) )
					transition( edgeName="goto",targetState="checkIfWasCleaning", cond=doswitchGuarded({! ( stopCheck == false  
					) }) )
				}	 
				state("handleOrderFromClientPhase1") { //this:State
					action { //it:State
						
										var Dest =  "teatable" + DestTable
						updateResourceRep("reachingPosition(teatable$DestTable)" 
						)
						request("moveTo", "moveTo($Dest)" ,"mover" )  
					}
					 transition(edgeName="t039",targetState="handleOrderFromClientPhase2",cond=whenReply("done"))
				}	 
				state("handleOrderFromClientPhase2") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("done(X,Y)"), Term.createTerm("done(X,Y)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("waiter | client can now order...")
								updateResourceRep( "atPosition(teatable$DestTable,${payloadArg(0)},${payloadArg(1)}):clientOrdering:$DestTable"  
								)
						}
					}
					 transition(edgeName="t040",targetState="handleOrderFromClientPhase3",cond=whenDispatch("tea"))
				}	 
				state("handleOrderFromClientPhase3") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("tea(TEATABLE_ID,TEA)"), Term.createTerm("tea(TABLE,TEA)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("waiter | client at teatable ${payloadArg(0)} ordered a ${payloadArg(1)}! Sending order to Barman...")
								forward("order", "order(${payloadArg(0)},${payloadArg(1)})" ,"barman" ) 
						}
					}
					 transition( edgeName="goto",targetState="checkForOtherPrioritizedTasks", cond=doswitchGuarded({ WasCleaning == true  
					}) )
					transition( edgeName="goto",targetState="doATask", cond=doswitchGuarded({! ( WasCleaning == true  
					) }) )
				}	 
				state("serveTeaToClientPhase0") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("ready(TEATABLE_ID,TEA)"), Term.createTerm("ready(T,D)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  WasCleaning == true  
								 ){ var TempTime = 0L  
								TempTime = getCurrentTime()
								 TimeToClean = TimeToClean - (TempTime - TimeCleanBegan)  
								println("waiter | interrupted cleaning to serve")
								}
								 var Teatable = payloadArg(0).toInt()  
								if(  stopHandlingClientAtTable.get(Teatable)!! == true  
								 ){println("waiter | ignoring ready message for table $Teatable since it's client is no more inside the tearoom...")
								 stopCheck = true  
								}
								else
								 { stopCheck = false  
								 
								 						DestTable = Teatable
								 						CurDrink = payloadArg(1)
								 }
						}
					}
					 transition( edgeName="goto",targetState="serveTeaToClientPhase1", cond=doswitchGuarded({ stopCheck == false  
					}) )
					transition( edgeName="goto",targetState="checkIfWasCleaning", cond=doswitchGuarded({! ( stopCheck == false  
					) }) )
				}	 
				state("serveTeaToClientPhase1") { //this:State
					action { //it:State
						updateResourceRep("reachingPosition(servicedesk)" 
						)
						println("waiter | order for table $DestTable ready! Reaching service desk...")
						request("moveTo", "moveTo(servicedesk)" ,"mover" )  
					}
					 transition(edgeName="t041",targetState="servTeaToClientPhase2",cond=whenReply("done"))
				}	 
				state("servTeaToClientPhase2") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("done(X,Y)"), Term.createTerm("done(X,Y)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								updateResourceRep("atPosition(servicedesk,${payloadArg(0)},${payloadArg(1)}):tea($CurDrink,$DestTable)" 
								)
								delay(500) 
								println("waiter | bringing tea to table $DestTable...")
								var Dest =  "teatable" + DestTable  
								updateResourceRep("reachingPosition(teatable$DestTable):servingTea($CurDrink)" 
								)
								request("moveTo", "moveTo($Dest)" ,"mover" )  
						}
					}
					 transition(edgeName="t042",targetState="servTeaToClientPhase3",cond=whenReply("done"))
				}	 
				state("servTeaToClientPhase3") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("done(X,Y)"), Term.createTerm("done(X,Y)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								updateResourceRep("atPosition(teatable$DestTable,${payloadArg(0)},${payloadArg(1)}):teaServed:$DestTable:$CurDrink" 
								)
								delay(1000) 
								println("Waiter resuming rimer for client...")
								forward("resumeTimer", "resumeTimer($DestTable)" ,"mastertimer" ) 
						}
					}
					 transition( edgeName="goto",targetState="checkForOtherPrioritizedTasks", cond=doswitchGuarded({ WasCleaning == true  
					}) )
					transition( edgeName="goto",targetState="doATask", cond=doswitchGuarded({! ( WasCleaning == true  
					) }) )
				}	 
				state("handlePaymentPhase0") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("billPlease(TABLE_ID)"), Term.createTerm("billPlease(T)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  WasCleaning == true  
								 ){ var TempTime = 0L  
								TempTime = getCurrentTime()
								 TimeToClean = TimeToClean - (TempTime - TimeCleanBegan)  
								println("waiter | interrupted cleaning to handle payment request")
								}
								 var Teatable = payloadArg(0).toInt()  
								if(  stopHandlingClientAtTable.get(Teatable)!! == true  
								 ){println("waiter | ignoring billPlease message for table $Teatable since it's client timed-out...")
								 stopCheck = true  
								}
								else
								 {println("waiter | Client at table $Teatable is ready to pay. Ending timer for table $Teatable")
								 forward("endTimer", "endTimer(${payloadArg(0)})" ,"mastertimer" ) 
								  stopHandlingClientAtTable.put(Teatable, true)  
								  stopCheck = false  
								 
								 						DestTable = Teatable
								 }
						}
					}
					 transition( edgeName="goto",targetState="handlePaymentPhase1", cond=doswitchGuarded({ stopCheck == false  
					}) )
					transition( edgeName="goto",targetState="checkIfWasCleaning", cond=doswitchGuarded({! ( stopCheck == false  
					) }) )
				}	 
				state("handlePaymentPhase1") { //this:State
					action { //it:State
						println("waiter | Reaching table ${payloadArg(0)} ...")
						updateResourceRep("reachingPosition(teatable$DestTable)" 
						)
						 var Dest = "teatable" + DestTable  
						request("moveTo", "moveTo($Dest)" ,"mover" )  
					}
					 transition(edgeName="t043",targetState="handlePaymentPhase2",cond=whenReply("done"))
				}	 
				state("handlePaymentPhase2") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("done(X,Y)"), Term.createTerm("done(X,Y)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("waiter | Asking Client at table $DestTable to pay ...")
								updateResourceRep("atPosition(teatable$DestTable,${payloadArg(0)},${payloadArg(1)}):clientPaying:$DestTable" 
								)
								delay(500) 
								request("getRoomState", "getRoomState(getClientFromTable,$DestTable)" ,"tearoom" )  
						}
					}
					 transition(edgeName="t044",targetState="convoyClientToExitPhase1",cond=whenReply("state"))
				}	 
				state("convoyClientToExitPhase1") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("state(STATE)"), Term.createTerm("state(C)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("waiter | Convoying Client ${payloadArg(0)} to exit door ...")
								 ClientToConvoy = payloadArg(0) 
								updateResourceRep("convoyingClientToExitDoor($ClientToConvoy)" 
								)
								request("moveTo", "moveTo(exit)" ,"mover" )  
						}
					}
					 transition(edgeName="t045",targetState="convoyClientToExitPhase2",cond=whenReply("done"))
				}	 
				state("convoyClientToExitPhase2") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("done(X,Y)"), Term.createTerm("done(X,Y)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("waiter | saying goodbye to client $ClientToConvoy...")
								updateResourceRep("atPosition(exit,${payloadArg(0)},${payloadArg(1)}):clientExiting:$ClientToConvoy" 
								)
								delay(500) 
								forward("updateState", "updateState(updateTableState,$DestTable,dirty,ARG)" ,"tearoom" ) 
								forward("cleanTable", "cleanTable($DestTable)" ,"waiter" ) 
						}
					}
					 transition( edgeName="goto",targetState="checkForOtherPrioritizedTasks", cond=doswitchGuarded({ WasCleaning == true  
					}) )
					transition( edgeName="goto",targetState="doATask", cond=doswitchGuarded({! ( WasCleaning == true  
					) }) )
				}	 
				state("handleTimeoutPhase0") { //this:State
					action { //it:State
						println("waiter | inside handleTimeoutPhase0")
						if( checkMsgContent( Term.createTerm("timeout(TEATABLE_ID)"), Term.createTerm("timeout(T)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("waiter | inside handleTimeoutPhase0 AND onMsg!")
								 var Teatable = payloadArg(0).toInt()  
								if(  stopHandlingClientAtTable.get(Teatable)!! == true  
								 ){println("waiter | ignoring timeout message for table $Teatable since it's client was already handled...")
								 stopCheck = true  
								}
								else
								 {println("waiter | Time exceeded for client at table $Teatable")
								 println("waiter | reaching table $Teatable...")
								  stopHandlingClientAtTable.put(Teatable, true)  
								  stopCheck = false  
								 
								 						DestTable = Teatable	
								 }
						}
					}
					 transition( edgeName="goto",targetState="handleTimeoutPhase1", cond=doswitchGuarded({stopCheck == false 
					}) )
					transition( edgeName="goto",targetState="doATask", cond=doswitchGuarded({! (stopCheck == false 
					) }) )
				}	 
				state("handleTimeoutPhase1") { //this:State
					action { //it:State
						updateResourceRep("reachingPosition(teatable$DestTable)" 
						)
						 var Dest = "teatable" + DestTable  
						request("moveTo", "moveTo($Dest)" ,"mover" )  
					}
					 transition(edgeName="t046",targetState="handleTimeoutPhase2",cond=whenReply("done"))
				}	 
				state("handleTimeoutPhase2") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("done(X,Y)"), Term.createTerm("done(X,Y)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								updateResourceRep("atPosition(teatable$DestTable,,${payloadArg(0)},${payloadArg(1)}):maxTimeExceeded:$DestTable" 
								)
								println("waiter | communicating to client at table ${payloadArg(0)} that he has to pay and leave...")
								delay(800) 
								request("getRoomState", "getRoomState(getClientFromTable,$DestTable)" ,"tearoom" )  
						}
					}
					 transition(edgeName="t047",targetState="convoyClientToExitPhase1",cond=whenReply("state"))
				}	 
			}
		}
}
