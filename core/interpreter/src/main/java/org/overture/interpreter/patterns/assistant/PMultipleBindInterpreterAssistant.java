package org.overture.interpreter.patterns.assistant;

import java.util.List;

import org.overture.interpreter.ast.patterns.ASetMultipleBindInterpreter;
import org.overture.interpreter.ast.patterns.PMultipleBindInterpreter;
import org.overture.interpreter.expressions.assistant.PExpInterpreterAssiatant;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class PMultipleBindInterpreterAssistant {
	
	public static List<Value> getValues(
			PMultipleBindInterpreter mb, ObjectContext ctxt) {
		
		switch (mb.kindPMultipleBindInterpreter()) 
		{
			case SET:
				ASetMultipleBindInterpreter smb = (ASetMultipleBindInterpreter) mb;
				return PExpInterpreterAssiatant.getValues(smb.getSet(), ctxt);
			case TYPE:
				return new ValueList();
		}
		
		return null;
	}
}
