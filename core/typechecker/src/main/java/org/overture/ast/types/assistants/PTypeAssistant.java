package org.overture.ast.types.assistants;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistantTC;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
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
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUndefinedType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.EBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;

public class PTypeAssistant
{

	public static boolean hasSupertype(AClassType cto, PType other)
	{
		return PDefinitionAssistantTC.hasSupertype(cto.getClassdef(), other);
	}

	public static boolean isType(PType b, Class<? extends PType> typeclass)
	{
		switch (b.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.isType((ABracketType) b, typeclass);
			case INVARIANT:
				if (b instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.isType((ANamedInvariantType) b, typeclass);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.isType((AOptionalType) b, typeclass);
			case PARAMETER:
				return AParameterTypeAssistant.isType((AParameterType) b, typeclass);
			case UNION:
				return AUnionTypeAssistant.isType((AUnionType) b, typeclass);
			case UNKNOWN:
				return AUnknownTypeAssistant.isType((AUnknownType) b, typeclass);
			default:
				break;
		}
		return typeclass.isInstance(b);

	}

	public static PType polymorph(PType type, LexNameToken pname,
			PType actualType)
	{
		LexLocation location = type.getLocation();
		List<PDefinition> definitions = type.getDefinitions();

		switch (type.kindPType())
		{
			case PARAMETER:
				return AParameterTypeAssistant.polymorph((AParameterType) type, pname, actualType);

			case FUNCTION:
				return AFunctionTypeAssistant.polymorph((AFunctionType) type, pname, actualType);

			case MAP:
				return new AMapMapType(location, false, definitions, polymorph(((AMapMapType) type).getFrom(), pname, actualType), polymorph(((AMapMapType) type).getTo(), pname, actualType), ((AMapMapType) type).getEmpty());
			case OPTIONAL:
				return new AOptionalType(location, false, definitions, polymorph(type, pname, actualType));
			case PRODUCT:
				List<PType> polytypes = new Vector<PType>();

				for (PType ptype : ((AProductType) type).getTypes())
				{
					polytypes.add(polymorph(ptype, pname, actualType));
				}

				return new AProductType(location, false, definitions, polytypes);

			case SEQ:
				return new ASeqSeqType(location, false, definitions, polymorph(((SSeqType) type).getSeqof(), pname, actualType), ((SSeqType) type).getEmpty());
			case SET:
				return new ASetType(location, false, definitions, polymorph(((ASetType) type).getSetof().clone(), pname, actualType), ((ASetType) type).getEmpty(), false);
			case UNION:
				Set<PType> polytypesSet = new HashSet<PType>();

				for (PType ptype : ((AUnionType) type).getTypes())
				{
					polytypesSet.add(polymorph(ptype, pname, actualType));
				}

				// AUnionType uType = new AUnionType(location,false,new Vector<PType>(polytypesSet),false,false);
				// uType.setDefinitions(definitions);
				// uType.setProdCard(-1);
				// return uType;
				return null;// TODO
			default:
				return type;
		}

	}

	public static boolean isUnknown(PType type)
	{
		switch (type.kindPType())
		{
			case PARAMETER:
			case UNKNOWN:
				return true;
		}
		return false;
	}

	public static boolean isUnion(PType type)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.isUnion((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.isUnion((ANamedInvariantType) type);
				}
				break;
			case UNION:
				return AUnionTypeAssistant.isUnion((AUnionType) type);
			default:
				break;
		}
		return false;
	}

