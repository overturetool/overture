package org.overture.interpreter.expressions.assistant;

import java.util.List;

import org.overture.interpreter.ast.expressions.ACaseAlternativeInterpreter;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.values.Value;

public class ACaseAlternativeInterpreterAssistant {
	public static List<Value> getValues(
			ACaseAlternativeInterpreter c, ObjectContext ctxt) {
		
		return PExpInterpreterAssiatant.getValues(c.getResult(), ctxt);
	}	
}
