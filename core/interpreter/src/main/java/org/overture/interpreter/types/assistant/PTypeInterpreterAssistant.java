package org.overture.interpreter.types.assistant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.interpreter.ast.definitions.PDefinitionInterpreter;
import org.overture.interpreter.ast.types.AFunctionTypeInterpreter;
import org.overture.interpreter.ast.types.AMapMapTypeInterpreter;
import org.overture.interpreter.ast.types.AOptionalTypeInterpreter;
import org.overture.interpreter.ast.types.AProductTypeInterpreter;
import org.overture.interpreter.ast.types.ASeqSeqTypeInterpreter;
import org.overture.interpreter.ast.types.ASetTypeInterpreter;
import org.overture.interpreter.ast.types.AUnionTypeInterpreter;
import org.overture.interpreter.ast.types.PTypeInterpreter;
import org.overture.interpreter.ast.types.SSeqTypeInterpreter;
import org.overturetool.interpreter.vdmj.lex.LexLocation;
import org.overturetool.interpreter.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ValueException;



public class PTypeInterpreterAssistant {

//	public static boolean hasSupertype(AClassType cto, PType other) {
//		return PDefinitionAssistant.hasSupertype(cto.getClassdef(), other);
//	}

	public static boolean isType(PTypeInterpreter b, Class<? extends PTypeInterpreter> typeclass) {
		return typeclass.isInstance(b);
	}

