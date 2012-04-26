package org.overture.interpreter.definitions.assistant;

import java.util.List;

import org.overture.interpreter.ast.definitions.PDefinitionInterpreter;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class PDefinitionInterpreterAssistant {

	public static ValueList getValues(
			List<PDefinitionInterpreter> localDefs, ObjectContext ctxt) {
		
		ValueList list = new ValueList();

		for (PDefinitionInterpreter d: localDefs)
		{
			list.addAll(PDefinitionInterpreterAssistant.getValues(d, ctxt));
		}

		return list;
	}
	

	public static List<Value> getValues(
			PDefinitionInterpreter d, ObjectContext ctxt) {
		
		return new ValueList();
	}
}
