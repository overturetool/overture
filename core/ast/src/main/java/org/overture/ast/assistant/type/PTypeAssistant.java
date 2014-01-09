package org.overture.ast.assistant.type;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistantFactory;
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

public class PTypeAssistant
{

	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public PTypeAssistant(IAstAssistantFactory af)
	{
		this.af = af;
	}

	public static boolean isNumeric(PType type)
	{
		try
		{
			return type.apply(af.getNumericFinder());
		} catch (AnalysisException e)
		{
			return false;
		}
//		if (type instanceof SBasicType) {
//			SBasicType bType = (SBasicType) type;
//			return bType instanceof SNumericBasicType;
//		} else if (type instanceof ABracketType) {
//			return ABracketTypeAssistant.isNumeric((ABracketType) type);
//		} else if (type instanceof SInvariantType) {
//			if (type instanceof ANamedInvariantType) {
//				return ANamedInvariantTypeAssistant.isNumeric((ANamedInvariantType) type);
//			}
//		} else if (type instanceof AOptionalType) {
//			return AOptionalTypeAssistant.isNumeric((AOptionalType) type);
//		} else if (type instanceof AUnionType) {
//			return AUnionTypeAssistant.isNumeric((AUnionType) type);
//		} else if (type instanceof AUnknownType) {
//			return AUnknownTypeAssistant.isNumeric((AUnknownType) type);
//		}
//		return false;
	}

	public static SNumericBasicType getNumeric(PType type)
	{
		try
		{
			return type.apply(af.getNumericBasisChecker());
		} catch (AnalysisException e)
		{
			return null;
		}
		
//		if (type instanceof SBasicType) {
//			if (type instanceof SNumericBasicType) {
//				return (SNumericBasicType) type;
//			}
//		} else if (type instanceof ABracketType) {
//			return ABracketTypeAssistant.getNumeric((ABracketType) type);
//		} else if (type instanceof SInvariantType) {
//			if (type instanceof ANamedInvariantType) {
//				return ANamedInvariantTypeAssistant.getNumeric((ANamedInvariantType) type);
//			}
//		} else if (type instanceof AOptionalType) {
//			return AOptionalTypeAssistant.getNumeric((AOptionalType) type);
//		} else if (type instanceof AUnionType) {
//			return AUnionTypeAssistant.getNumeric((AUnionType) type);
//		} else if (type instanceof AUnknownType) {
//			return AUnknownTypeAssistant.getNumeric((AUnknownType) type);
//		}
//		assert false : "Can't getNumeric of a non-numeric";
//		return null;
	}

	public static int hashCode(PType type)
	{
		if (type instanceof ABracketType) {
			return hashCode(((ABracketType) type).getType());
		} else if (type instanceof AClassType) {
			return ((AClassType) type).getName().hashCode();
		} else if (type instanceof AFunctionType) {
			AFunctionType ftype = (AFunctionType) type;
			return hashCode(ftype.getParameters())
					+ hashCode(ftype.getResult());
		} else if (type instanceof SInvariantType) {
			if (type instanceof ANamedInvariantType) {
				return ((ANamedInvariantType) type).getName().hashCode();
			} else if (type instanceof ARecordInvariantType) {
				return ((ARecordInvariantType) type).getName().hashCode();
			}
		} else if (type instanceof SMapType) {
			SMapType mtype = (SMapType) type;
			return hashCode(mtype.getFrom()) + hashCode(mtype.getTo());
		} else if (type instanceof AOperationType) {
			AOperationType otype = (AOperationType) type;
			return hashCode(otype.getParameters()) + hashCode(otype.getResult());
		} else if (type instanceof AOptionalType) {
			return hashCode(((AOptionalType) type).getType());
		} else if (type instanceof AParameterType) {
			return ((AParameterType) type).getName().hashCode();
		} else if (type instanceof AProductType) {
			return hashCode(((AProductType) type).getTypes());
		} else if (type instanceof AQuoteType) {
			return ((AQuoteType) type).getValue().hashCode();
		} else if (type instanceof SSeqType) {
			SSeqType stype = (SSeqType) type;
			return stype.getEmpty() ? 0 : hashCode(stype.getSeqof());
		} else if (type instanceof ASetType) {
			ASetType stype = (ASetType) type;
			return stype.getEmpty() ? 0 : hashCode(stype.getSetof());
		} else if (type instanceof AUnionType) {
			AUnionType utype = (AUnionType) type;
			return hashCode(utype.getTypes());
		} else if (type instanceof AUnresolvedType) {
			return ((AUnresolvedType) type).getName().hashCode();
		}
		return type.getClass().hashCode();
	}

	public static int hashCode(List<PType> list)
	{
		int hashCode = 1;
		for (PType e : list)
			hashCode = 31 * hashCode + (e == null ? 0 : hashCode(e));
		return hashCode;
	}

	public static String getName(PType type)
	{
		return type.getLocation().getModule();
	}
}