	public static PTypeInterpreter polymorph(PTypeInterpreter type, LexNameToken pname, PTypeInterpreter actualType)
	{				
		LexLocation location = type.getLocation();
		List<PDefinitionInterpreter> definitions = type.getDefinitions();
		
		switch(type.kindPTypeInterpreter())
		{
			case FUNCTION:
				List<PTypeInterpreter> polyparams = new Vector<PTypeInterpreter>();

				for (PTypeInterpreter ptype: ((AFunctionTypeInterpreter)type).getParameters())
				{
					polyparams.add(polymorph(ptype,pname, actualType));
				}

				PTypeInterpreter polyresult = polymorph(((AFunctionTypeInterpreter)type).getResult(),pname, actualType);
				AFunctionTypeInterpreter ftype =
					new AFunctionTypeInterpreter(location,false,definitions, 
							((AFunctionTypeInterpreter)type).getPartial(),	polyparams, polyresult);
				return ftype;
			case MAP:
				return new AMapMapTypeInterpreter(location,false,definitions,
						polymorph(((AMapMapTypeInterpreter)type).getFrom(),pname, actualType), 
						polymorph(((AMapMapTypeInterpreter)type).getTo(), pname, actualType),
						((AMapMapTypeInterpreter)type).getEmpty());
			case OPTIONAL:
				return new AOptionalTypeInterpreter(location, false, definitions, 
							polymorph(type,pname, actualType));
			case PRODUCT:
				List<PTypeInterpreter> polytypes = new Vector<PTypeInterpreter>();

				for (PTypeInterpreter ptype: ((AProductTypeInterpreter)type).getTypes())
				{
					polytypes.add(polymorph(ptype,pname, actualType));
				}

				return new AProductTypeInterpreter(location,false, definitions, polytypes);
			
			case SEQ:
				return new ASeqSeqTypeInterpreter(location,false, definitions, 
						polymorph( ((SSeqTypeInterpreter)type).getSeqof(),pname, actualType),
						((SSeqTypeInterpreter)type).getEmpty());
			case SET:
				return new ASetTypeInterpreter(location, false,definitions,
						polymorph(((ASetTypeInterpreter)type).getSetof(), pname, actualType),
						((ASetTypeInterpreter)type).getEmpty(),false);
			case UNION:
				Set<PTypeInterpreter> polytypesSet = new HashSet<PTypeInterpreter>();

				for (PTypeInterpreter ptype: ((AUnionTypeInterpreter)type).getTypes())
				{					
					polytypesSet.add(polymorph(ptype,pname, actualType));
				}
				
				AUnionTypeInterpreter uType = new AUnionTypeInterpreter(location,false,new Vector<PTypeInterpreter>(polytypesSet),false,false);
				uType.setDefinitions(definitions);
				return uType;
			default:
				break;
		}
				
		return type;
	}
//
//	
//
//	public static boolean isUnknown(PType type) {
//		return type.kindPType().equals(EType.UNKNOWN);
//	}
//
//	public static boolean isUnion(PType type) {
//		return type.kindPType().equals(EType.UNION);
//	}
//
//	public static boolean isFunction(PType type) {
//		return type.kindPType().equals(EType.FUNCTION);
//	}
//
//	public static AFunctionType getFunction(PType type) {
//		switch (type.kindPType()) {
//		case FUNCTION:
//			if (type instanceof AFunctionType) {
//				return (AFunctionType) type;
//			}
//			break;
//		default:
//			assert false : "Can't getFunction of a non-function";
//		}
//		return null;
//	}
//
//	public static PType typeResolve(PType type,
//			ATypeDefinition root,
//			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
//			TypeCheckInfo question) {
//
//		PType result = null;
//
//		switch (type.kindPType()) {	
//		case BRACKET:
//			result = ABracketTypeAssistant.typeResolve(
//						(ABracketType) type,  root, rootVisitor, question);
//			
//			break;
//		case CLASS:
//				result = AClassTypeAssistant.typeResolve(
//						(AClassType) type,  root, rootVisitor, question);
//			break;
//		case FUNCTION:
//				result = AFunctionTypeAssistant.typeResolve(
//						(AFunctionType) type,  root, rootVisitor, question);
//			break;
//		case INVARIANT:
//			if(type instanceof ANamedInvariantType)
//			{
//				result = ANamedInvariantTypeAssistant.typeResolve((ANamedInvariantType)type, root, rootVisitor, question);
//			} else if(type instanceof ARecordInvariantType)
//			{
//				result = ARecordInvariantTypeAssistant.typeResolve((ARecordInvariantType)type, root, rootVisitor, question);
//			}
//			 break;
//		case MAP:
//				result = SMapTypeAssistant.typeResolve((SMapType)type, root, rootVisitor, question);
//			break;
//		case OPERATION:
//				result = AOperationTypeAssistant
//						.typeResolve((AOperationType) type,  root,
//								rootVisitor, question);
//			break;
//		case OPTIONAL:			
//				result = AOptionalTypeAssistant
//						.typeResolve((AOptionalType) type,  root,
//								rootVisitor, question);
//			break;
//		case PARAMETER:
//			result = AParameterTypeAssistant.typeResolve((AParameterType)type,root,rootVisitor,question);
//			
//			break;
//		case PRODUCT:
//			result = AProductTypeAssistant.typeResolve((AProductType)type,root,rootVisitor,question);
//			break;
//			
//		case SEQ:			
//			result = SSeqTypeAssistant.typeResolve((SSeqType)type,root,rootVisitor,question);
//			break;
//		case SET:
//			result = ASetTypeAssistant.typeResolve((ASetType)type,root,rootVisitor,question);
//			break;
//		case UNION:
//			result = AUnionTypeAssistat.typeResolve((AUnionType)type,root,rootVisitor,question);
//			break;
//		case UNRESOLVED:
//			result = AUnresolvedTypeAssistant.typeResolve((AUnresolvedType)type,root,rootVisitor,question);
//			break;
//		default:
//			type.setResolved(true);
//			result = type;
//			break;
//		}
//		return result;
//	}
//
//	public static void unResolve(PType type) {
//		switch (type.kindPType()) {		
//		case BRACKET:
//			ABracketTypeAssistant.unResolve((ABracketType)type);
//			break;		
//		case CLASS:
//			AClassTypeAssistant.unResolve((AClassType)type);
//			break;
//		case FUNCTION:
//			AFunctionTypeAssistant.unResolve((AFunctionType) type);
//			break;		
//		case INVARIANT:
//			if(type instanceof ANamedInvariantType)
//			{
//				ANamedInvariantTypeAssistant.unResolve((ANamedInvariantType)type);
//			} else if(type instanceof ARecordInvariantType)
//			{
//				ARecordInvariantTypeAssistant.unResolve((ARecordInvariantType)type);
//			}
//			 break;
//		case MAP:
//			SMapTypeAssistant.unResolve((SMapType)type);
//			break;
//		case OPERATION:
//			if (type instanceof AOperationType) {
//				AOperationTypeAssistant.unResolve((AOperationType) type);
//			}
//			break;
//		case OPTIONAL:
//			AOperationTypeAssistant.unResolve((AOperationType)type);
//			break;
//		case PRODUCT:
//			AProductTypeAssistant.unResolve((AProductType) type);
//			break;
//		case SEQ:
//			SSeqTypeAssistant.unResolve((SSeqType)type);			
//			break;
//		case SET:
//			ASetTypeAssistant.unResolve((ASetType)type);
//			break;
//		case UNION:
//			AUnionTypeAssistat.unResolve((AUnionType)type);
//			break;		
//		default:
//			type.setResolved(false);
//			break;
//		}
//
//	}
//
//	public static boolean isOperation(PType type) {
//		switch (type.kindPType()) {
//		case OPERATION:
//			return true;
//		case BASIC:
//		case BRACKET:
//		case CLASS:
//		case FUNCTION:	
//		case INVARIANT:
//		case MAP:
//		case OPTIONAL:
//		case PARAMETER:
//		case PRODUCT:		
//		case QUOTE:
//		case SEQ:
//		case SET:
//		case UNDEFINED:
//		case UNION:
//		case UNKNOWN:
//		case UNRESOLVED:
//		case VOID:
//		case VOIDRETURN:
//		default:
//			System.out.println("PTypeAssistent : isOperation not implemented");
//			return false;
//
//		}
//	}
//
//	public static AOperationType getOperation(PType type) {
//		switch (type.kindPType()) {
//		case OPERATION:
//			if (type instanceof AOperationType) {
//				return (AOperationType) type;
//			}
//
//		default:
//			assert false : "Can't getOperation of a non-operation";
//			return null;
//
//		}
//	}
//
//	public static boolean isSeq(PType type) {
//		switch (type.kindPType()) {
//		case SEQ:
//			return true;	
//		case BRACKET:
//			return isSeq(((ABracketType)type).getType());
//		case OPTIONAL:
//			return isSeq(((AOptionalType)type).getType());
//		case PARAMETER:
//			return true;
//		case UNION:
//			return getSeq(type) != null;
//		case UNKNOWN:
//			return true;
//		default:
//			return false;
//		}
//	}
//
//	public static SSeqType getSeq(PType type) {
//		switch (type.kindPType()) {
//		case SEQ:					
//				return (SSeqType) type;		
//		case BRACKET:
//			return getSeq(((ABracketType)type).getType());				
//		case OPTIONAL:
//			return getSeq(((AOptionalType)type).getType());
//		case PARAMETER:
//			return new ASeqSeqType(type.getLocation(), false, new AUnknownType(type.getLocation(), false), true);		
//		case UNION:
//			return AUnionTypeAssistat.getSeq((AUnionType)type);
//		case UNKNOWN:
//			return new ASeqSeqType(type.getLocation(), false, new AUnknownType(type.getLocation(), false), true);					
//		default:
//			assert false : "Can't getSeq of a non-seq";
//			return null;
//		}
//	}
//
//	public static boolean isNumeric(PType type) {
//		switch (type.kindPType()) {
//		case BASIC:
//			if (type instanceof SBasicType) {
//				SBasicType bType = (SBasicType) type;
//				if (bType.kindSBasicType() == EBasicType.NUMERIC) {
//					return true;
//				} else
//					return false;
//
//			}
//		default:
//			return false;
//		}
//	}
//
//	public static boolean isMap(PType type) {
//		switch (type.kindPType()) {
//		case MAP:
//			return true;
//		case BRACKET:
//			return isMap(((ABracketType)type).getType());		
//		case INVARIANT:
//			switch (((SInvariantType) type).kindSInvariantType()) {
//			case NAMED:
//				return isMap(((ANamedInvariantType)type).getType());
//			default:				
//				return false;
//			}			
//		case OPTIONAL:
//			return isMap(((AOptionalType)type).getType());	
//		case PARAMETER:
//			return true;	
//		case UNION:
//			return getMap(type) != null;
//		case UNKNOWN:
//			return true;
//		default:
//			return false;
//		}
//	}
//
//	public static SMapType getMap(PType type) {
//		switch (type.kindPType()) {
//		case MAP:
//			if (type instanceof AMapMapType) {
//				return (AMapMapType) type;
//			}
//			else if(type instanceof AInMapMapType) {
//				return (AInMapMapType) type;
//			}		
//		case BRACKET:
//			return getMap(((ABracketType)type).getType());		
//		case INVARIANT:
//			switch (((SInvariantType) type).kindSInvariantType()) {
//			case NAMED:
//				return getMap(((ANamedInvariantType)type).getType());
//			default:
//				assert false : "Can't getMap of a non-map";
//				return null;
//			}			
//		case OPTIONAL:
//			return getMap(((AOptionalType)type).getType());	
//		case PARAMETER:
//			return new AMapMapType(type.getLocation(), false, new AUnknownType(type.getLocation(), false), new AUnknownType(type.getLocation(), false), true);	
//		case UNION:
//			return AUnionTypeAssistat.getMap((AUnionType)type);
//		case UNKNOWN:
//			return new AMapMapType(type.getLocation(), false, new AUnknownType(type.getLocation(), false), new AUnknownType(type.getLocation(), false), true);						
//		default:
//			assert false : "Can't getMap of a non-map";
//			return null;
//		}
//	}
//
//	public static boolean isSet(PType type) {
//		switch (type.kindPType()) {
//		case SET:
//			return true;
//		case BRACKET:
//			return isSet(((ABracketType)type).getType());		
//		case OPTIONAL:
//			return isSet(((AOptionalType)type).getType());
//		case PARAMETER:
//			return true;
//		case UNION:
//			return getSet((AUnionType)type) != null;
//		case UNKNOWN:
//			return true;
//		default:
//			return false;
//		}
//	}
//
//	public static ASetType getSet(PType type) {
//		switch (type.kindPType()) {
//		case SET:			
//			return (ASetType) type;		
//		case BRACKET:
//			return getSet(((ABracketType)type).getType());		
//		case OPTIONAL:
//			return getSet(((AOptionalType)type).getType());
//		case PARAMETER:
//			return new ASetType(type.getLocation(), false, new AUnknownType(type.getLocation(), false), true, false);		
//		case UNION:
//			return AUnionTypeAssistat.getSet((AUnionType)type);
//		case UNKNOWN:
//			return new ASetType(type.getLocation(), false, new AUnknownType(type.getLocation(), false), true, false);		
//		default:
//			assert false : "Can't getSet of a non-set";
//			return null;
//		}
//	}
//
//	public static SNumericBasicType getNumeric(PType type) {
//		switch (type.kindPType()) {
//		case BASIC:
//			if (type instanceof SBasicType) {
//				SBasicType bType = (SBasicType) type;
//				if (bType.kindSBasicType() == EBasicType.NUMERIC) {
//					if (type instanceof SNumericBasicType) {
//						return (SNumericBasicType) type;
//					}
//				}
//			}
//		default:
//			assert false : "Can't getNumeric of a non-numeric";
//			return null;
//		}
//	}
//
//	public static boolean isRecord(PType type) {
//		switch (type.kindPType()) {
//		case INVARIANT:
//			if (type instanceof SInvariantType) {
//				SInvariantType iType = (SInvariantType) type;
//				if (iType.kindSInvariantType() == EInvariantType.RECORD) {
//					if (type instanceof ARecordInvariantType) {
//						return true;
//					}
//				}
//			}
//		default:
//			return false;
//		}
//	}
//
//	public static ARecordInvariantType getRecord(PType type) {
//		switch (type.kindPType()) {
//		case INVARIANT:
//			if (type instanceof SInvariantType) {
//				SInvariantType iType = (SInvariantType) type;
//				if (iType.kindSInvariantType() == EInvariantType.RECORD) {
//					if (type instanceof ARecordInvariantType) {
//						return (ARecordInvariantType) type;
//					}
//				}
//			}
//		default:
//			assert false : "Can't getRecord of a non-record";
//			return null;
//		}
//	}
//
//	public static boolean isClass(PType type) {
//		switch (type.kindPType()) {
//		case CLASS:
//			if (type instanceof AClassType) {
//				return true;
//			}
//		default:
//			return false;
//		}
//	}
//
//	public static AClassType getClassType(PType type) {
//		switch (type.kindPType()) {
//		case CLASS:
//			if (type instanceof AClassType) {
//				return (AClassType) type;
//			}
//		default:
//			assert false : "Can't getClass of a non-class";
//			return null;
//		}
//
//	}
//
//	public static AProductType getProduct(PType type) {
//		switch (type.kindPType()) {
//		case PRODUCT:
//			if (type instanceof AProductType) {
//				return (AProductType) type;
//			}
//		default:
//			assert false : "Can't getProduct of a non-product";
//			return null;
//		}
//	}
//	
//	public static boolean isProduct(PType type) {
//		switch (type.kindPType()) {
//		case PRODUCT:
//			if (type instanceof AProductType) {
//				return true;
//			}
//		default:
//			return false;
//		}		
//	}
//
//	public static boolean narrowerThan(PType type, PAccessSpecifier accessSpecifier) {
//		if (type.getDefinitions() != null)
//		{
//			boolean result = false;
//
//			for (PDefinition d: type.getDefinitions())
//			{
//				result = result || PAccessSpecifierAssistant.narrowerThan(d.getAccess(),accessSpecifier);
//			}
//
//			return result;
//		}
//		else
//		{
//			return false;
//		}
//	}
//
//	public static boolean equals(PType type, PType other) {
//		
//		other = deBracket(other);
//		return type.getClass() == other.getClass();
//		
//	}
//
//	private static PType deBracket(PType other) {
//		
//		while (other instanceof ABracketType)
//		{
//			other = ((ABracketType)other).getType();
//		}
//
//		return other;
//	}

	public static void abort(PTypeInterpreter type, ValueException ve)
	{
		throw new ContextException(ve, type.getLocation());
	}

	public static void abort(AFunctionTypeInterpreter type, int number,
			String msg, Context ctxt)
	{
		throw new ContextException(number, msg, type.getLocation(), ctxt);
	}

}
