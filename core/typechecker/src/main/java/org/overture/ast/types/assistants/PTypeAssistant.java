package org.overture.ast.types.assistants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistant;
import org.overture.ast.definitions.assistants.PDefinitionAssistant;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.EBasicType;
import org.overture.ast.types.EInvariantType;
import org.overture.ast.types.EType;
import org.overture.ast.types.PAccessSpecifier;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;

public class PTypeAssistant {

	public static boolean hasSupertype(AClassType cto, PType other) {
		return PDefinitionAssistant.hasSupertype(cto.getClassdef(), other);
	}

	public static boolean isType(PType b, Class<? extends PType> typeclass) {
		return typeclass.isInstance(b);
	}

	public static PType polymorph(PType type, LexNameToken pname, PType actualType)
	{				
		LexLocation location = type.getLocation();
		List<PDefinition> definitions = type.getDefinitions();
		
		switch(type.kindPType())
		{
			case FUNCTION:
				List<PType> polyparams = new Vector<PType>();

				for (PType ptype: ((AFunctionType)type).getParameters())
				{
					polyparams.add(polymorph(ptype,pname, actualType));
				}

				PType polyresult = polymorph(((AFunctionType)type).getResult(),pname, actualType);
				AFunctionType ftype =
					new AFunctionType(location,false,definitions, 
							((AFunctionType)type).getPartial(),	polyparams, polyresult);
				return ftype;
			case MAP:
				return new AMapMapType(location,false,definitions,
						polymorph(((AMapMapType)type).getFrom(),pname, actualType), 
						polymorph(((AMapMapType)type).getTo(), pname, actualType),
						((AMapMapType)type).getEmpty());
			case OPTIONAL:
				return new AOptionalType(location, false, definitions, 
							polymorph(type,pname, actualType));
			case PRODUCT:
				List<PType> polytypes = new Vector<PType>();

				for (PType ptype: ((AProductType)type).getTypes())
				{
					polytypes.add(polymorph(ptype,pname, actualType));
				}

				return new AProductType(location,false, definitions, polytypes);
			
			case SEQ:
				return new ASeqSeqType(location,false, definitions, 
						polymorph( ((SSeqType)type).getSeqof(),pname, actualType),
						((SSeqType)type).getEmpty());
			case SET:
				return new ASetType(location, false,definitions,
						polymorph(((ASetType)type).getSetof(), pname, actualType),
						((ASetType)type).getEmpty(),false);
			case UNION:
				Set<PType> polytypesSet = new HashSet<PType>();

				for (PType ptype: ((AUnionType)type).getTypes())
				{					
					polytypesSet.add(polymorph(ptype,pname, actualType));
				}
				
				return new AUnionType(location,false,definitions, new Vector<PType>(polytypesSet),false,false);
			default:
				break;
		}
				
		return type;
	}

	

	public static boolean isUnknown(PType type) {
		return type.kindPType().equals(EType.UNKNOWN);
	}

	public static boolean isUnion(PType type) {
		return type.kindPType().equals(EType.UNION);
	}

	public static boolean isFunction(PType type) {
		return type.kindPType().equals(EType.FUNCTION);
	}

	public static AFunctionType getFunction(PType type) {
		switch (type.kindPType()) {
		case FUNCTION:
			if (type instanceof AFunctionType) {
				return (AFunctionType) type;
			}
			break;
		default:
			assert false : "Can't getFunction of a non-function";
		}
		return null;
	}

