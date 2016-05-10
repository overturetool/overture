package org.overture.interpreter.assistant.definition;

import java.util.HashMap;
import java.util.List;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.types.PType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.state.AExplicitFunctionDefinitionRuntimeState;
import org.overture.interpreter.values.FunctionValue;
import org.overture.typechecker.assistant.definition.AExplicitFunctionDefinitionAssistantTC;

public class AExplicitFunctionDefinitionAssistantInterpreter extends
		AExplicitFunctionDefinitionAssistantTC implements IAstAssistant
{

	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AExplicitFunctionDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public FunctionValue getPolymorphicValue(IInterpreterAssistantFactory af,
			AExplicitFunctionDefinition expdef, PTypeList actualTypes)
	{
		AExplicitFunctionDefinitionRuntimeState state = VdmRuntime.getNodeState(expdef);

		if (state.polyfuncs == null)
		{
			state.polyfuncs = new HashMap<>();
		} else
		{
			// We always return the same function value for a polymorph
			// with a given set of types. This is so that the one function
			// value can record measure counts for recursive polymorphic
			// functions.

			FunctionValue rv = state.polyfuncs.get(actualTypes);

			if (rv != null)
			{
				return rv;
			}
		}

		FunctionValue prefv = null;
		FunctionValue postfv = null;

		if (expdef.getPredef() != null)
		{
			prefv = getPolymorphicValue(af, expdef.getPredef(), actualTypes);
		} else
		{
			prefv = null;
		}

		if (expdef.getPostdef() != null)
		{
			postfv = getPolymorphicValue(af, expdef.getPostdef(), actualTypes);
		} else
		{
			postfv = null;
		}

		FunctionValue rv = new FunctionValue(af, expdef, actualTypes, prefv, postfv, null);

		state.polyfuncs.put(actualTypes, rv);
		return rv;
	}

}
