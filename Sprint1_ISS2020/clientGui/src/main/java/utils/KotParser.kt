package utils

import java.util.regex.Pattern



object KotParser {
	
	@JvmStatic
	fun getMessageArg(msg : String, msgName : String):String{
		
		var pt = Pattern.compile(msgName)
		var temp = pt.split(msg)
		
//		for(t in temp){
//			System.out.println(t)
//		}
		
		var i = temp.size - 1
				
		pt = Pattern.compile("(\\(|\\))")
		temp=pt.split(temp[i])
		
//		for(t in temp){
//			System.out.println(t)
//		}
//		
//		System.out.println(temp.size)
		return temp[1]
	}
	
	@JvmStatic
	fun getPayloadElements(p : String): Array<String>{
		var pt = Pattern.compile(",")
		var res = pt.split(p)
		
		return res
	}
}