	public static PType typeResolve(PType type,
			ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {

		PType result = null;

		switch (type.kindPType()) {	
		case BRACKET:
			if (type instanceof ABracketType) {
				result = ABracketTypeAssistant.typeResolve(
						(ABracketType) type,  root, rootVisitor, question);
			}
			break;
		case CLASS:
			if (type instanceof AClassType) {
				result = AClassTypeAssistant.typeResolve(
						(AClassType) type,  root, rootVisitor, question);
			}
			break;
		case FUNCTION:
			if (type instanceof AFunctionType) {
				result = AFunctionTypeAssistant.typeResolve(
						(AFunctionType) type,  root, rootVisitor, question);
			}
			break;
		case MAP:
			if(type instanceof SMapType)
			{
				result = SMapTypeAssistant.typeResolve((SMapType)type, root, rootVisitor, question);
			}
			break;
		case OPERATION:
			if (type instanceof AOperationType) {
				result = AOperationTypeAssistant
						.typeResolve((AOperationType) type,  root,
								rootVisitor, question);
			}
			break;
		case OPTIONAL:
			if (type instanceof AOptionalType) {
				result = AOptionalTypeAssistant
						.typeResolve((AOptionalType) type,  root,
								rootVisitor, question);
			}
			break;
		case PARAMETER:
			if(type instanceof AParameterType)
			{
				result = AParameterTypeAssistant.typeResolve((AParameterType)type,root,rootVisitor,question);
			}
			break;
		case PRODUCT:
			result = AProductTypeAssistant.typeResolve((AProductType)type,root,rootVisitor,question);
			break;
			
		case SEQ:			
			result = SSeqTypeAssistant.typeResolve((SSeqType)type,root,rootVisitor,question);
			break;
		case SET:
			result = ASetTypeAssistant.typeResolve((ASetType)type,root,rootVisitor,question);
			break;
		case UNION:
			result = AUnionTypeAssistat.typeResolve((AUnionType)type,root,rootVisitor,question);
			break;
		case UNRESOLVED:
			result = AUnresolvedTypeAssistant.typeResolve((AUnresolvedType)type,root,rootVisitor,question);
			break;
		default:
			type.setResolved(true);
			result = type;
			break;
		}
		return result;
	}

	public static void unResolve(PType type) {
		switch (type.kindPType()) {		
		case BRACKET:
			if (type instanceof ABracketType) {
				ABracketTypeAssistant.unResolve((ABracketType)type);
			}
			break;		
		case CLASS:
			if (type instanceof AClassType) {
				AClassTypeAssistant.unResolve((AClassType)type);
			}
			break;
		case FUNCTION:
			if (type instanceof AFunctionType) {
				AFunctionTypeAssistant.unResolve((AFunctionType) type);
			}
			break;		
			
		case INVARIANT:
		case MAP:
			if(type instanceof SMapType)
			{
				SMapTypeAssistant.unResolve((SMapType)type);
			}
			break;
		case OPERATION:
			if (type instanceof AOperationType) {
				AOperationTypeAssistant.unResolve((AOperationType) type);
			}
			break;
		case OPTIONAL:
			AOperationTypeAssistant.unResolve((AOperationType)type);
			break;
		case PRODUCT:
			AProductTypeAssistant.unResolve((AProductType) type);
			break;
		case SEQ:
			SSeqTypeAssistant.unResolve((SSeqType)type);			
			break;
		case SET:
			ASetTypeAssistant.unResolve((ASetType)type);
			break;
		case UNION:
			AUnionTypeAssistat.unResolve((AUnionType)type);
			break;
		
		default:
			System.out.println("PTypeAssistent : typeResolve not implemented");
			break;
		}

	}

	public static boolean isOperation(PType type) {
		switch (type.kindPType()) {
		case OPERATION:
			return true;
		case BASIC:
		case BRACKET:
		case CLASS:
		case FUNCTION:	
		case INVARIANT:
		case MAP:
		case OPTIONAL:
		case PARAMETER:
		case PRODUCT:		
		case QUOTE:
		case SEQ:
		case SET:
		case UNDEFINED:
		case UNION:
		case UNKNOWN:
		case UNRESOLVED:
		case VOID:
		case VOIDRETURN:
		default:
			System.out.println("PTypeAssistent : isOperation not implemented");
			return false;

		}
	}

	public static AOperationType getOperation(PType type) {
		switch (type.kindPType()) {
		case OPERATION:
			if (type instanceof AOperationType) {
				return (AOperationType) type;
			}

		default:
			assert false : "Can't getOperation of a non-operation";
			return null;

		}
	}

	public static boolean isSeq(PType type) {
		switch (type.kindPType()) {
		case SEQ:
			return true;
		default:
			return false;
		}
	}

	public static SSeqType getSeq(PType type) {
		switch (type.kindPType()) {
		case SEQ:					
				return (SSeqType) type;		
		default:
			assert false : "Can't getSeq of a non-seq";
			return null;
		}
	}

	public static boolean isNumeric(PType type) {
		switch (type.kindPType()) {
		case BASIC:
			if (type instanceof SBasicType) {
				SBasicType bType = (SBasicType) type;
				if (bType.kindSBasicType() == EBasicType.NUMERIC) {
					return true;
				} else
					return false;

			}
		default:
			return false;
		}
	}

	public static boolean isMap(PType type) {
		switch (type.kindPType()) {
		case MAP:
			return true;
		default:
			return false;
		}
	}

	public static SMapType getMap(PType type) {
		switch (type.kindPType()) {
		case MAP:
			if (type instanceof AMapMapType) {
				return (AMapMapType) type;
			}
			else if(type instanceof AInMapMapType) {
				return (AInMapMapType) type;
			}
				
		default:
			assert false : "Can't getMap of a non-map";
			return null;
		}
	}

	public static boolean isSet(PType type) {
		switch (type.kindPType()) {
		case SET:
			return true;
		default:
			return false;
		}
	}

	public static ASetType getSet(PType type) {
		switch (type.kindPType()) {
		case SET:
			if (type instanceof ASetType) {
				return (ASetType) type;
			}
		default:
			assert false : "Can't getSet of a non-set";
			return null;
		}
	}

	public static SNumericBasicType getNumeric(PType type) {
		switch (type.kindPType()) {
		case BASIC:
			if (type instanceof SBasicType) {
				SBasicType bType = (SBasicType) type;
				if (bType.kindSBasicType() == EBasicType.NUMERIC) {
					if (type instanceof SNumericBasicType) {
						return (SNumericBasicType) type;
					}
				}
			}
		default:
			assert false : "Can't getNumeric of a non-numeric";
			return null;
		}
	}

	public static boolean isRecord(PType type) {
		switch (type.kindPType()) {
		case INVARIANT:
			if (type instanceof SInvariantType) {
				SInvariantType iType = (SInvariantType) type;
				if (iType.kindSInvariantType() == EInvariantType.RECORD) {
					if (type instanceof ARecordInvariantType) {
						return true;
					}
				}
			}
		default:
			return false;
		}
	}

	public static ARecordInvariantType getRecord(PType type) {
		switch (type.kindPType()) {
		case INVARIANT:
			if (type instanceof SInvariantType) {
				SInvariantType iType = (SInvariantType) type;
				if (iType.kindSInvariantType() == EInvariantType.RECORD) {
					if (type instanceof ARecordInvariantType) {
						return (ARecordInvariantType) type;
					}
				}
			}
		default:
			assert false : "Can't getRecord of a non-record";
			return null;
		}
	}

	public static boolean isClass(PType type) {
		switch (type.kindPType()) {
		case CLASS:
			if (type instanceof AClassType) {
				return true;
			}
		default:
			return false;
		}
	}

	public static AClassType getClassType(PType type) {
		switch (type.kindPType()) {
		case CLASS:
			if (type instanceof AClassType) {
				return (AClassType) type;
			}
		default:
			assert false : "Can't getClass of a non-class";
			return null;
		}

	}

	public static AProductType getProduct(PType type) {
		switch (type.kindPType()) {
		case PRODUCT:
			if (type instanceof AProductType) {
				return (AProductType) type;
			}
		default:
			assert false : "Can't getProduct of a non-product";
			return null;
		}
	}
	
	public static boolean isProduct(PType type) {
		switch (type.kindPType()) {
		case PRODUCT:
			if (type instanceof AProductType) {
				return true;
			}
		default:
			return false;
		}		
	}

	public static boolean narrowerThan(PType type, PAccessSpecifier accessSpecifier) {
		if (type.getDefinitions() != null)
		{
			boolean result = false;

			for (PDefinition d: type.getDefinitions())
			{
				result = result || PAccessSpecifierAssistant.narrowerThan(d.getAccess(),accessSpecifier);
			}

			return result;
		}
		else
		{
			return false;
		}
	}

	public static boolean equals(PType type, PType other) {
		
		other = deBracket(other);
		return type.getClass() == other.getClass();
		
	}

	private static PType deBracket(PType other) {
		
		while (other instanceof ABracketType)
		{
			other = ((ABracketType)other).getType();
		}

		return other;
	}

}
