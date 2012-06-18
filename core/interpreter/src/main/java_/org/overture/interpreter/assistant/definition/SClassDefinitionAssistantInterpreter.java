package org.overture.interpreter.assistant.definition;

import java.util.Collection;
import java.util.HashMap;

import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.LexNameToken;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligation;
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
			v = publicStaticValues.get(local);

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

	public static Context getStatics(SClassDefinition classDef)
	{
		Context ctxt = new Context(classDef.getLocation(), "Statics", null);
		ctxt.putAll(publicStaticValues);
		ctxt.putAll(privateStaticValues);
		return ctxt;
	}

	public static ObjectValue newInstance(SClassDefinition classdef,
			Object object, Object object2, RootContext initialContext)
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

	public static Object makeNewInstance(ASystemClassDefinition systemClass,
			Object object, ValueList valueList, RootContext initialContext,
			HashMap<LexNameToken, ObjectValue> hashMap)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
