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
			case SBasicType.kindPType:
				SBasicType bType = (SBasicType) type;
				return SNumericBasicType.kindSBasicType.equals(bType.kindSBasicType());
			case ABracketType.kindPType:
				return ABracketTypeAssistant.isNumeric((ABracketType) type);
			case ANamedInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.isNumeric((ANamedInvariantType) type);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistant.isNumeric((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistant.isNumeric((AParameterType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistant.isNumeric((AUnionType) type);
			case AUnknownType.kindPType:
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
			case SBasicType.kindPType:
				SBasicType bType = (SBasicType) type;
				//FIXME doesn't the outer if imply the inner? -jwc/1Apr2013 
				if (SNumericBasicType.kindSBasicType.equals(bType.kindSBasicType()))
				{
					if (type instanceof SNumericBasicType)
					{
						return (SNumericBasicType) type;
					}
				}
				break;
			case ABracketType.kindPType:
				return ABracketTypeAssistant.getNumeric((ABracketType) type);
			case ANamedInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.getNumeric((ANamedInvariantType) type);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistant.getNumeric((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistant.getNumeric((AParameterType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistant.getNumeric((AUnionType) type);
			case AUnknownType.kindPType:
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
			case ABracketType.kindPType:
				return hashCode(((ABracketType)type).getType());
			case AClassType.kindPType:
				return ((AClassType)type).getName().hashCode();
			case AFunctionType.kindPType:
				AFunctionType ftype = (AFunctionType) type;
				return hashCode(ftype.getParameters()) + hashCode(ftype.getResult());
			case SInvariantType.kindPType:
			{
				SInvariantType stype = (SInvariantType) type;
				switch (stype.kindSInvariantType())
				{
					case ANamedInvariantType.kindSInvariantType:
						return ((ANamedInvariantType)type).getName().hashCode();
					case ARecordInvariantType.kindSInvariantType:
						return ((ARecordInvariantType)type).getName().hashCode();
				}
			}
			case SMapType.kindPType:
			{
				SMapType mtype = (SMapType) type;
				return hashCode(mtype.getFrom()) + hashCode(mtype.getTo());
			}
			case AOperationType.kindPType:
			{
				AOperationType otype = (AOperationType) type;
				return hashCode(otype.getParameters()) + hashCode(otype.getResult());
			}
			case AOptionalType.kindPType:
				return hashCode(((AOptionalType)type).getType());
			case AParameterType.kindPType:
				return ((AParameterType)type).getName().hashCode();
			case AProductType.kindPType:
				return hashCode(((AProductType)type).getTypes());
			case AQuoteType.kindPType:
				return ((AQuoteType)type).getValue().hashCode();
			case SSeqType.kindPType:
			{
				SSeqType stype = (SSeqType) type;
				return  stype.getEmpty() ? 0 : hashCode(stype.getSeqof());
			}
			case ASetType.kindPType:
			{
				ASetType stype = (ASetType) type;
				return  stype.getEmpty() ? 0 : hashCode(stype.getSetof());
			}
			case AUnionType.kindPType:
			{
				AUnionType utype = (AUnionType) type;
				return hashCode(utype.getTypes());
			}
			case AUnresolvedType.kindPType:
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
