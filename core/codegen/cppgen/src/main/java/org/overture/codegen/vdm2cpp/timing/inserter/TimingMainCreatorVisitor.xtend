package org.overture.codegen.vdm2cpp.timing.inserter

import java.util.HashMap

class TimingMainCreator {
	
	new() {
		
	}
	
	static def generateMainMethod(HashMap<Long,String> arg)
	'''
	timing::TimedProgram __pp__({
		«FOR a: arg.keySet SEPARATOR ','»
		std::pair<int,std::string>{«a»,"«arg.get(a)»"}
		«ENDFOR»
	});
	'''
	
}