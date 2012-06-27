package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AErrorCase;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.statement.PStmAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.OperationValue;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;

public class AImplicitOperationDefinitionAssistantInterpreter
{

	public static NameValuePairList getNamedValues(
			AImplicitOperationDefinition d, Context initialContext)
	{
		NameValuePairList nvl = new NameValuePairList();

		FunctionValue prefunc =
			(d.getPredef() == null) ? null : new FunctionValue(d.getPredef(), null, null, null);

		FunctionValue postfunc =
			(d.getPostdef() == null) ? null : new FunctionValue(d.getPostdef(), null, null, null);

		// Note, body may be null if it is really implicit. This is caught
		// when the function is invoked. The value is needed to implement
		// the pre_() expression for implicit functions.

		OperationValue op =	new OperationValue(d, prefunc, postfunc, d.getState());
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

	public static PExp findExpression(AImplicitOperationDefinition d, int lineno)
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
		
		if (d.getErrors() != null)
		{
			for (AErrorCase err: d.getErrors())
			{
				PExp found = AErrorCaseAssistantIntepreter.findExpression(err,lineno);
				if (found != null) return found;
			}
		}

		return d.getBody() == null ? null : PStmAssistantInterpreter.findExpression(d.getBody(),lineno);
	}

	public static PStm findStatement(AImplicitOperationDefinition d, int lineno)
	{
		return d.getBody() == null ? null : PStmAssistantInterpreter.findStatement(d.getBody(),lineno);
	}

}
