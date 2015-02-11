package org.overture.codegen.vdm2cpp.timing.inserter

import java.util.HashMap
import java.io.StringWriter

class TimingMainCreator {
	
	new() {
		
	}
	
	def generateMainMethod(HashMap<Long,String> arg)
	{
		var sw = new StringWriter();
		for(a : arg.keySet)
		{
			sw.append('''timing::Timing::get_instance()->set_name_for(«a»,"«arg.get(a)»");
			''')
		}
		
		return sw.toString()
	}
	
}