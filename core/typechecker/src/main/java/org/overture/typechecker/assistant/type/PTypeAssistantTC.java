package org.overture.typechecker.assistant.type;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.type.PTypeAssistant;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.LexNameToken;
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
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class PTypeAssistantTC extends PTypeAssistant
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
				return ABracketTypeAssistantTC.isType((ABracketType) b, typeclass);
			case INVARIANT:
				if (b instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isType((ANamedInvariantType) b, typeclass);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.isType((AOptionalType) b, typeclass);
			case PARAMETER:
				return AParameterTypeAssistantTC.isType((AParameterType) b, typeclass);
			case UNION:
				return AUnionTypeAssistantTC.isType((AUnionType) b, typeclass);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.isType((AUnknownType) b, typeclass);
			default:
				break;
		}
		return typeclass.isInstance(b);

	}

	public static PType polymorph(PType type, LexNameToken pname,
			PType actualType)
	{
		switch (type.kindPType())
		{
			case PARAMETER:
				return AParameterTypeAssistantTC.polymorph((AParameterType) type, pname, actualType);

			case FUNCTION:
				return AFunctionTypeAssistantTC.polymorph((AFunctionType) type, pname, actualType);

			case MAP:
				return SMapTypeAssistantTC.polymorph((SMapType)type,pname,actualType);

			case OPTIONAL:
				return AOptionalTypeAssistantTC.polymorph((AOptionalType)type,pname,actualType);
				
			case PRODUCT:
				return AProductTypeAssistantTC.polymorph((AProductType) type,pname,actualType);
				
			case SEQ:
				return SSeqTypeAssistantTC.polymorph((SSeqType) type,pname,actualType);

			case SET:
				return ASetTypeAssistantTC.polymorph((ASetType) type,pname,actualType);
			
			case UNION:
				return AUnionTypeAssistantTC.polymorph((AUnionType)type,pname,actualType);				
			default:
				return type;
		}

	}

	public static boolean isUnknown(PType type)
	{
		switch (type.kindPType())
		{
			case UNION:
				return AUnionTypeAssistantTC.isUnknown((AUnionType)type);
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
				return ABracketTypeAssistantTC.isUnion((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isUnion((ANamedInvariantType) type);
				}
				break;
			case UNION:
				return AUnionTypeAssistantTC.isUnion((AUnionType) type);
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
				return ABracketTypeAssistantTC.getUnion((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getUnion((ANamedInvariantType) type);
				}
				break;
			case UNION:
				return AUnionTypeAssistantTC.getUnion((AUnionType) type);
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
				return ABracketTypeAssistantTC.isFunction((ABracketType) type);
			case FUNCTION:
				return true;
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isFunction((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.isFunction((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistantTC.isFunction((AFunctionType) type);
			case UNION:
				return AUnionTypeAssistantTC.isFunction((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.isFunction((AUnknownType) type);
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
				return ABracketTypeAssistantTC.getFunction((ABracketType) type);
			case FUNCTION:
				return (AFunctionType) type;
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getFunction((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.getFunction((AOptionalType) type);
			case UNION:
				return AUnionTypeAssistantTC.getFunction((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.getFunction((AUnknownType) type);
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
				result = ABracketTypeAssistantTC.typeResolve((ABracketType) type, root, rootVisitor, question);

				break;
			case CLASS:
				result = AClassTypeAssistantTC.typeResolve((AClassType) type, root, rootVisitor, question);
				break;
			case FUNCTION:
				result = AFunctionTypeAssistantTC.typeResolve((AFunctionType) type, root, rootVisitor, question);
				break;
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					result = ANamedInvariantTypeAssistantTC.typeResolve((ANamedInvariantType) type, root, rootVisitor, question);
				} else if (type instanceof ARecordInvariantType)
				{
					result = ARecordInvariantTypeAssistantTC.typeResolve((ARecordInvariantType) type, root, rootVisitor, question);
				}
				break;
			case MAP:
				result = SMapTypeAssistantTC.typeResolve((SMapType) type, root, rootVisitor, question);
				break;
			case OPERATION:
				result = AOperationTypeAssistantTC.typeResolve((AOperationType) type, root, rootVisitor, question);
				break;
			case OPTIONAL:
				result = AOptionalTypeAssistantTC.typeResolve((AOptionalType) type, root, rootVisitor, question);
				break;
			case PARAMETER:
				result = AParameterTypeAssistantTC.typeResolve((AParameterType) type, root, rootVisitor, question);

				break;
			case PRODUCT:
				result = AProductTypeAssistantTC.typeResolve((AProductType) type, root, rootVisitor, question);
				break;

			case SEQ:
				result = SSeqTypeAssistantTC.typeResolve((SSeqType) type, root, rootVisitor, question);
				break;
			case SET:
				result = ASetTypeAssistantTC.typeResolve((ASetType) type, root, rootVisitor, question);
				break;
			case UNION:
				result = AUnionTypeAssistantTC.typeResolve((AUnionType) type, root, rootVisitor, question);
				break;
			case UNRESOLVED:
				result = AUnresolvedTypeAssistantTC.typeResolve((AUnresolvedType) type, root, rootVisitor, question);
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
				ABracketTypeAssistantTC.unResolve((ABracketType) type);
				break;
			case CLASS:
				AClassTypeAssistantTC.unResolve((AClassType) type);
				break;
			case FUNCTION:
				AFunctionTypeAssistantTC.unResolve((AFunctionType) type);
				break;
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					ANamedInvariantTypeAssistantTC.unResolve((ANamedInvariantType) type);
				} else if (type instanceof ARecordInvariantType)
				{
					ARecordInvariantTypeAssistantTC.unResolve((ARecordInvariantType) type);
				}
				break;
			case MAP:
				SMapTypeAssistantTC.unResolve((SMapType) type);
				break;
			case OPERATION:
				if (type instanceof AOperationType)
				{
					AOperationTypeAssistantTC.unResolve((AOperationType) type);
				}
				break;
			case OPTIONAL:
				AOptionalTypeAssistantTC.unResolve((AOptionalType) type);
				break;
			case PRODUCT:
				AProductTypeAssistantTC.unResolve((AProductType) type);
				break;
			case SEQ:
				SSeqTypeAssistantTC.unResolve((SSeqType) type);
				break;
			case SET:
				ASetTypeAssistantTC.unResolve((ASetType) type);
				break;
			case UNION:
				AUnionTypeAssistantTC.unResolve((AUnionType) type);
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
				return ABracketTypeAssistantTC.isOperation((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isOperation((ANamedInvariantType) type);
				}
				break;
			case OPERATION:
				return true;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.isOperation((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistantTC.isOperation((AParameterType) type);
			case UNION:
				return AUnionTypeAssistantTC.isOperation((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.isOperation((AUnknownType) type);
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
				return ABracketTypeAssistantTC.getOperation((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getOperation((ANamedInvariantType) type);
				}
				break;
			case OPERATION:
				return (AOperationType) type;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.getOperation((AOptionalType) type);
			case UNION:
				return AUnionTypeAssistantTC.getOperation((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.getOperation((AUnknownType) type);
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
				return ABracketTypeAssistantTC.isSeq((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isSeq((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.isSeq((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistantTC.isSeq((AParameterType) type);
			case UNION:
				return AUnionTypeAssistantTC.isSeq((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.isSeq((AUnknownType) type);
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
				return ABracketTypeAssistantTC.getSeq((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getSeq((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.getSeq((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistantTC.getSeq((AParameterType) type);
			case UNION:
				return AUnionTypeAssistantTC.getSeq((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.getSeq((AUnknownType) type);
			default:
				break;
		}
		assert false : "cannot getSeq from non-seq";
		return null;

	}

	

	

	public static boolean isMap(PType type)
	{
		switch (type.kindPType())
		{
			case MAP:
				return SMapTypeAssistantTC.isMap((SMapType) type);
			case BRACKET:
				return ABracketTypeAssistantTC.isMap((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isMap((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.isMap((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistantTC.isMap((AParameterType) type);
			case UNION:
				return AUnionTypeAssistantTC.isMap((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.isMap((AUnknownType) type);
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
				return SMapTypeAssistantTC.getMap((SMapType) type);
			case BRACKET:
				return ABracketTypeAssistantTC.getMap(((ABracketType) type));
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getMap((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.getMap((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistantTC.getMap((AParameterType) type);
			case UNION:
				return AUnionTypeAssistantTC.getMap((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.getMap((AUnknownType) type);
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
				return ASetTypeAssistantTC.isSet((ASetType) type);
			case BRACKET:
				return ABracketTypeAssistantTC.isSet((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isSet((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.isSet((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistantTC.isSet((AParameterType) type);
			case UNION:
				return AUnionTypeAssistantTC.isSet((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.isSet((AUnknownType) type);
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
				return ASetTypeAssistantTC.getSet((ASetType) type);
			case BRACKET:
				return ABracketTypeAssistantTC.getSet((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getSet((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.getSet((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistantTC.getSet((AParameterType) type);
			case UNION:
				return AUnionTypeAssistantTC.getSet((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.getSet((AUnknownType) type);
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
				return ABracketTypeAssistantTC.isRecord((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isRecord((ANamedInvariantType) type);
				} else if (type instanceof ARecordInvariantType)
				{
					return ARecordInvariantTypeAssistantTC.isRecord((ARecordInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.isRecord((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistantTC.isRecord((AParameterType) type);
			case UNION:
				return AUnionTypeAssistantTC.isRecord((AUnionType) type);
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
				return ABracketTypeAssistantTC.getRecord((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getRecord((ANamedInvariantType) type);

				} else if (type instanceof ARecordInvariantType)
				{
					return ARecordInvariantTypeAssistantTC.getRecord((ARecordInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.getRecord((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistantTC.getRecord((AParameterType) type);
			case UNION:
				return AUnionTypeAssistantTC.getRecord((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.getRecord((AUnknownType) type);
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
				return AClassTypeAssistantTC.isClass((AClassType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isClass((ANamedInvariantType) type);

				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.isClass((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistantTC.isClass((AParameterType) type);
			case UNION:
				return AUnionTypeAssistantTC.isClass((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.isClass((AUnknownType) type);
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
					return ANamedInvariantTypeAssistantTC.getClassType((ANamedInvariantType) type);

				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.getClassType((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistantTC.getClassType((AParameterType) type);
			case UNION:
				return AUnionTypeAssistantTC.getClassType((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.getClassType((AUnknownType) type);
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
				return ABracketTypeAssistantTC.getProduct((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getProduct((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.getProduct((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistantTC.getProduct((AProductType) type);
			case PRODUCT:
				return AProductTypeAssistantTC.getProduct((AProductType) type);
			case UNION:
				return AUnionTypeAssistantTC.getProduct((AUnionType) type);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.getProduct((AUnknownType) type);
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
				return ABracketTypeAssistantTC.isProduct((ABracketType) type);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isProduct((ANamedInvariantType) type);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.isProduct((AOptionalType) type);
			case PARAMETER:
				return AParameterTypeAssistantTC.isProduct((AParameterType) type);
			case PRODUCT:
				return AProductTypeAssistantTC.isProduct((AProductType) type);
			case UNION:
				return AUnionTypeAssistantTC.isProduct((AUnionType) type);
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
				return ABracketTypeAssistantTC.narrowerThan((ABracketType) type, accessSpecifier);
			case FUNCTION:
				return AFunctionTypeAssistantTC.narrowerThan((AFunctionType) type, accessSpecifier);
			case OPERATION:
				return AOperationTypeAssistantTC.narrowerThan((AOperationType) type, accessSpecifier);
			case OPTIONAL:
				return AOptionalTypeAssistantTC.narrowerThan((AOptionalType) type, accessSpecifier);
			case PARAMETER:
				return AParameterTypeAssistantTC.narrowerThan((AParameterType) type, accessSpecifier);
			case SEQ:
				return SSeqTypeAssistantTC.narrowerThan((SSeqType) type, accessSpecifier);
			case SET:
				return ASetTypeAssistantTC.narrowerThan((ASetType) type, accessSpecifier);
			case UNION:
				return AUnionTypeAssistantTC.narrowerThan((AUnionType) type, accessSpecifier);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.narrowerThan((AUnknownType) type, accessSpecifier);
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

	public static boolean equals(PType type, Object other)
	{
		switch (type.kindPType())
		{
			case BRACKET:
				return ABracketTypeAssistantTC.equals((ABracketType) type, other);
			case CLASS:
				return AClassTypeAssistantTC.equals((AClassType) type, other);
			case FUNCTION:
				return AFunctionTypeAssistantTC.equals((AFunctionType) type, other);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.equals((ANamedInvariantType) type, other);
				} else if (type instanceof ARecordInvariantType)
				{
					return ARecordInvariantTypeAssistantTC.equals((ARecordInvariantType) type, other);
				}
			case MAP:
				return SMapTypeAssistantTC.equals((SMapType) type, other);
			case OPERATION:
				return AOperationTypeAssistantTC.equals((AOperationType) type, other);
			case OPTIONAL:
				return AOptionalTypeAssistantTC.equals((AOptionalType) type, other);
			case PARAMETER:
				return AParameterTypeAssistantTC.equals((AParameterType) type, other);
			case PRODUCT:
				return AProductTypeAssistantTC.equals((AProductType) type, other);
			case QUOTE:
				return AQuoteTypeAssistantTC.equals((AQuoteType) type, other);
			case SEQ:
				return SSeqTypeAssistantTC.equals((SSeqType) type, other);
			case SET:
				return ASetTypeAssistantTC.equals((ASetType) type, other);
			case UNDEFINED:
				return AUndefinedTypeAssistantTC.equals((AUndefinedType) type, other);
			case UNION:
				return AUnionTypeAssistantTC.equals((AUnionType) type, other);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.equals((AUnknownType) type, other);
			case UNRESOLVED:
				return AUnresolvedTypeAssistantTC.equals((AUnresolvedType) type, other);
			case VOID:
				return AVoidTypeAssistantTC.equals((AVoidType) type, other);
			case VOIDRETURN:
				return AVoidReturnTypeAssistantTC.equals((AVoidReturnType) type, other);
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
				return ABracketTypeAssistantTC.isType((ABracketType) exptype, typename);
			case INVARIANT:
				if (exptype instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isType((ANamedInvariantType) exptype, typename);
				} else if (exptype instanceof ARecordInvariantType)
				{
					return ARecordInvariantTypeAssistantTC.isType((ARecordInvariantType) exptype, typename);
				}
				break;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.isType((AOptionalType) exptype, typename);
			case PARAMETER:
				return AParameterTypeAssistantTC.isType((AParameterType) exptype, typename);
			case UNION:
				return AUnionTypeAssistantTC.isType((AUnionType) exptype, typename);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.isType((AUnknownType) exptype, typename);
			case UNRESOLVED:
				return AUnresolvedTypeAssistantTC.isType((AUnresolvedType) exptype, typename);
			case VOID:
			case VOIDRETURN:
			default:
				break;
		}

		return (PTypeAssistantTC.toDisplay(exptype).equals(typename)) ? exptype
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
				return ABracketTypeAssistantTC.toDisplay((ABracketType) exptype);
			case CLASS:
				return AClassTypeAssistantTC.toDisplay((AClassType) exptype);
			case FUNCTION:
				return AFunctionTypeAssistantTC.toDisplay((AFunctionType) exptype);
			case INVARIANT:
				switch (((SInvariantType) exptype).kindSInvariantType())
				{
					case NAMED:
						return ANamedInvariantTypeAssistantTC.toDisplay((ANamedInvariantType) exptype);
					case RECORD:
						return ARecordInvariantTypeAssistantTC.toDisplay((ARecordInvariantType) exptype);
				}
			case MAP:
				switch (((SMapType) exptype).kindSMapType())
				{
					case INMAP:
						return AInMapMapTypeAssistantTC.toDisplay((AInMapMapType) exptype);
					case MAP:
						return AMapMapTypeAssistantTC.toDisplay((AMapMapType) exptype);
				}
			case OPERATION:
				return AOperationTypeAssistantTC.toDisplay((AOperationType) exptype);
			case OPTIONAL:
				return AOptionalTypeAssistantTC.toDisplay((AOptionalType) exptype);
			case PARAMETER:
				return AParameterTypeAssistantTC.toDisplay((AParameterType) exptype);
			case PRODUCT:
				return AProductTypeAssistantTC.toDisplay((AProductType) exptype);
			case QUOTE:
				return AQuoteTypeAssistantTC.toDisplay((AQuoteType) exptype);
			case SEQ:
				switch (((SSeqType) exptype).kindSSeqType())
				{
					case SEQ:
						return ASeqSeqTypeAssistantTC.toDisplay((ASeqSeqType) exptype);
					case SEQ1:
						return ASeq1SeqTypeAssistantTC.toDisplay((ASeq1SeqType) exptype);
				}
			case SET:
				return ASetTypeAssistantTC.toDisplay((ASetType) exptype);
			case UNDEFINED:
				return "(undefined)";
			case UNION:
				return AUnionTypeAssistantTC.toDisplay((AUnionType) exptype);
			case UNKNOWN:
				return "?";
			case UNRESOLVED:
				return AUnresolvedTypeAssistantTC.toDisplay((AUnresolvedType) exptype);
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
				return ABracketTypeAssistantTC.isProduct((ABracketType) type, size);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isProduct((ANamedInvariantType) type, size);
				} else
					return false;
			case OPTIONAL:
				return AOptionalTypeAssistantTC.isProduct((AOptionalType) type, size);
			case PARAMETER:
				return true;
			case PRODUCT:
				return AProductTypeAssistantTC.isProduct((AProductType) type, size);
			case UNION:
				return AUnionTypeAssistantTC.isProduct((AUnionType) type, size);
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
				return ABracketTypeAssistantTC.getProduct((ABracketType) type, size);
			case INVARIANT:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getProduct((ANamedInvariantType) type, size);
				} else
				{
					assert false : "cannot getProduct from non-product type";
					return null;
				}
			case OPTIONAL:
				return AOptionalTypeAssistantTC.getProduct((AOptionalType) type, size);
			case PARAMETER:
				return AParameterTypeAssistantTC.getProduct((AProductType) type, size);
			case PRODUCT:
				return AProductTypeAssistantTC.getProduct((AProductType) type, size);
			case UNION:
				return AUnionTypeAssistantTC.getProduct((AUnionType) type, size);
			case UNKNOWN:
				return AUnknownTypeAssistantTC.getProduct((AUnknownType) type, size);
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

	public static boolean isVoid(PType type) {
		switch (type.kindPType()) {
		case UNION:
			return AUnionTypeAssistantTC.isVoid((AUnionType) type);
		case VOID:
		case VOIDRETURN:
			return true;
		default:
			return false;
		}
	}

	public static boolean hasVoid(PType type) {
		switch (type.kindPType()) {
		case UNION:
			return AUnionTypeAssistantTC.hasVoid((AUnionType) type);
		case VOID:
		case VOIDRETURN:
			return true;
		default:
			return false;
		}

	}

	public static Object deBracket(Object other) {
		while (other instanceof ABracketType)
		{
			other = ((ABracketType)other).getType();
		}

		return other;
	}

	
}
