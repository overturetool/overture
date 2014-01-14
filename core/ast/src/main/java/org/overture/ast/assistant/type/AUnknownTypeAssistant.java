package org.overture.ast.assistant.type;

import org.overture.ast.assistant.IAstAssistantFactory;

public class AUnknownTypeAssistant
{

	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public AUnknownTypeAssistant(IAstAssistantFactory af)
	{
		this.af = af;
	}

//	public static boolean isNumeric(AUnknownType type)
//	{
//		return true;
//	}

//	public static ARealNumericBasicType getNumeric(AUnknownType type)
//	{
//		return AstFactory.newARealNumericBasicType(type.getLocation());
//	}

}
