package org.overture.codegen.utils;

import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class VdmTransUtil
{
	//TODO: Copied from UML2VDM. Factor out in assistant
	public static boolean isUnionOfQuotes(AUnionType type)
	{
		try
		{
			for (PType t : type.getTypes())
			{
				if (!PTypeAssistantTC.isType(t, AQuoteType.class))
				{
					return false;
				}
			}
		} catch (Error t)//Hack for stackoverflowError
		{
			return false;
		}

		return true;
	}
}
