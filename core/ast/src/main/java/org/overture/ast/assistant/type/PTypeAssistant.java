package org.overture.ast.assistant.type;

import java.util.List;

import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.EBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;

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
	
	public static int hashCode(PType type)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return hashCode(((ABracketType)type).getType());
			case CLASS:
				return ((AClassType)type).getName().hashCode();
			case FUNCTION:
			{
				AFunctionType ftype = (AFunctionType) type;
				return hashCode(ftype.getParameters()) + hashCode(ftype.getResult());
			}
			case INVARIANT:
			{
				SInvariantType stype = (SInvariantType) type;
				switch (stype.kindSInvariantType())
				{
					case NAMED:
						return ((ANamedInvariantType)type).getName().hashCode();
					case RECORD:
						return ((ARecordInvariantType)type).getName().hashCode();
				}
			}
			case MAP:
			{
				SMapType mtype = (SMapType) type;
				return hashCode(mtype.getFrom()) + hashCode(mtype.getTo());
			}
			case OPERATION:
			{
				AOperationType otype = (AOperationType) type;
				return hashCode(otype.getParameters()) + hashCode(otype.getResult());
			}
			case OPTIONAL:
				return hashCode(((AOptionalType)type).getType());
			case PARAMETER:
				return ((AParameterType)type).getName().hashCode();
			case PRODUCT:
				return hashCode(((AProductType)type).getTypes());
			case QUOTE:
				return ((AQuoteType)type).getValue().hashCode();
			case SEQ:
			{
				SSeqType stype = (SSeqType) type;
				return  stype.getEmpty() ? 0 : hashCode(stype.getSeqof());
			}
			case SET:
			{
				ASetType stype = (ASetType) type;
				return  stype.getEmpty() ? 0 : hashCode(stype.getSetof());
			}
			case UNION:
			{
				AUnionType utype = (AUnionType) type;
				return hashCode(utype.getTypes());
			}
			case UNRESOLVED:
				return ((AUnresolvedType)type).getName().hashCode();
			default:
				return type.getClass().hashCode();
		}
	}

	public static int hashCode(List<PType> list)
	{
		 int hashCode = 1;
	        for (PType e : list)
	            hashCode = 31*hashCode + (e==null ? 0 : hashCode(e));
	        return hashCode;
	}
}
