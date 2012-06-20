package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.statement.PStmAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.OperationValue;
import org.overture.typechecker.assistant.definition.AExplicitOperationDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;

public class AExplicitOperationDefinitionAssistantInterpreter extends AExplicitOperationDefinitionAssistantTC
{

	public static NameValuePairList getNamedValues(
			AExplicitOperationDefinition d, Context initialContext)
	{
		NameValuePairList nvl = new NameValuePairList();

		FunctionValue prefunc =
			(d.getPredef() == null) ? null : new FunctionValue(d.getPredef(), null, null, null);

		FunctionValue postfunc =
			(d.getPostdef() == null) ? null : new FunctionValue(d.getPostdef(), null, null, null);

		OperationValue op = new OperationValue(d, prefunc, postfunc, d.getState());
		op.isConstructor = d.getIsConstructor();
		op.isStatic = PAccessSpecifierAssistantTC.isStatic(d.getAccess());
		nvl.add(new NameValuePair(d.getName(), op));

		if (d.getPredef() != null)
		{
			prefunc.isStatic = PAccessSpecifierAssistantTC.isStatic(d.getAccess());
			nvl.add(new NameValuePair(d.getPredef().getName(), prefunc));
		}

		if (d.getPostdef() != null)
		{
			postfunc.isStatic = PAccessSpecifierAssistantTC.isStatic(d.getAccess());
			nvl.add(new NameValuePair(d.getPostdef().getName(), postfunc));
		}

		return nvl;
	}

	public static PExp findExpression(AExplicitOperationDefinition d, int lineno)
	{
		if (d.getPredef() != null)
		{
			PExp found = PDefinitionAssistantInterpreter.findExpression(d.getPredef(),lineno);
			if (found != null) return found;
		}

		if (d.getPostdef() != null)
		{
			PExp found = PDefinitionAssistantInterpreter.findExpression(d.getPostdef(),lineno);
			if (found != null) return found;
		}

		return PStmAssistantInterpreter.findExpression(d.getBody(),lineno);
	}

}
