package org.overture.interpreter.assistant.expression;

import java.util.List;

import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ACasesExpAssistantTC;

public class ACasesExpAssistantInterpreter extends ACasesExpAssistantTC
{

	public static ValueList getValues(ACasesExp exp, ObjectContext ctxt)
	{
		ValueList list = PExpAssistantInterpreter.getValues(exp.getExpression(),ctxt);

		for (ACaseAlternative c: exp.getCases())
		{
			list.addAll(ACaseAlternativeAssistantInterpreter.getValues(c,ctxt));
		}

		if (exp.getOthers() != null)
		{
			list.addAll(PExpAssistantInterpreter.getValues(exp.getOthers(), ctxt));
		}

		return list;
	}

	public static PExp findExpression(ACasesExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
		if (found != null) return found;

		found = PExpAssistantInterpreter.findExpression(exp.getExpression(), lineno);
		if (found != null) return found;

		for (ACaseAlternative c: exp.getCases())
		{
			found = PExpAssistantInterpreter.findExpression(c.getResult(),lineno);
			if (found != null) break;
		}

		return found != null ? found :
				exp.getOthers() != null ? PExpAssistantInterpreter.findExpression(exp.getOthers(),lineno) : null;
	}

	public static List<PExp> getSubExpressions(ACasesExp exp)
	{
		List<PExp> subs = PExpAssistantInterpreter.getSubExpressions(exp.getExpression());

		for (ACaseAlternative c: exp.getCases())
		{
			subs.addAll(ACaseAlternativeAssistantInterpreter.getSubExpressions(c));
		}

		if (exp.getOthers() != null)
		{
			subs.addAll(PExpAssistantInterpreter.getSubExpressions(exp.getOthers()));
		}

		subs.add(exp);
		return subs;
	}

}
