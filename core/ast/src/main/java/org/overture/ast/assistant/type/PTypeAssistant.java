package org.overture.ast.assistant.type;

import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.EBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SNumericBasicType;

public class PTypeAssistant {

	public static boolean isNumeric(PType type)
	{
		switch (type.kindPType())
		{
			case BASIC:
				SBasicType bType = (SBasicType) type;
				if (bType.kindSBasicType() == EBasicType.NUMERIC)
				{
					return true;
				} else
				{
					return false;
				}
			case BRACKET:
				return ABracketTypeAssistant.isNumeric((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.isNumeric((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.isNumeric((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.isNumeric((AParameterType) type);
			case UNION:
				return AUnionTypeAssistant.isNumeric((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.isNumeric((AUnknownType) type);
			default:
				break;
		}
		return false;
	}
	
	public static SNumericBasicType getNumeric(PType type)
	{
		switch (type.kindPType())
		{
			case BASIC:
				SBasicType bType = (SBasicType) type;
				if (bType.kindSBasicType() == EBasicType.NUMERIC)
				{
					if (type instanceof SNumericBasicType)
					{
						return (SNumericBasicType) type;
					}
				}
				break;
			case BRACKET:
				return ABracketTypeAssistant.getNumeric((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.getNumeric((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.getNumeric((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.getNumeric((AParameterType) type);
			case UNION:
				return AUnionTypeAssistant.getNumeric((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.getNumeric((AUnknownType) type);
			default:
				break;
		}
		assert false : "Can't getNumeric of a non-numeric";
		return null;
	}
	
}
