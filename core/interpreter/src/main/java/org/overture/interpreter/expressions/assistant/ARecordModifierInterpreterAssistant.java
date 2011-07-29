package org.overture.interpreter.expressions.assistant;

import java.util.List;

import org.overture.interpreter.ast.expressions.ARecordModifierInterpreter;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.values.Value;

public class ARecordModifierInterpreterAssistant {
	
	public static List<Value> getValues(
			ARecordModifierInterpreter rm, ObjectContext ctxt) {
		return PExpInterpreterAssiatant.getValues(rm.getValue(), ctxt);
	}
}
