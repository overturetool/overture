package org.overture.ast.types.assistants;

import java.util.HashSet;
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
import org.overture.ast.types.ANamedInvariantType;
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
				
				AUnionType uType = new AUnionType(location,false,new Vector<PType>(polytypesSet),false,false);
				uType.setDefinitions(definitions);
				return uType;
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
			result = ABracketTypeAssistant.typeResolve(
						(ABracketType) type,  root, rootVisitor, question);
			
			break;
		case CLASS:
				result = AClassTypeAssistant.typeResolve(
						(AClassType) type,  root, rootVisitor, question);
			break;
		case FUNCTION:
				result = AFunctionTypeAssistant.typeResolve(
						(AFunctionType) type,  root, rootVisitor, question);
			break;
		case INVARIANT:
			if(type instanceof ANamedInvariantType)
			{
				result = ANamedInvariantTypeAssistant.typeResolve((ANamedInvariantType)type, root, rootVisitor, question);
			} else if(type instanceof ARecordInvariantType)
			{
				result = ARecordInvariantTypeAssistant.typeResolve((ARecordInvariantType)type, root, rootVisitor, question);
			}
			 break;
		case MAP:
				result = SMapTypeAssistant.typeResolve((SMapType)type, root, rootVisitor, question);
			break;
		case OPERATION:
				result = AOperationTypeAssistant
						.typeResolve((AOperationType) type,  root,
								rootVisitor, question);
			break;
		case OPTIONAL:			
				result = AOptionalTypeAssistant
						.typeResolve((AOptionalType) type,  root,
								rootVisitor, question);
			break;
		case PARAMETER:
			result = AParameterTypeAssistant.typeResolve((AParameterType)type,root,rootVisitor,question);
			
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
			ABracketTypeAssistant.unResolve((ABracketType)type);
			break;		
		case CLASS:
			AClassTypeAssistant.unResolve((AClassType)type);
			break;
		case FUNCTION:
			AFunctionTypeAssistant.unResolve((AFunctionType) type);
			break;		
		case INVARIANT:
			if(type instanceof ANamedInvariantType)
			{
				ANamedInvariantTypeAssistant.unResolve((ANamedInvariantType)type);
			} else if(type instanceof ARecordInvariantType)
			{
				ARecordInvariantTypeAssistant.unResolve((ARecordInvariantType)type);
			}
			 break;
		case MAP:
			SMapTypeAssistant.unResolve((SMapType)type);
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
			type.setResolved(false);
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
		case BRACKET:
			return isSeq(((ABracketType)type).getType());
		case OPTIONAL:
			return isSeq(((AOptionalType)type).getType());
		case PARAMETER:
			return true;
		case UNION:
			return getSeq(type) != null;
		case UNKNOWN:
			return true;
		default:
			return false;
		}
	}

	public static SSeqType getSeq(PType type) {
		switch (type.kindPType()) {
		case SEQ:					
				return (SSeqType) type;		
		case BRACKET:
			return getSeq(((ABracketType)type).getType());				
		case OPTIONAL:
			return getSeq(((AOptionalType)type).getType());
		case PARAMETER:
			return new ASeqSeqType(type.getLocation(), false, new AUnknownType(type.getLocation(), false), true);		
		case UNION:
			return AUnionTypeAssistat.getSeq((AUnionType)type);
		case UNKNOWN:
			return new ASeqSeqType(type.getLocation(), false, new AUnknownType(type.getLocation(), false), true);					
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
		case BRACKET:
			return isMap(((ABracketType)type).getType());		
		case INVARIANT:
			switch (((SInvariantType) type).kindSInvariantType()) {
			case NAMED:
				return isMap(((ANamedInvariantType)type).getType());
			default:				
				return false;
			}			
		case OPTIONAL:
			return isMap(((AOptionalType)type).getType());	
		case PARAMETER:
			return true;	
		case UNION:
			return getMap(type) != null;
		case UNKNOWN:
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
		case BRACKET:
			return getMap(((ABracketType)type).getType());		
		case INVARIANT:
			switch (((SInvariantType) type).kindSInvariantType()) {
			case NAMED:
				return getMap(((ANamedInvariantType)type).getType());
			default:
				assert false : "Can't getMap of a non-map";
				return null;
			}			
		case OPTIONAL:
			return getMap(((AOptionalType)type).getType());	
		case PARAMETER:
			return new AMapMapType(type.getLocation(), false, new AUnknownType(type.getLocation(), false), new AUnknownType(type.getLocation(), false), true);	
		case UNION:
			return AUnionTypeAssistat.getMap((AUnionType)type);
		case UNKNOWN:
			return new AMapMapType(type.getLocation(), false, new AUnknownType(type.getLocation(), false), new AUnknownType(type.getLocation(), false), true);						
		default:
			assert false : "Can't getMap of a non-map";
			return null;
		}
	}

	public static boolean isSet(PType type) {
		switch (type.kindPType()) {
		case SET:
			return true;
		case BRACKET:
			return isSet(((ABracketType)type).getType());		
		case OPTIONAL:
			return isSet(((AOptionalType)type).getType());
		case PARAMETER:
			return true;
		case UNION:
			return getSet((AUnionType)type) != null;
		case UNKNOWN:
			return true;
		default:
			return false;
		}
	}

	public static ASetType getSet(PType type) {
		switch (type.kindPType()) {
		case SET:			
			return (ASetType) type;		
		case BRACKET:
			return getSet(((ABracketType)type).getType());		
		case OPTIONAL:
			return getSet(((AOptionalType)type).getType());
		case PARAMETER:
			return new ASetType(type.getLocation(), false, new AUnknownType(type.getLocation(), false), true, false);		
		case UNION:
			return AUnionTypeAssistat.getSet((AUnionType)type);
		case UNKNOWN:
			return new ASetType(type.getLocation(), false, new AUnknownType(type.getLocation(), false), true, false);		
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
