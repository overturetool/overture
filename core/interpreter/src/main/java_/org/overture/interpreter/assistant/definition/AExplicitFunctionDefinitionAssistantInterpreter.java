package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.typechecker.assistant.definition.AExplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;

public class AExplicitFunctionDefinitionAssistantInterpreter extends AExplicitFunctionDefinitionAssistantTC
{

	public static NameValuePairList getNamedValues(
			AExplicitFunctionDefinition d, RootContext ctxt)
	{
		NameValuePairList nvl = new NameValuePairList();
		Context free = ctxt.getVisibleVariables();

		FunctionValue prefunc =
			(d.getPredef() == null) ? null : new FunctionValue(d.getPredef(), null, null, free);

		FunctionValue postfunc =
			(d.getPostdef() == null) ? null : new FunctionValue(d.getPostdef(), null, null, free);

		FunctionValue func = new FunctionValue(d, prefunc, postfunc, free);
		func.isStatic = PAccessSpecifierAssistantTC.isStatic(d.getAccess());
		func.uninstantiated = (d.getTypeParams() != null);
		nvl.add(new NameValuePair(d.getName(), func));

		if (d.getPredef() != null)
		{
			nvl.add(new NameValuePair(d.getPredef().getName(), prefunc));
			prefunc.uninstantiated = (d.getTypeParams() != null);
		}

		if (d.getPostdef() != null)
		{
			nvl.add(new NameValuePair(d.getPostdef().getName(), postfunc));
			postfunc.uninstantiated = (d.getTypeParams() != null);
		}

		if (Settings.dialect == Dialect.VDM_SL)
		{
			// This is needed for recursive local functions
			free.putList(nvl);
		}

		return nvl;
	}

}
