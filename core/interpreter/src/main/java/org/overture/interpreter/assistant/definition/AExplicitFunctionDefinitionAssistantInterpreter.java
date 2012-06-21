package org.overture.interpreter.assistant.definition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.ast.types.PType;
import org.overture.config.Settings;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.typechecker.assistant.definition.AExplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;

public class AExplicitFunctionDefinitionAssistantInterpreter extends AExplicitFunctionDefinitionAssistantTC
{

	public static NameValuePairList getNamedValues(
			AExplicitFunctionDefinition d, Context initialContext)
	{
		NameValuePairList nvl = new NameValuePairList();
		Context free = initialContext.getVisibleVariables();

		FunctionValue prefunc =
			(d.getPredef() == null) ? null : new FunctionValue(d.getPredef(), null, null, free);

		FunctionValue postfunc =
			(d.getPostdef() == null) ? null : new FunctionValue(d.getPostdef(), null, null, free);

		FunctionValue func = new FunctionValue(d, prefunc, postfunc, free);
		func.isStatic = PAccessSpecifierAssistantTC.isStatic(d.getAccess());
		func.uninstantiated = (!d.getTypeParams().isEmpty());
		nvl.add(new NameValuePair(d.getName(), func));

		if (d.getPredef() != null)
		{
			nvl.add(new NameValuePair(d.getPredef().getName(), prefunc));
			prefunc.uninstantiated = (!d.getTypeParams().isEmpty());
		}

		if (d.getPostdef() != null)
		{
			nvl.add(new NameValuePair(d.getPostdef().getName(), postfunc));
			postfunc.uninstantiated = (!d.getTypeParams().isEmpty());
		}

		if (Settings.dialect == Dialect.VDM_SL)
		{
			// This is needed for recursive local functions
			free.putList(nvl);
		}

		return nvl;
	}

	public static FunctionValue getPolymorphicValue(
			AExplicitFunctionDefinition expdef, PTypeList actualTypes)
	{
		Map<List<PType>, FunctionValue> polyfuncs = VdmRuntime.getNodeState(expdef).polyfuncs;
		
		if (polyfuncs == null)
		{
			polyfuncs = new HashMap<List<PType>, FunctionValue>();
		}
		else
		{
			// We always return the same function value for a polymorph
			// with a given set of types. This is so that the one function
			// value can record measure counts for recursive polymorphic
			// functions.
			
			FunctionValue rv = polyfuncs.get(actualTypes);
			
			if (rv != null)
			{
				return rv;
			}
		}
		
		FunctionValue prefv = null;
		FunctionValue postfv = null;

		if (expdef.getPredef() != null)
		{
			prefv = getPolymorphicValue(expdef.getPredef(),actualTypes);
		}
		else
		{
			prefv = null;
		}

		if (expdef.getPostdef() != null)
		{
			postfv = getPolymorphicValue(expdef.getPostdef(),actualTypes);
		}
		else
		{
			postfv = null;
		}

		FunctionValue rv = new FunctionValue(
				expdef, actualTypes, prefv, postfv, null);

		polyfuncs.put(actualTypes, rv);
		return rv;
	}

	public static PExp findExpression(AExplicitFunctionDefinition d, int lineno)
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

		return PExpAssistantInterpreter.findExpression(d.getBody(),lineno);
	}

}
