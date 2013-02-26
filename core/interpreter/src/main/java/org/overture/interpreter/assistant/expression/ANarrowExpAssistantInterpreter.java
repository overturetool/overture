package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ANarrowExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ANarrowExpAssistantTC;

public class ANarrowExpAssistantInterpreter extends ANarrowExpAssistantTC {
	
	public static ValueList getValues(ANarrowExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getTest(), ctxt);
	}
	
	public static PExp findExpression(ANarrowExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpression(exp, lineno);
		
		if(found != null)
			return found;
		
		return PExpAssistantInterpreter.findExpression(exp.getTest(), lineno);                                                                   
	}

}
