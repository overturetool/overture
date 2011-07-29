package org.overture.interpreter.patterns.assistant;

import java.util.List;

import org.overture.interpreter.ast.patterns.ASetBindInterpreter;
import org.overture.interpreter.ast.patterns.PBindInterpreter;
import org.overture.interpreter.expressions.assistant.PExpInterpreterAssiatant;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class PBindInterpreterAssistant {

	public static List<Value> getValues(PBindInterpreter bind, 
			ObjectContext ctxt) {
		
		switch (bind.kindPBindInterpreter()) {

		case SET:
			ASetBindInterpreter sb = (ASetBindInterpreter) bind;
			return PExpInterpreterAssiatant.getValues(sb.getSet(), ctxt);
		case TYPE:
			return new ValueList();
		}
		
		return null;
	}
	
}
