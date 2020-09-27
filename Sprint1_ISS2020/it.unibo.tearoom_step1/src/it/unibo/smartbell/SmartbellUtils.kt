package it.unibo.smartbell

import java.util.UUID

object SmartbellUtils {
	
	fun getClientID() : String {
		return "c" + UUID.randomUUID().toString().replace("-","")
		//return "id" + UUID.randomUUID().variant().toString()
	}
	
	fun getTemperature() : Double {
		return ( 36.0 + ( 2.0*Math.random() ) )
	}
}