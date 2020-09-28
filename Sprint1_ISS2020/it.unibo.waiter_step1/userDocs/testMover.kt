import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.MsgUtil
import it.unibo.kactor.MqttUtils
import it.unibo.kactor.sysUtil
import java.util.regex.Pattern

class testMover {
	
	var mover            : ActorBasic? = null
	val initDelayTime     = 4000L

	
	fun assertPosition(state : String, x : String , y : String) {
		println("state is $state")
		var pos = Pattern.compile(",").split(state)
		var first = pos[0]
		var second = pos[1]
		println("test : x = $first , y = $second")
		assertTrue(first == x && second == y)
	}
	
	
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi
	@Before
	fun systemSetUp() {
		
   		kotlin.concurrent.thread(start = true) {
			it.unibo.ctxmover.main() 						// MainCtxTearoom()
			println("testMover systemSetUp done")
   		} 
	}	

	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi
	@After
	fun terminate() {
		println("testMover terminated!")
	}
	
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi
	fun test(){
		println(" --- testMover ---")
 		runBlocking{
			
			delay(15000)
 			while(mover == null){
				println("test | waiting for mover to be created...")
				delay(500)
				mover = sysUtil.getActor("mover")
			}
		//Virtual Robot start at Home position (0,0) 
		var state = mover!!.geResourceRep()
		assertPosition(state, "0", "0")
		
		//move to entrance (0,3)
		var request = MsgUtil.buildRequest( "test","moveTo", "moveTo(entrance)", mover!!.name  )
		mover!!.actor.send(request)
		delay(5000)
		state = mover!!.geResourceRep()
		assertPosition(state, "0", "3")
			
		//move to teatable 1 (1,1)
		request = MsgUtil.buildRequest( "test","moveTo", "moveTo(teatable1)", mover!!.name  )
		mover!!.actor.send(request)
		delay(5000)
		state = mover!!.geResourceRep()
		assertPosition(state, "1", "1")
				
		//move to teatable 2 (3,1)
		request = MsgUtil.buildRequest( "test","moveTo", "moveTo(teatable2)", mover!!.name  )
		mover!!.actor.send(request)
		delay(5000)
		state = mover!!.geResourceRep()
		assertPosition(state, "3", "1")
			
		//move to service desk (4,0)
		request = MsgUtil.buildRequest( "test","moveTo", "moveTo(servicedesk)", mover!!.name  )
		mover!!.actor.send(request)
		delay(5000)
		state = mover!!.geResourceRep()
		assertPosition(state, "4", "0")
			
		//move to exit (4,3)
		request = MsgUtil.buildRequest( "test","moveTo", "moveTo(exit)", mover!!.name  )
		mover!!.actor.send(request)
		delay(5000)
		state = mover!!.geResourceRep()
		assertPosition(state, "4", "3")
				
		}
}


				

		
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	@Test
	fun testMover(){
		
		test()

		println("testMover finished  ")
	}

}