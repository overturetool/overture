package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.LexNameToken;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;

public class SClassDefinitionAssistantInterpreter extends SClassDefinitionAssistantTC
{

	public static Value getStatic(SClassDefinition classdef, LexNameToken sought)
	{
		LexNameToken local = (sought.explicit) ? sought
				: sought.getModifiedName(classdef.getName().name);

		Value v = privateStaticValues.get(local);

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

	public static NameValuePairList getStatics(SClassDefinition classdef)
	{
		// TODO Auto-generated method stub
		assert false : "not implemented";
		return null;
	}

}
