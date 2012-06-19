package org.overture.interpreter.assistant.statement;

import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.Value;

public class ACaseAlternativeStmAssistantInterpreter
{

	public static Value eval(ACaseAlternativeStm node, Value val, Context ctxt) throws Throwable
	{
		Context evalContext = new Context(node.getLocation(), "case alternative", ctxt);

		try
		{
			evalContext.putList(PPatternAssistantInterpreter.getNamedValues(node.getPattern(),val, ctxt));
			return node.getResult().apply(VdmRuntime.getStatementEvaluator(),evalContext);
		}
		catch (PatternMatchException e)
		{
			// CasesStatement tries the others
		}

		return null;
	}

}
