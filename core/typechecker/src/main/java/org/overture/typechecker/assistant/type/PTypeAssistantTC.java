package org.overture.typechecker.assistant.type;

import java.util.LinkedList;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.type.PTypeAssistant;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
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
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.isType((ABracketType) b, typeclass);
			case SInvariantType.kindPType:
				if (b instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isType((ANamedInvariantType) b, typeclass);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.isType((AOptionalType) b, typeclass);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.isType((AParameterType) b, typeclass);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.isType((AUnionType) b, typeclass);
			case AUnknownType.kindPType:
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
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.polymorph((AParameterType) type, pname, actualType);

			case AFunctionType.kindPType:
				return AFunctionTypeAssistantTC.polymorph((AFunctionType) type, pname, actualType);

			case SMapType.kindPType:
				return SMapTypeAssistantTC.polymorph((SMapType)type,pname,actualType);

			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.polymorph((AOptionalType)type,pname,actualType);
				
			case AProductType.kindPType:
				return AProductTypeAssistantTC.polymorph((AProductType) type,pname,actualType);
				
			case SSeqType.kindPType:
				return SSeqTypeAssistantTC.polymorph((SSeqType) type,pname,actualType);

			case ASetType.kindPType:
				return ASetTypeAssistantTC.polymorph((ASetType) type,pname,actualType);
			
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.polymorph((AUnionType)type,pname,actualType);				
			default:
				return type;
		}

	}

	public static boolean isUnknown(PType type)
	{
		switch (type.kindPType())
		{
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.isUnknown((AUnionType)type);
			case AParameterType.kindPType:
			case AUnknownType.kindPType:
				return true;
		}
		return false;
	}

	public static boolean isUnion(PType type)
	{
		switch (type.kindPType())
		{
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.isUnion((ABracketType) type);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isUnion((ANamedInvariantType) type);
				}
				break;
			case AUnionType.kindPType:
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
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.getUnion((ABracketType) type);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getUnion((ANamedInvariantType) type);
				}
				break;
			case AUnionType.kindPType:
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
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.isFunction((ABracketType) type);
			case AFunctionType.kindPType:
				return true;
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isFunction((ANamedInvariantType) type);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.isFunction((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.isFunction((AFunctionType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.isFunction((AUnionType) type);
			case AUnknownType.kindPType:
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
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.getFunction((ABracketType) type);
			case AFunctionType.kindPType:
				return (AFunctionType) type;
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getFunction((ANamedInvariantType) type);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.getFunction((AOptionalType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.getFunction((AUnionType) type);
			case AUnknownType.kindPType:
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
			case ABracketType.kindPType:
				result = ABracketTypeAssistantTC.typeResolve((ABracketType) type, root, rootVisitor, question);

				break;
			case AClassType.kindPType:
				result = AClassTypeAssistantTC.typeResolve((AClassType) type, root, rootVisitor, question);
				break;
			case AFunctionType.kindPType:
				result = AFunctionTypeAssistantTC.typeResolve((AFunctionType) type, root, rootVisitor, question);
				break;
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					result = ANamedInvariantTypeAssistantTC.typeResolve((ANamedInvariantType) type, root, rootVisitor, question);
				} else if (type instanceof ARecordInvariantType)
				{
					result = ARecordInvariantTypeAssistantTC.typeResolve((ARecordInvariantType) type, root, rootVisitor, question);
				}
				break;
			case SMapType.kindPType:
				result = SMapTypeAssistantTC.typeResolve((SMapType) type, root, rootVisitor, question);
				break;
			case AOperationType.kindPType:
				result = AOperationTypeAssistantTC.typeResolve((AOperationType) type, root, rootVisitor, question);
				break;
			case AOptionalType.kindPType:
				result = AOptionalTypeAssistantTC.typeResolve((AOptionalType) type, root, rootVisitor, question);
				break;
			case AParameterType.kindPType:
				result = AParameterTypeAssistantTC.typeResolve((AParameterType) type, root, rootVisitor, question);

				break;
			case AProductType.kindPType:
				result = AProductTypeAssistantTC.typeResolve((AProductType) type, root, rootVisitor, question);
				break;

			case SSeqType.kindPType:
				result = SSeqTypeAssistantTC.typeResolve((SSeqType) type, root, rootVisitor, question);
				break;
			case ASetType.kindPType:
				result = ASetTypeAssistantTC.typeResolve((ASetType) type, root, rootVisitor, question);
				break;
			case AUnionType.kindPType:
				result = AUnionTypeAssistantTC.typeResolve((AUnionType) type, root, rootVisitor, question);
				break;
			case AUnresolvedType.kindPType:
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
			case ABracketType.kindPType:
				ABracketTypeAssistantTC.unResolve((ABracketType) type);
				break;
			case AClassType.kindPType:
				AClassTypeAssistantTC.unResolve((AClassType) type);
				break;
			case AFunctionType.kindPType:
				AFunctionTypeAssistantTC.unResolve((AFunctionType) type);
				break;
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					ANamedInvariantTypeAssistantTC.unResolve((ANamedInvariantType) type);
				} else if (type instanceof ARecordInvariantType)
				{
					ARecordInvariantTypeAssistantTC.unResolve((ARecordInvariantType) type);
				}
				break;
			case SMapType.kindPType:
				SMapTypeAssistantTC.unResolve((SMapType) type);
				break;
			case AOperationType.kindPType:
				if (type instanceof AOperationType)
				{
					AOperationTypeAssistantTC.unResolve((AOperationType) type);
				}
				break;
			case AOptionalType.kindPType:
				AOptionalTypeAssistantTC.unResolve((AOptionalType) type);
				break;
			case AProductType.kindPType:
				AProductTypeAssistantTC.unResolve((AProductType) type);
				break;
			case SSeqType.kindPType:
				SSeqTypeAssistantTC.unResolve((SSeqType) type);
				break;
			case ASetType.kindPType:
				ASetTypeAssistantTC.unResolve((ASetType) type);
				break;
			case AUnionType.kindPType:
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
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.isOperation((ABracketType) type);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isOperation((ANamedInvariantType) type);
				}
				break;
			case AOperationType.kindPType:
				return true;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.isOperation((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.isOperation((AParameterType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.isOperation((AUnionType) type);
			case AUnknownType.kindPType:
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
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.getOperation((ABracketType) type);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getOperation((ANamedInvariantType) type);
				}
				break;
			case AOperationType.kindPType:
				return (AOperationType) type;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.getOperation((AOptionalType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.getOperation((AUnionType) type);
			case AUnknownType.kindPType:
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
			case SSeqType.kindPType:
				return true;
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.isSeq((ABracketType) type);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isSeq((ANamedInvariantType) type);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.isSeq((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.isSeq((AParameterType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.isSeq((AUnionType) type);
			case AUnknownType.kindPType:
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
			case SSeqType.kindPType:
				return (SSeqType) type;
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.getSeq((ABracketType) type);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getSeq((ANamedInvariantType) type);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.getSeq((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.getSeq((AParameterType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.getSeq((AUnionType) type);
			case AUnknownType.kindPType:
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
			case SMapType.kindPType:
				return SMapTypeAssistantTC.isMap((SMapType) type);
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.isMap((ABracketType) type);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isMap((ANamedInvariantType) type);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.isMap((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.isMap((AParameterType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.isMap((AUnionType) type);
			case AUnknownType.kindPType:
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
			case SMapType.kindPType:
				return SMapTypeAssistantTC.getMap((SMapType) type);
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.getMap(((ABracketType) type));
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getMap((ANamedInvariantType) type);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.getMap((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.getMap((AParameterType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.getMap((AUnionType) type);
			case AUnknownType.kindPType:
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
			case ASetType.kindPType:
				return ASetTypeAssistantTC.isSet((ASetType) type);
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.isSet((ABracketType) type);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isSet((ANamedInvariantType) type);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.isSet((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.isSet((AParameterType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.isSet((AUnionType) type);
			case AUnknownType.kindPType:
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
			case ASetType.kindPType:
				return ASetTypeAssistantTC.getSet((ASetType) type);
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.getSet((ABracketType) type);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getSet((ANamedInvariantType) type);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.getSet((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.getSet((AParameterType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.getSet((AUnionType) type);
			case AUnknownType.kindPType:
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
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.isRecord((ABracketType) type);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isRecord((ANamedInvariantType) type);
				} else if (type instanceof ARecordInvariantType)
				{
					return ARecordInvariantTypeAssistantTC.isRecord((ARecordInvariantType) type);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.isRecord((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.isRecord((AParameterType) type);
			case AUnionType.kindPType:
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

			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.getRecord((ABracketType) type);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getRecord((ANamedInvariantType) type);

				} else if (type instanceof ARecordInvariantType)
				{
					return ARecordInvariantTypeAssistantTC.getRecord((ARecordInvariantType) type);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.getRecord((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.getRecord((AParameterType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.getRecord((AUnionType) type);
			case AUnknownType.kindPType:
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

			case AClassType.kindPType:
				return AClassTypeAssistantTC.isClass((AClassType) type);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isClass((ANamedInvariantType) type);

				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.isClass((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.isClass((AParameterType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.isClass((AUnionType) type);
			case AUnknownType.kindPType:
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
			case AClassType.kindPType:
				if (type instanceof AClassType)
				{
					return (AClassType) type;
				}
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getClassType((ANamedInvariantType) type);

				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.getClassType((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.getClassType((AParameterType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.getClassType((AUnionType) type);
			case AUnknownType.kindPType:
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
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.getProduct((ABracketType) type);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getProduct((ANamedInvariantType) type);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.getProduct((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.getProduct((AProductType) type);
			case AProductType.kindPType:
				return AProductTypeAssistantTC.getProduct((AProductType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.getProduct((AUnionType) type);
			case AUnknownType.kindPType:
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
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.isProduct((ABracketType) type);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isProduct((ANamedInvariantType) type);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.isProduct((AOptionalType) type);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.isProduct((AParameterType) type);
			case AProductType.kindPType:
				return AProductTypeAssistantTC.isProduct((AProductType) type);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.isProduct((AUnionType) type);
			case AUnknownType.kindPType:
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
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.narrowerThan((ABracketType) type, accessSpecifier);
			case AFunctionType.kindPType:
				return AFunctionTypeAssistantTC.narrowerThan((AFunctionType) type, accessSpecifier);
			case AOperationType.kindPType:
				return AOperationTypeAssistantTC.narrowerThan((AOperationType) type, accessSpecifier);
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.narrowerThan((AOptionalType) type, accessSpecifier);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.narrowerThan((AParameterType) type, accessSpecifier);
			case SSeqType.kindPType:
				return SSeqTypeAssistantTC.narrowerThan((SSeqType) type, accessSpecifier);
			case ASetType.kindPType:
				return ASetTypeAssistantTC.narrowerThan((ASetType) type, accessSpecifier);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.narrowerThan((AUnionType) type, accessSpecifier);
			case AUnknownType.kindPType:
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
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.equals((ABracketType) type, other);
			case AClassType.kindPType:
				return AClassTypeAssistantTC.equals((AClassType) type, other);
			case AFunctionType.kindPType:
				return AFunctionTypeAssistantTC.equals((AFunctionType) type, other);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.equals((ANamedInvariantType) type, other);
				} else if (type instanceof ARecordInvariantType)
				{
					return ARecordInvariantTypeAssistantTC.equals((ARecordInvariantType) type, other);
				}
			case SMapType.kindPType:
				return SMapTypeAssistantTC.equals((SMapType) type, other);
			case AOperationType.kindPType:
				return AOperationTypeAssistantTC.equals((AOperationType) type, other);
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.equals((AOptionalType) type, other);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.equals((AParameterType) type, other);
			case AProductType.kindPType:
				return AProductTypeAssistantTC.equals((AProductType) type, other);
			case AQuoteType.kindPType:
				return AQuoteTypeAssistantTC.equals((AQuoteType) type, other);
			case SSeqType.kindPType:
				return SSeqTypeAssistantTC.equals((SSeqType) type, other);
			case ASetType.kindPType:
				return ASetTypeAssistantTC.equals((ASetType) type, other);
			case AUndefinedType.kindPType:
				return AUndefinedTypeAssistantTC.equals((AUndefinedType) type, other);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.equals((AUnionType) type, other);
			case AUnknownType.kindPType:
				return AUnknownTypeAssistantTC.equals((AUnknownType) type, other);
			case AUnresolvedType.kindPType:
				return AUnresolvedTypeAssistantTC.equals((AUnresolvedType) type, other);
			case AVoidType.kindPType:
				return AVoidTypeAssistantTC.equals((AVoidType) type, other);
			case AVoidReturnType.kindPType:
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
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.isType((ABracketType) exptype, typename);
			case SInvariantType.kindPType:
				if (exptype instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isType((ANamedInvariantType) exptype, typename);
				} else if (exptype instanceof ARecordInvariantType)
				{
					return ARecordInvariantTypeAssistantTC.isType((ARecordInvariantType) exptype, typename);
				}
				break;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.isType((AOptionalType) exptype, typename);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.isType((AParameterType) exptype, typename);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.isType((AUnionType) exptype, typename);
			case AUnknownType.kindPType:
				return AUnknownTypeAssistantTC.isType((AUnknownType) exptype, typename);
			case AUnresolvedType.kindPType:
				return AUnresolvedTypeAssistantTC.isType((AUnresolvedType) exptype, typename);
			case AVoidType.kindPType:
			case AVoidReturnType.kindPType:
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
			case SBasicType.kindPType:
				switch (((SBasicType) exptype).kindSBasicType())
				{
					case ABooleanBasicType.kindSBasicType:
						return "bool";
					case ACharBasicType.kindSBasicType:
						return "char";
					case SNumericBasicType.kindSBasicType:
						switch (((SNumericBasicType) exptype).kindSNumericBasicType())
						{
							case AIntNumericBasicType.kindSNumericBasicType:
								return "int";
							case ANatNumericBasicType.kindSNumericBasicType:
								return "nat";
							case ANatOneNumericBasicType.kindSNumericBasicType:
								return "nat1";
							case ARationalNumericBasicType.kindSNumericBasicType:
								return "rat";
							case ARealNumericBasicType.kindSNumericBasicType:
								return "real";
						}
					case ATokenBasicType.kindSBasicType:
						return "token";
				}
				break;
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.toDisplay((ABracketType) exptype);
			case AClassType.kindPType:
				return AClassTypeAssistantTC.toDisplay((AClassType) exptype);
			case AFunctionType.kindPType:
				return AFunctionTypeAssistantTC.toDisplay((AFunctionType) exptype);
			case SInvariantType.kindPType:
				switch (((SInvariantType) exptype).kindSInvariantType())
				{
					case ANamedInvariantType.kindSInvariantType:
						return ANamedInvariantTypeAssistantTC.toDisplay((ANamedInvariantType) exptype);
					case ARecordInvariantType.kindSInvariantType:
						return ARecordInvariantTypeAssistantTC.toDisplay((ARecordInvariantType) exptype);
				}
			case SMapType.kindPType:
				switch (((SMapType) exptype).kindSMapType())
				{
					case AInMapMapType.kindSMapType:
						return AInMapMapTypeAssistantTC.toDisplay((AInMapMapType) exptype);
					case AMapMapType.kindSMapType:
						return AMapMapTypeAssistantTC.toDisplay((AMapMapType) exptype);
				}
			case AOperationType.kindPType:
				return AOperationTypeAssistantTC.toDisplay((AOperationType) exptype);
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.toDisplay((AOptionalType) exptype);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.toDisplay((AParameterType) exptype);
			case AProductType.kindPType:
				return AProductTypeAssistantTC.toDisplay((AProductType) exptype);
			case AQuoteType.kindPType:
				return AQuoteTypeAssistantTC.toDisplay((AQuoteType) exptype);
			case SSeqType.kindPType:
				switch (((SSeqType) exptype).kindSSeqType())
				{
					case ASeqSeqType.kindSSeqType:
						return ASeqSeqTypeAssistantTC.toDisplay((ASeqSeqType) exptype);
					case ASeq1SeqType.kindSSeqType:
						return ASeq1SeqTypeAssistantTC.toDisplay((ASeq1SeqType) exptype);
				}
			case ASetType.kindPType:
				return ASetTypeAssistantTC.toDisplay((ASetType) exptype);
			case AUndefinedType.kindPType:
				return "(undefined)";
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.toDisplay((AUnionType) exptype);
			case AUnknownType.kindPType:
				return "?";
			case AUnresolvedType.kindPType:
				return AUnresolvedTypeAssistantTC.toDisplay((AUnresolvedType) exptype);
			case AVoidType.kindPType:
				return "()";
			case AVoidReturnType.kindPType:
				return "(return)";
		}
		assert false : "PTypeAssistant.toDisplay should not hit this case";
		return null;
	}

	public static boolean isProduct(PType type, int size)
	{
		switch (type.kindPType())
		{
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.isProduct((ABracketType) type, size);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.isProduct((ANamedInvariantType) type, size);
				} else
					return false;
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.isProduct((AOptionalType) type, size);
			case AParameterType.kindPType:
				return true;
			case AProductType.kindPType:
				return AProductTypeAssistantTC.isProduct((AProductType) type, size);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.isProduct((AUnionType) type, size);
			case AUnknownType.kindPType:
				return true;
			default:
				return false;
		}
	}

	public static AProductType getProduct(PType type, int size)
	{
		switch (type.kindPType())
		{
			case ABracketType.kindPType:
				return ABracketTypeAssistantTC.getProduct((ABracketType) type, size);
			case SInvariantType.kindPType:
				if (type instanceof ANamedInvariantType)
				{
					return ANamedInvariantTypeAssistantTC.getProduct((ANamedInvariantType) type, size);
				} else
				{
					assert false : "cannot getProduct from non-product type";
					return null;
				}
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantTC.getProduct((AOptionalType) type, size);
			case AParameterType.kindPType:
				return AParameterTypeAssistantTC.getProduct((AProductType) type, size);
			case AProductType.kindPType:
				return AProductTypeAssistantTC.getProduct((AProductType) type, size);
			case AUnionType.kindPType:
				return AUnionTypeAssistantTC.getProduct((AUnionType) type, size);
			case AUnknownType.kindPType:
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
		case AUnionType.kindPType:
			return AUnionTypeAssistantTC.isVoid((AUnionType) type);
		case AVoidType.kindPType:
		case AVoidReturnType.kindPType:
			return true;
		default:
			return false;
		}
	}

	public static boolean hasVoid(PType type) {
		switch (type.kindPType()) {
		case AUnionType.kindPType:
			return AUnionTypeAssistantTC.hasVoid((AUnionType) type);
		case AVoidType.kindPType:
		case AVoidReturnType.kindPType:
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