	public static AUnionType getUnion(PType type)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.getUnion((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.getUnion((ANamedInvariantType) type);
				}
				break;
			case UNION:
				return AUnionTypeAssistant.getUnion((AUnionType) type);
			default:
				break;
		}
		assert false : " cannot getUnion from non-union";
		return null;
	}

	public static boolean isFunction(PType type)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.isFunction((ABracketType) type);
			case FUNCTION:
				return true;
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.isFunction((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.isFunction((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.isFunction((AFunctionType) type);
			case UNION:
				return AUnionTypeAssistant.isFunction((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.isFunction((AUnknownType) type);
			default:
				break;
		}
		return false;
	}

	public static AFunctionType getFunction(PType type)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.getFunction((ABracketType) type);
			case FUNCTION:
				return (AFunctionType) type;
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.getFunction((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.getFunction((AOptionalType) type);
			case UNION:
				return AUnionTypeAssistant.getFunction((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.getFunction((AUnknownType) type);
			default:
				break;
		}
		assert false : "Can't getFunction of a non-function";
		return null;
	}

	public static PType typeResolve(PType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question)
	{

		PType result = null;

		switch (type.kindPType())
		{
			case BRACKET:
				result = ABracketTypeAssistant.typeResolve((ABracketType) type, root, rootVisitor, question);

				break;
			case CLASS:
				result = AClassTypeAssistant.typeResolve((AClassType) type, root, rootVisitor, question);
				break;
			case FUNCTION:
				result = AFunctionTypeAssistant.typeResolve((AFunctionType) type, root, rootVisitor, question);
				break;
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					result = ANamedInvariantTypeAssistant.typeResolve((ANamedInvariantType) type, root, rootVisitor, question);
				} else if (type instanceof ARecordInvariantType)
				{
					result = ARecordInvariantTypeAssistant.typeResolve((ARecordInvariantType) type, root, rootVisitor, question);
				}
				break;
			case MAP:
				result = SMapTypeAssistant.typeResolve((SMapType) type, root, rootVisitor, question);
				break;
			case OPERATION:
				result = AOperationTypeAssistant.typeResolve((AOperationType) type, root, rootVisitor, question);
				break;
			case OPTIONAL:
				result = AOptionalTypeAssistant.typeResolve((AOptionalType) type, root, rootVisitor, question);
				break;
			case PARAMETER:
				result = AParameterTypeAssistant.typeResolve((AParameterType) type, root, rootVisitor, question);

				break;
			case PRODUCT:
				result = AProductTypeAssistant.typeResolve((AProductType) type, root, rootVisitor, question);
				break;

			case SEQ:
				result = SSeqTypeAssistant.typeResolve((SSeqType) type, root, rootVisitor, question);
				break;
			case SET:
				result = ASetTypeAssistant.typeResolve((ASetType) type, root, rootVisitor, question);
				break;
			case UNION:
				result = AUnionTypeAssistant.typeResolve((AUnionType) type, root, rootVisitor, question);
				break;
			case UNRESOLVED:
				result = AUnresolvedTypeAssistant.typeResolve((AUnresolvedType) type, root, rootVisitor, question);
				break;
			default:
				type.setResolved(true);
				result = type;
				break;
		}
		return result;
	}

	public static void unResolve(PType type)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				ABracketTypeAssistant.unResolve((ABracketType) type);
				break;
			case CLASS:
				AClassTypeAssistant.unResolve((AClassType) type);
				break;
			case FUNCTION:
				AFunctionTypeAssistant.unResolve((AFunctionType) type);
				break;
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					ANamedInvariantTypeAssistant.unResolve((ANamedInvariantType) type);
				} else if (type instanceof ARecordInvariantType)
				{
					ARecordInvariantTypeAssistant.unResolve((ARecordInvariantType) type);
				}
				break;
			case MAP:
				SMapTypeAssistant.unResolve((SMapType) type);
				break;
			case OPERATION:
				if (type instanceof AOperationType)
				{
					AOperationTypeAssistant.unResolve((AOperationType) type);
				}
				break;
			case OPTIONAL:
				AOptionalTypeAssistant.unResolve((AOptionalType) type);
				break;
			case PRODUCT:
				AProductTypeAssistant.unResolve((AProductType) type);
				break;
			case SEQ:
				SSeqTypeAssistant.unResolve((SSeqType) type);
				break;
			case SET:
				ASetTypeAssistant.unResolve((ASetType) type);
				break;
			case UNION:
				AUnionTypeAssistant.unResolve((AUnionType) type);
				break;
			default:
				type.setResolved(false);
				break;
		}

	}

	public static boolean isOperation(PType type)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.isOperation((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.isOperation((ANamedInvariantType) type);
				}
				break;
			case OPERATION:
				return true;
			case OPTIONAL:
				return AOptionalTypeAssistant.isOperation((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.isOperation((AParameterType) type);
			case UNION:
				return AUnionTypeAssistant.isOperation((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.isOperation((AUnknownType) type);
			default:
				break;
		}
		return false;
	}

	public static AOperationType getOperation(PType type)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.getOperation((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.getOperation((ANamedInvariantType) type);
				}
				break;
			case OPERATION:
				return (AOperationType) type;
			case OPTIONAL:
				return AOptionalTypeAssistant.getOperation((AOptionalType) type);
			case UNION:
				return AUnionTypeAssistant.getOperation((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.getOperation((AUnknownType) type);
			default:
				break;
		}
		assert false : "Can't getOperation of a non-operation";
		return null;

	}

	public static boolean isSeq(PType type)
	{
		switch (type.kindPType())
		{
			case SEQ:
				return true;
			case BRACKET:
				return ABracketTypeAssistant.isSeq((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.isSeq((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.isSeq((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.isSeq((AParameterType) type);
			case UNION:
				return AUnionTypeAssistant.isSeq((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.isSeq((AUnknownType) type);
			default:
				break;
		}
		return false;
	}

	public static SSeqType getSeq(PType type)
	{
		switch (type.kindPType())
		{
			case SEQ:
				return (SSeqType) type;
			case BRACKET:
				return ABracketTypeAssistant.getSeq((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.getSeq((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.getSeq((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.getSeq((AParameterType) type);
			case UNION:
				return AUnionTypeAssistant.getSeq((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.getSeq((AUnknownType) type);
			default:
				break;
		}
		assert false : "cannot getSeq from non-seq";
		return null;

	}

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

	public static boolean isMap(PType type)
	{
		switch (type.kindPType())
		{
			case MAP:
				return SMapTypeAssistant.isMap((SMapType) type);
			case BRACKET:
				return ABracketTypeAssistant.isMap((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.isMap((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.isMap((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.isMap((AParameterType) type);
			case UNION:
				return AUnionTypeAssistant.isMap((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.isMap((AUnknownType) type);
			default:
				break;
		}
		return false;
	}

	public static SMapType getMap(PType type)
	{
		switch (type.kindPType())
		{
			case MAP:
				return SMapTypeAssistant.getMap((SMapType) type);
			case BRACKET:
				return ABracketTypeAssistant.getMap(((ABracketType) type));
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.getMap((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.getMap((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.getMap((AParameterType) type);
			case UNION:
				return AUnionTypeAssistant.getMap((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.getMap((AUnknownType) type);
			default:
				break;
		}
		assert false : "Can't getMap of a non-map";
		return null;
	}

	public static boolean isSet(PType type)
	{
		switch (type.kindPType())
		{
			case SET:
				return ASetTypeAssistant.isSet((ASetType) type);
			case BRACKET:
				return ABracketTypeAssistant.isSet((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.isSet((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.isSet((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.isSet((AParameterType) type);
			case UNION:
				return AUnionTypeAssistant.isSet((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.isSet((AUnknownType) type);
			default:
				break;
		}
		return false;
	}

	public static ASetType getSet(PType type)
	{
		switch (type.kindPType())
		{
			case SET:
				return ASetTypeAssistant.getSet((ASetType) type);
			case BRACKET:
				return ABracketTypeAssistant.getSet((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.getSet((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.getSet((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.getSet((AParameterType) type);
			case UNION:
				return AUnionTypeAssistant.getSet((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.getSet((AUnknownType) type);
			default:
				break;
		}
		assert false : "Can't getSet of a non-set";
		return null;
	}

	public static boolean isRecord(PType type)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.isRecord((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.isRecord((ANamedInvariantType) type);
				} else if (type instanceof ARecordInvariantType)
				{
					return ARecordInvariantTypeAssistant.isRecord((ARecordInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.isRecord((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.isRecord((AParameterType) type);
			case UNION:
				return AUnionTypeAssistant.isRecord((AUnionType) type);
			default:
				break;
		}
		return false;
	}

	public static ARecordInvariantType getRecord(PType type)
	{
		switch (type.kindPType())
		{

			case BRACKET:
				return ABracketTypeAssistant.getRecord((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.getRecord((ANamedInvariantType) type);

				} else if (type instanceof ARecordInvariantType)
				{
					return ARecordInvariantTypeAssistant.getRecord((ARecordInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.getRecord((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.getRecord((AParameterType) type);
			case UNION:
				return AUnionTypeAssistant.getRecord((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.getRecord((AUnknownType) type);
			default:
				break;
		}
		assert false : "Can't getRecord of a non-record";
		return null;
	}

	public static boolean isClass(PType type)
	{
		switch (type.kindPType())
		{

			case CLASS:
				return AClassTypeAssistant.isClass((AClassType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.isClass((ANamedInvariantType) type);

				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.isClass((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.isClass((AParameterType) type);
			case UNION:
				return AUnionTypeAssistant.isClass((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.isClass((AUnknownType) type);
			default:
				break;
		}
		return false;
	}

	public static AClassType getClassType(PType type)
	{
		switch (type.kindPType())
		{
			case CLASS:
				if (type instanceof AClassType)
				{
					return (AClassType) type;
				}
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.getClassType((ANamedInvariantType) type);

				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.getClassType((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.getClassType((AParameterType) type);
			case UNION:
				return AUnionTypeAssistant.getClassType((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.getClassType((AUnknownType) type);
			default:
				break;
		}
		assert false : "Can't getClass of a non-class";
		return null;

	}

	public static AProductType getProduct(PType type)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.getProduct((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.getProduct((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.getProduct((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.getProduct((AProductType) type);
			case PRODUCT:
				return AProductTypeAssistant.getProduct((AProductType) type);
			case UNION:
				return AUnionTypeAssistant.getProduct((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistant.getProduct((AUnknownType) type);
			default:
				break;
		}
		assert false : "cannot getProduct from non-product type";
		return null;
	}

	public static boolean isProduct(PType type)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.isProduct((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.isProduct((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.isProduct((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistant.isProduct((AParameterType) type);
			case PRODUCT:
				return AProductTypeAssistant.isProduct((AProductType) type);
			case UNION:
				return AUnionTypeAssistant.isProduct((AUnionType) type);
			case UNKNOWN:
				return true;
			default:
				break;
		}
		return false;
	}

	public static boolean narrowerThan(PType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.narrowerThan((ABracketType) type, accessSpecifier);
			case FUNCTION:
				return AFunctionTypeAssistant.narrowerThan((AFunctionType) type, accessSpecifier);
			case OPERATION:
				return AOperationTypeAssistant.narrowerThan((AOperationType) type, accessSpecifier);
			case OPTIONAL:
				return AOptionalTypeAssistant.narrowerThan((AOptionalType) type, accessSpecifier);
			case PARAMETER:
				return AParameterTypeAssistant.narrowerThan((AParameterType) type, accessSpecifier);
			case SEQ:
				return SSeqTypeAssistant.narrowerThan((SSeqType) type, accessSpecifier);
			case SET:
				return ASetTypeAssistant.narrowerThan((ASetType) type, accessSpecifier);
			case UNION:
				return AUnionTypeAssistant.narrowerThan((AUnionType) type, accessSpecifier);
			case UNKNOWN:
				return AUnknownTypeAssistant.narrowerThan((AUnknownType) type, accessSpecifier);
			default:
				return narrowerThanBaseCase(type, accessSpecifier);
		}
	}

	public static boolean narrowerThanBaseCase(PType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
	{
		if (type.getDefinitions() != null)
		{
			boolean result = false;
			for (PDefinition d : type.getDefinitions())
			{
				result = result
						|| PAccessSpecifierAssistantTC.narrowerThan(d.getAccess(), accessSpecifier);
			}
			return result;
		} else
		{
			return false;
		}
	}

	public static boolean equals(PType type, PType other)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.equals((ABracketType) type, other);
			case CLASS:
				return AClassTypeAssistant.equals((AClassType) type, other);
			case FUNCTION:
				return AFunctionTypeAssistant.equals((AFunctionType) type, other);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.equals((ANamedInvariantType) type, other);
				} else if (type instanceof ARecordInvariantType)
				{
					return ARecordInvariantTypeAssistant.equals((ARecordInvariantType) type, other);
				}
			case MAP:
				return SMapTypeAssistant.equals((SMapType) type, other);
			case OPERATION:
				return AOperationTypeAssistant.equals((AOperationType) type, other);
			case OPTIONAL:
				return AOptionalTypeAssistant.equals((AOptionalType) type, other);
			case PARAMETER:
				return AParameterTypeAssistant.equals((AParameterType) type, other);
			case PRODUCT:
				return AProductTypeAssistant.equals((AProductType) type, other);
			case QUOTE:
				return AQuoteTypeAssistant.equals((AQuoteType) type, other);
			case SEQ:
				return SSeqTypeAssistant.equals((SSeqType) type, other);
			case SET:
				return ASetTypeAssistant.equals((ASetType) type, other);
			case UNDEFINED:
				return AUndefinedTypeAssistant.equals((AUndefinedType) type, other);
			case UNION:
				return AUnionTypeAssistant.equals((AUnionType) type, other);
			case UNKNOWN:
				return AUnknownTypeAssistant.equals((AUnknownType) type, other);
			case UNRESOLVED:
				return AUnresolvedTypeAssistant.equals((AUnresolvedType) type, other);
			case VOID:
				return AVoidTypeAssistant.equals((AVoidType) type, other);
			case VOIDRETURN:
				return AVoidReturnTypeAssistant.equals((AVoidReturnType) type, other);
			default:
				break;
		}

		other = deBracket(other);
		return type.getClass() == other.getClass();

	}

	public static PType deBracket(PType other)
	{

		while (other instanceof ABracketType)
		{
			other = ((ABracketType) other).getType();
		}

		return other;
	}

	public static PType isType(PType exptype, String typename)
	{
		switch (exptype.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.isType((ABracketType) exptype, typename);
			case INVARIANT:
				if (exptype instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.isType((ANamedInvariantType) exptype, typename);
				} else if (exptype instanceof ARecordInvariantType)
				{
					return ARecordInvariantTypeAssistant.isType((ARecordInvariantType) exptype, typename);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistant.isType((AOptionalType) exptype, typename);
			case PARAMETER:
				return AParameterTypeAssistant.isType((AParameterType) exptype, typename);
			case UNION:
				return AUnionTypeAssistant.isType((AUnionType) exptype, typename);
			case UNKNOWN:
				return AUnknownTypeAssistant.isType((AUnknownType) exptype, typename);
			case UNRESOLVED:
				return AUnresolvedTypeAssistant.isType((AUnresolvedType) exptype, typename);
			case VOID:
			case VOIDRETURN:
			default:
				break;
		}

		return (PTypeAssistant.toDisplay(exptype).equals(typename)) ? exptype
				: null;
	}

	private static String toDisplay(PType exptype)
	{

		switch (exptype.kindPType())
		{
			case BASIC:
				switch (((SBasicType) exptype).kindSBasicType())
				{
					case BOOLEAN:
						return "bool";
					case CHAR:
						return "char";
					case NUMERIC:
						switch (((SNumericBasicType) exptype).kindSNumericBasicType())
						{
							case INT:
								return "int";
							case NAT:
								return "nat";
							case NATONE:
								return "nat1";
							case RATIONAL:
								return "rat";
							case REAL:
								return "real";
						}
					case TOKEN:
						return "token";
				}
				break;
			case BRACKET:
				return ABracketTypeAssistant.toDisplay((ABracketType) exptype);
			case CLASS:
				return AClassTypeAssistant.toDisplay((AClassType) exptype);
			case FUNCTION:
				return AFunctionTypeAssistant.toDisplay((AFunctionType) exptype);
			case INVARIANT:
				switch (((SInvariantType) exptype).kindSInvariantType())
				{
					case NAMED:
						return ANamedInvariantTypeAssistant.toDisplay((ANamedInvariantType) exptype);
					case RECORD:
						return ARecordInvariantTypeAssistant.toDisplay((ARecordInvariantType) exptype);
				}
			case MAP:
				switch (((SMapType) exptype).kindSMapType())
				{
					case INMAP:
						return AInMapMapTypeAssistant.toDisplay((AInMapMapType) exptype);
					case MAP:
						return AMapMapTypeAssistant.toDisplay((AMapMapType) exptype);
				}
			case OPERATION:
				return AOperationTypeAssistant.toDisplay((AOperationType) exptype);
			case OPTIONAL:
				return AOptionalTypeAssistant.toDisplay((AOptionalType) exptype);
			case PARAMETER:
				return AParameterTypeAssistant.toDisplay((AParameterType) exptype);
			case PRODUCT:
				return AProductTypeAssistant.toDisplay((AProductType) exptype);
			case QUOTE:
				return AQuoteTypeAssistant.toDisplay((AQuoteType) exptype);
			case SEQ:
				switch (((SSeqType) exptype).kindSSeqType())
				{
					case SEQ:
						return ASeqSeqTypeAssistant.toDisplay((ASeqSeqType) exptype);
					case SEQ1:
						return ASeq1SeqTypeAssistant.toDisplay((ASeq1SeqType) exptype);
				}
			case SET:
				return ASetTypeAssistant.toDisplay((ASetType) exptype);
			case UNDEFINED:
				return "(undefined)";
			case UNION:
				return AUnionTypeAssistant.toDisplay((AUnionType) exptype);
			case UNKNOWN:
				return "?";
			case UNRESOLVED:
				return AUnresolvedTypeAssistant.toDisplay((AUnresolvedType) exptype);
			case VOID:
				return "()";
			case VOIDRETURN:
				return "(return)";
		}
		assert false : "PTypeAssistant.toDisplay should not hit this case";
		return null;
	}

	public static boolean isProduct(PType type, int size)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.isProduct((ABracketType) type, size);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.isProduct((ANamedInvariantType) type, size);
				} else
					return false;
			case OPTIONAL:
				return AOptionalTypeAssistant.isProduct((AOptionalType) type, size);
			case PARAMETER:
				return true;
			case PRODUCT:
				return AProductTypeAssistant.isProduct((AProductType) type, size);
			case UNION:
				return AUnionTypeAssistant.isProduct((AUnionType) type, size);
			case UNKNOWN:
				return true;
			default:
				return false;
		}
	}

	public static AProductType getProduct(PType type, int size)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistant.getProduct((ABracketType) type, size);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistant.getProduct((ANamedInvariantType) type, size);
				} else
				{
					assert false : "cannot getProduct from non-product type";
					return null;
				}
			case OPTIONAL:
				return AOptionalTypeAssistant.getProduct((AOptionalType) type, size);
			case PARAMETER:
				return AParameterTypeAssistant.getProduct((AProductType) type, size);
			case PRODUCT:
				return AProductTypeAssistant.getProduct((AProductType) type, size);
			case UNION:
				return AUnionTypeAssistant.getProduct((AUnionType) type, size);
			case UNKNOWN:
				return AUnknownTypeAssistant.getProduct((AUnknownType) type, size);
			default:
				assert false : "cannot getProduct from non-product type";
				return null;
		}
	}

	public static boolean equals(LinkedList<PType> parameters,
			LinkedList<PType> other)
	{

		if (parameters.size() != other.size())
		{
			return false;
		}

		for (int i = 0; i < parameters.size(); i++)
		{
			if (!equals(parameters.get(i), other.get(i)))
				return false;
		}

		return true;
	}

}
