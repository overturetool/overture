package org.overture.interpreter.assistant.definition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.types.PType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.FunctionValue;
import org.overture.typechecker.assistant.definition.AImplicitFunctionDefinitionAssistantTC;

public class AImplicitFunctionDefinitionAssistantInterpreter extends
		AImplicitFunctionDefinitionAssistantTC implements IAstAssistant
{

	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AImplicitFunctionDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	//FIXME: only used one. Mainline it.
	public FunctionValue getPolymorphicValue(IInterpreterAssistantFactory af,
			AImplicitFunctionDefinition impdef, PTypeList actualTypes)
	{

		Map<List<PType>, FunctionValue> polyfuncs = VdmRuntime.getNodeState(impdef).polyfuncs;

		if (polyfuncs == null)
		{
			polyfuncs = new HashMap<List<PType>, FunctionValue>();
		} else
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

		if (impdef.getPredef() != null)
		{
			prefv = af.createAExplicitFunctionDefinitionAssistant().getPolymorphicValue(af, impdef.getPredef(), actualTypes);
		} else
		{
			prefv = null;
		}

		if (impdef.getPostdef() != null)
		{
			postfv = af.createAExplicitFunctionDefinitionAssistant().getPolymorphicValue(af, impdef.getPostdef(), actualTypes);
		} else
		{
			postfv = null;
		}

		FunctionValue rv = new FunctionValue(af, impdef, actualTypes, prefv, postfv, null);

		polyfuncs.put(actualTypes, rv);
		return rv;
	}

}
