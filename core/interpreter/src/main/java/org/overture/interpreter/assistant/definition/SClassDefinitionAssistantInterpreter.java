package org.overture.interpreter.assistant.definition;

import java.util.HashMap;

import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.LexNameToken;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;

public class SClassDefinitionAssistantInterpreter extends SClassDefinitionAssistantTC
{

	public static Value getStatic(SClassDefinition classdef, LexNameToken sought)
	{
		LexNameToken local = (sought.explicit) ? sought
				: sought.getModifiedName(classdef.getName().name);

		
		
		
		Value v = VdmRuntime.getNodeState(classdef).privateStaticValues.get(local);

		if (v == null)
		{
			v = VdmRuntime.getNodeState(classdef).publicStaticValues.get(local);

			if (v == null)
			{
				for (SClassDefinition sdef : classdef.getSuperDefs())
				{
					v = getStatic(sdef,local);

					if (v != null)
					{
						break;
					}
				}
			}
		}

		return v;
	}

	public static Context getStatics(SClassDefinition classdef)
	{
		Context ctxt = new Context(classdef.getLocation(), "Statics", null);
		ctxt.putAll(VdmRuntime.getNodeState(classdef).publicStaticValues);
		ctxt.putAll(VdmRuntime.getNodeState(classdef).privateStaticValues);
		return ctxt;
	}

	public static ObjectValue newInstance(SClassDefinition classdef,
			Object object, Object object2, Context ctxt)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static void staticInit(SClassDefinition cdef,
			StateContext globalContext)
	{
		// TODO Auto-generated method stub
		
	}

	public static ProofObligationList getProofObligations(
			SClassDefinition c, POContextStack ctxt)
	{
		return PDefinitionListAssistantInterpreter.getProofObligations(c.getDefinitions(), ctxt);
	}

	public static void staticValuesInit(SClassDefinition cdef,
			StateContext globalContext)
	{
		// TODO Auto-generated method stub
		
	}

	public static boolean hasDelegate(SClassDefinition classdef)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public static Object newInstance(SClassDefinition classdef)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static Value invokeDelegate(SClassDefinition classdef,
			Object delegateObject, Context ctxt)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static ObjectValue makeNewInstance(ASystemClassDefinition systemClass,
			Object object, ValueList valueList, RootContext initialContext,
			HashMap<LexNameToken, ObjectValue> hashMap)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static PExp findExpression(SClassDefinition d, int lineno)
	{
		return PDefinitionListAssistantInterpreter.findExpression(d.getDefinitions(), lineno);
	}

	public static boolean isTypeDefinition(SClassDefinition def)
	{
		return true;
	}

}
