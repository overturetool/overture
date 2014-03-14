package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.statement.PStmAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.OperationValue;
import org.overture.typechecker.assistant.definition.AExplicitOperationDefinitionAssistantTC;

public class AExplicitOperationDefinitionAssistantInterpreter extends
		AExplicitOperationDefinitionAssistantTC
{

	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AExplicitOperationDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static NameValuePairList getNamedValues(
			AExplicitOperationDefinition d, Context initialContext)
	{
		NameValuePairList nvl = new NameValuePairList();

		FunctionValue prefunc = d.getPredef() == null ? null
				: new FunctionValue(d.getPredef(), null, null, null);

		FunctionValue postfunc = d.getPostdef() == null ? null
				: new FunctionValue(d.getPostdef(), null, null, null);

		OperationValue op = new OperationValue(d, prefunc, postfunc, d.getState(), af);
		op.isConstructor = d.getIsConstructor();
		op.isStatic = af.createPAccessSpecifierAssistant().isStatic(d.getAccess());
		nvl.add(new NameValuePair(d.getName(), op));

		if (d.getPredef() != null)
		{
			prefunc.isStatic = af.createPAccessSpecifierAssistant().isStatic(d.getAccess());
			nvl.add(new NameValuePair(d.getPredef().getName(), prefunc));
		}

		if (d.getPostdef() != null)
		{
			postfunc.isStatic = af.createPAccessSpecifierAssistant().isStatic(d.getAccess());
			nvl.add(new NameValuePair(d.getPostdef().getName(), postfunc));
		}

		return nvl;
	}

	public static PExp findExpression(AExplicitOperationDefinition d, int lineno)
	{
		if (d.getPredef() != null)
		{
			PExp found = PDefinitionAssistantInterpreter.findExpression(d.getPredef(), lineno);
			if (found != null)
			{
				return found;
			}
		}

		if (d.getPostdef() != null)
		{
			PExp found = PDefinitionAssistantInterpreter.findExpression(d.getPostdef(), lineno);
			if (found != null)
			{
				return found;
			}
		}

		return PStmAssistantInterpreter.findExpression(d.getBody(), lineno);
	}

	public static PStm findStatement(AExplicitOperationDefinition d, int lineno)
	{
		return PStmAssistantInterpreter.findStatement(d.getBody(), lineno);
	}

}
