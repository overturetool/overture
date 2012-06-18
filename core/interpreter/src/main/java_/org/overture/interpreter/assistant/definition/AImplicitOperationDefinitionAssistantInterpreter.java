package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.OperationValue;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;

public class AImplicitOperationDefinitionAssistantInterpreter
{

	public static NameValuePairList getNamedValues(
			AImplicitOperationDefinition d, RootContext initialContext)
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

}
