package connQak

import org.eclipse.californium.core.CoapClient
import it.unibo.kactor.ApplMessage
import org.eclipse.californium.core.coap.MediaTypeRegistry
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.CoapHandler

class ConnQakCoapMine {
	
	 var client   : CoapClient = CoapClient()
	
	fun createConnection(hostA : String, hostP : String, ctxDest : String, dest : String ):String{
 			val url = "coap://$hostA:$hostP/$ctxDest/$dest"
 			System.out.println("connQakCoap | url=${url.toString()}")
 			//uriStr: coap://192.168.1.22:8060/ctxdomains/waiter
			//client = CoapClient(  )
		    client.uri = url.toString()
			client.setTimeout( 1000L )
 			//initialCmd: to make console more reactive at the first user cmd
 		    val respGet  = client.get( ) //CoapResponse
 		    var resp = ""
			if( respGet != null ){
				resp=respGet.getResponseText()
				System.out.println("connQakCoap | createConnection doing  get | CODE=  ${respGet.code} content=$resp")
			}	
			else{
				resp="CONNECTION ERROR"
				System.out.println("connQakCoap | url=  ${url} FAILURE")
			}
		return resp
	}
	
	 fun forward( msg: ApplMessage ){		
        System.out.println("connQakCoap | PUT forward ${msg}  ")		
        val respPut = client.put(msg.toString(), MediaTypeRegistry.TEXT_PLAIN)
        System.out.println("connQakCoap | RESPONSE CODE=  ${respPut.code}")		
	}
	
	 fun request( msg: ApplMessage ) : String{
 		val respPut = client.put(msg.toString(), MediaTypeRegistry.TEXT_PLAIN)
		if( respPut != null ){
			System.out.println("connQakCoap | answer= ${respPut.getResponseText()}")
			return respPut.getResponseText()
		}
		else
			return ""			
	}
	
	 fun emit( msg: ApplMessage){	
         val respPut = client.put(msg.toString(), MediaTypeRegistry.TEXT_PLAIN)
         System.out.println("connQakCoap | PUT emit ${msg} RESPONSE CODE=  ${respPut.code}")		
		
	}
	
	 fun readRep(   ) : String{
		val respGet : CoapResponse = client.get( )
		return respGet.getResponseText()
	}
	
}