package it.unibo.waiter

import java.util.regex.Pattern

object WaiterUtils {
	
	fun getNumTables(msg : String) : Array<Int> {
		var resp = arrayOf(0,0)
		
		var temp = Pattern.compile("b").split(msg)   
	    temp = Pattern.compile("d").split(temp[1])
		
		resp[0]=temp[0].toInt()
		resp[1]=temp[1].toInt()
		
		System.out.println(""+resp[0]+" e "+resp[1])
		
		return resp
	}
}