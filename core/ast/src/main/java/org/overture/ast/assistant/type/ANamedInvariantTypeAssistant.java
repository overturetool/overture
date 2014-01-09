package org.overture.ast.assistant.type;

import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.SNumericBasicType;

public class ANamedInvariantTypeAssistant
{

	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public ANamedInvariantTypeAssistant(IAstAssistantFactory af)
	{
		this.af = af;
	}

//	public static boolean isNumeric(ANamedInvariantType type)
//	{
//		if (type.getOpaque())
//			return false;
//		return PTypeAssistant.isNumeric(type.getType());
//	}

	public static SNumericBasicType getNumeric(ANamedInvariantType type)
	{
		return PTypeAssistant.getNumeric(type.getType());
	}

}
