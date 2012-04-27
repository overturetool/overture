package org.overture.interpreter.definitions.assistant;

import org.overture.interpreter.ast.definitions.SClassDefinitionInterpreter;
import org.overturetool.interpreter.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.values.Value;

public class SClassDefinitionInterpreterAssistant
{

	public static boolean hasDelegate(SClassDefinitionInterpreter classdef)
	{
		return classdef.getDelegate().hasDelegate();
	}

	public static Value invokeDelegate(SClassDefinitionInterpreter classdef,
			Object delegateObject, Context ctxt)
	{
		return classdef.getDelegate().invokeDelegate(delegateObject, ctxt);
	}

	public static Object newInstance(SClassDefinitionInterpreter classdef)
	{
		return classdef.getDelegate().newInstance();
	}

	public static Value getStatic(SClassDefinitionInterpreter classdef,
			LexNameToken sought)
	{
		LexNameToken local = (sought.explicit) ? sought
				: sought.getModifiedName(classdef.getName().name);

		Value v = classdef.getprivateStaticValues.get(local);

		if (v == null)
		{
			v = publicStaticValues.get(local);

			if (v == null)
			{
				for (ClassDefinition sdef : superdefs)
				{
					v = sdef.getStatic(local);

					if (v != null)
					{
						break;
					}
				}
			}
		}

		return v;
	}

}
