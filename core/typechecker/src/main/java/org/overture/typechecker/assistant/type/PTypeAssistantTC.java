package org.overture.typechecker.assistant.type;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.type.PTypeAssistant;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SInvariantTypeBase;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class PTypeAssistantTC extends PTypeAssistant
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public PTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static boolean hasSupertype(AClassType cto, PType other)
	{
		return PDefinitionAssistantTC.hasSupertype(cto.getClassdef(), other);
	}

	public static boolean isType(PType type, Class<? extends PType> typeclass)
	{
		if (type instanceof ABracketType)
		{
			return ABracketTypeAssistantTC.isType((ABracketType) type, typeclass);
		} else if (type instanceof SInvariantType)
		{
			if (type instanceof ANamedInvariantType)
			{
				return ANamedInvariantTypeAssistantTC.isType((ANamedInvariantType) type, typeclass);
			}
		} else if (type instanceof AOptionalType)
		{
			return AOptionalTypeAssistantTC.isType((AOptionalType) type, typeclass);
		} else if (type instanceof AUnionType)
		{
			return AUnionTypeAssistantTC.isType((AUnionType) type, typeclass);
		} else if (type instanceof AUnknownType)
		{
			return AUnknownTypeAssistantTC.isType((AUnknownType) type, typeclass);
		}
		return typeclass.isInstance(type);

	}

	public static PType polymorph(PType type, ILexNameToken pname,
			PType actualType)
	{
		if (type instanceof AParameterType)
		{
			return AParameterTypeAssistantTC.polymorph((AParameterType) type, pname, actualType);
		} else if (type instanceof AFunctionType)
		{
			return AFunctionTypeAssistantTC.polymorph((AFunctionType) type, pname, actualType);
		} else if (type instanceof SMapType)
		{
			return SMapTypeAssistantTC.polymorph((SMapType) type, pname, actualType);
		} else if (type instanceof AOptionalType)
		{
			return AOptionalTypeAssistantTC.polymorph((AOptionalType) type, pname, actualType);
		} else if (type instanceof AProductType)
		{
			return AProductTypeAssistantTC.polymorph((AProductType) type, pname, actualType);
		} else if (type instanceof SSeqType)
		{
			return SSeqTypeAssistantTC.polymorph((SSeqType) type, pname, actualType);
		} else if (type instanceof ASetType)
		{
			return ASetTypeAssistantTC.polymorph((ASetType) type, pname, actualType);
		} else if (type instanceof AUnionType)
		{
			return AUnionTypeAssistantTC.polymorph((AUnionType) type, pname, actualType);
		} else
		{
			return type;
		}

	}

	public static boolean isUnknown(PType type)
	{
		if (type instanceof AUnionType)
		{
			return AUnionTypeAssistantTC.isUnknown((AUnionType) type);
		} else if (type instanceof AUnknownType)
		{
			return true;
		}
		return false;
	}

	public static boolean isUnion(PType type)
	{
		if (type instanceof ABracketType)
		{
			return ABracketTypeAssistantTC.isUnion((ABracketType) type);
		} else if (type instanceof SInvariantType)
		{
			if (type instanceof ANamedInvariantType)
			{
				return ANamedInvariantTypeAssistantTC.isUnion((ANamedInvariantType) type);
			}
		} else if (type instanceof AUnionType)
		{
			return AUnionTypeAssistantTC.isUnion((AUnionType) type);
		}
		return false;
	}

	public static AUnionType getUnion(PType type)
	{
		try
		{
			return type.apply(af.getUnionTypeFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public static boolean isFunction(PType type)
	{
		try
		{
			return type.apply(af.getPTypeFunctionChecker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public static AFunctionType getFunction(PType type)
	{
		if (type instanceof ABracketType)
		{
			return ABracketTypeAssistantTC.getFunction((ABracketType) type);
		} else if (type instanceof AFunctionType)
		{
			return (AFunctionType) type;
		} else if (type instanceof SInvariantType)
		{
			if (type instanceof ANamedInvariantType)
			{
				return ANamedInvariantTypeAssistantTC.getFunction((ANamedInvariantType) type);
			}
		} else if (type instanceof AOptionalType)
		{
			return AOptionalTypeAssistantTC.getFunction((AOptionalType) type);
		} else if (type instanceof AUnionType)
		{
			return af.createAUnionTypeAssistant().getFunction((AUnionType) type);
		} else if (type instanceof AUnknownType)
		{
			return AUnknownTypeAssistantTC.getFunction((AUnknownType) type);
		}
		assert false : "Can't getFunction of a non-function";
		return null;
	}

	public  PType typeResolve(PType type, ATypeDefinition root,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question)
	{

		PType result = null;

		if (type instanceof ABracketType)
		{
			result = ABracketTypeAssistantTC.typeResolve((ABracketType) type, root, rootVisitor, question);
		} else if (type instanceof AClassType)
		{
			result = AClassTypeAssistantTC.typeResolve((AClassType) type, root, rootVisitor, question);
		} else if (type instanceof AFunctionType)
		{
			result = AFunctionTypeAssistantTC.typeResolve((AFunctionType) type, root, rootVisitor, question);
		} else if (type instanceof SInvariantType)
		{
			if (type instanceof ANamedInvariantType)
			{
				result = ANamedInvariantTypeAssistantTC.typeResolve((ANamedInvariantType) type, root, rootVisitor, question);
			} else if (type instanceof ARecordInvariantType)
			{
				result = ARecordInvariantTypeAssistantTC.typeResolve((ARecordInvariantType) type, root, rootVisitor, question);
			}
		} else if (type instanceof SMapType)
		{
			result = SMapTypeAssistantTC.typeResolve((SMapType) type, root, rootVisitor, question);
		} else if (type instanceof AOperationType)
		{
			result = AOperationTypeAssistantTC.typeResolve((AOperationType) type, root, rootVisitor, question);
		} else if (type instanceof AOptionalType)
		{
			result = AOptionalTypeAssistantTC.typeResolve((AOptionalType) type, root, rootVisitor, question);
		} else if (type instanceof AParameterType)
		{
			result = AParameterTypeAssistantTC.typeResolve((AParameterType) type, root, rootVisitor, question);
		} else if (type instanceof AProductType)
		{
			result = AProductTypeAssistantTC.typeResolve((AProductType) type, root, rootVisitor, question);
		} else if (type instanceof SSeqType)
		{
			result = SSeqTypeAssistantTC.typeResolve((SSeqType) type, root, rootVisitor, question);
		} else if (type instanceof ASetType)
		{
			result = ASetTypeAssistantTC.typeResolve((ASetType) type, root, rootVisitor, question);
		} else if (type instanceof AUnionType)
		{
			result = AUnionTypeAssistantTC.typeResolve((AUnionType) type, root, rootVisitor, question);
		} else if (type instanceof AUnresolvedType)
		{
			result = AUnresolvedTypeAssistantTC.typeResolve((AUnresolvedType) type, root, rootVisitor, question);
		} else
		{
			type.setResolved(true);
			result = type;
		}
		return result;
	}

	public static void unResolve(PType type)
	{
		try
		{
			type.apply(af.getTypeUnresolver());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			
		}
//		if (type instanceof ABracketType)
//		{
//			ABracketTypeAssistantTC.unResolve((ABracketType) type);
//		} else if (type instanceof AClassType)
//		{
//			AClassTypeAssistantTC.unResolve((AClassType) type);
//		} else if (type instanceof AFunctionType)
//		{
//			AFunctionTypeAssistantTC.unResolve((AFunctionType) type);
//		} else if (type instanceof SInvariantType)
//		{
//			if (type instanceof ANamedInvariantType)
//			{
//				ANamedInvariantTypeAssistantTC.unResolve((ANamedInvariantType) type);
//			} else if (type instanceof ARecordInvariantType)
//			{
//				ARecordInvariantTypeAssistantTC.unResolve((ARecordInvariantType) type);
//			}
//		} else if (type instanceof SMapType)
//		{
//			SMapTypeAssistantTC.unResolve((SMapType) type);
//		} else if (type instanceof AOperationType)
//		{
//			if (type instanceof AOperationType)
//			{
//				AOperationTypeAssistantTC.unResolve((AOperationType) type);
//			}
//		} else if (type instanceof AOptionalType)
//		{
//			AOptionalTypeAssistantTC.unResolve((AOptionalType) type);
//		} else if (type instanceof AProductType)
//		{
//			AProductTypeAssistantTC.unResolve((AProductType) type);
//		} else if (type instanceof SSeqType)
//		{
//			SSeqTypeAssistantTC.unResolve((SSeqType) type);
//		} else if (type instanceof ASetType)
//		{
//			ASetTypeAssistantTC.unResolve((ASetType) type);
//		} else if (type instanceof AUnionType)
//		{
//			AUnionTypeAssistantTC.unResolve((AUnionType) type);
//		} else
//		{
//			type.setResolved(false);
//		}

	}

	public static boolean isOperation(PType type)
	{
		try
		{
			return type.apply(af.getOperationBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public static AOperationType getOperation(PType type)
	{
		try
		{
			return type.apply(af.getOperationTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public static boolean isSeq(PType type)
	{
		try
		{
			return type.apply(af.getSeqBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public static SSeqType getSeq(PType type)
	{
		try
		{
			return type.apply(af.getSeqTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public static boolean isMap(PType type)
	{
		try
		{
			return type.apply(af.getMapBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public static SMapType getMap(PType type)
	{
		try
		{
			return type.apply(af.getMapTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public static boolean isSet(PType type)
	{
		try
		{
			return type.apply(af.getSetBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public static ASetType getSet(PType type)
	{
		try
		{
			return type.apply(af.getSetTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public static boolean isRecord(PType type)
	{
		try
		{
			return type.apply(af.getRecordBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public static ARecordInvariantType getRecord(PType type)
	{
		try
		{
			return type.apply(af.getRecordTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public static boolean isClass(PType type)
	{
		try
		{
			return type.apply(af.getClassBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public static AClassType getClassType(PType type)
	{
		try
		{
			return type.apply(af.getClassTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public static AProductType getProduct(PType type)
	{
		try
		{
			return type.apply(af.getProductTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
		
	}

	public static boolean isProduct(PType type)
	{
		try
		{
			return type.apply(af.getProductBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
//		if (type instanceof ABracketType)
//		{
//			return ABracketTypeAssistantTC.isProduct((ABracketType) type);
//		} else if (type instanceof SInvariantType)
//		{
//			if (type instanceof ANamedInvariantType)
//			{
//				return ANamedInvariantTypeAssistantTC.isProduct((ANamedInvariantType) type);
//			}
//		} else if (type instanceof AOptionalType)
//		{
//			return AOptionalTypeAssistantTC.isProduct((AOptionalType) type);
//		} else if (type instanceof AProductType)
//		{
//			return AProductTypeAssistantTC.isProduct((AProductType) type);
//		} else if (type instanceof AUnionType)
//		{
//			return AUnionTypeAssistantTC.isProduct((AUnionType) type);
//		} else if (type instanceof AUnknownType)
//		{
//			return true;
//		}
//		return false;
	}

	public static boolean narrowerThan(PType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
	{
		try
		{
			return type.apply(af.getNarrowerThanComparator(),accessSpecifier);
		} catch (AnalysisException e)
		{
			return false;
		}
//		if (type instanceof ABracketType)
//		{
//			return ABracketTypeAssistantTC.narrowerThan((ABracketType) type, accessSpecifier);
//		} else if (type instanceof AFunctionType)
//		{
//			return AFunctionTypeAssistantTC.narrowerThan((AFunctionType) type, accessSpecifier);
//		} else if (type instanceof AOperationType)
//		{
//			return AOperationTypeAssistantTC.narrowerThan((AOperationType) type, accessSpecifier);
//		} else if (type instanceof AOptionalType)
//		{
//			return AOptionalTypeAssistantTC.narrowerThan((AOptionalType) type, accessSpecifier);
//		} else if (type instanceof SSeqType)
//		{
//			return SSeqTypeAssistantTC.narrowerThan((SSeqType) type, accessSpecifier);
//		} else if (type instanceof ASetType)
//		{
//			return ASetTypeAssistantTC.narrowerThan((ASetType) type, accessSpecifier);
//		} else if (type instanceof AUnionType)
//		{
//			return AUnionTypeAssistantTC.narrowerThan((AUnionType) type, accessSpecifier);
//		} else if (type instanceof AUnknownType)
//		{
//			return AUnknownTypeAssistantTC.narrowerThan((AUnknownType) type, accessSpecifier);
//		} else if (type instanceof SInvariantTypeBase)
//		{
//			if (type instanceof ANamedInvariantType)
//			{
//				return ANamedInvariantTypeAssistantTC.narrowerThan((ANamedInvariantType) type, accessSpecifier);
//			} else if (type instanceof ARecordInvariantType)
//			{
//				return ARecordInvariantTypeAssistantTC.narrowerThan((ARecordInvariantType) type, accessSpecifier);
//			}
//			return narrowerThanBaseCase(type, accessSpecifier);
//		} else
//		{
//			return narrowerThanBaseCase(type, accessSpecifier);
//		}
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
		try
		{
			return type.apply(af.getTypeEqualityChecker(),other);
		} catch (AnalysisException e)
		{
			return false;
		}
//		if (type instanceof ABracketType)
//		{
//			return ABracketTypeAssistantTC.equals((ABracketType) type, other);
//		} else if (type instanceof AClassType)
//		{
//			return AClassTypeAssistantTC.equals((AClassType) type, other);
//		} else if (type instanceof AFunctionType)
//		{
//			return AFunctionTypeAssistantTC.equals((AFunctionType) type, other);
//		} else if (type instanceof SInvariantType)
//		{
//			if (type instanceof ANamedInvariantType)
//			{
//				return ANamedInvariantTypeAssistantTC.equals((ANamedInvariantType) type, other);
//			} else if (type instanceof ARecordInvariantType)
//			{
//				return ARecordInvariantTypeAssistantTC.equals((ARecordInvariantType) type, other);
//			}
//		} else if (type instanceof SMapType)
//		{
//			return SMapTypeAssistantTC.equals((SMapType) type, other);
//		} else if (type instanceof AOperationType)
//		{
//			return AOperationTypeAssistantTC.equals((AOperationType) type, other);
//		} else if (type instanceof AOptionalType)
//		{
//			return AOptionalTypeAssistantTC.equals((AOptionalType) type, other);
//		} else if (type instanceof AProductType)
//		{
//			return AProductTypeAssistantTC.equals((AProductType) type, other);
//		} else if (type instanceof AQuoteType)
//		{
//			return AQuoteTypeAssistantTC.equals((AQuoteType) type, other);
//		} else if (type instanceof SSeqType)
//		{
//			return SSeqTypeAssistantTC.equals((SSeqType) type, other);
//		} else if (type instanceof ASetType)
//		{
//			return ASetTypeAssistantTC.equals((ASetType) type, other);
//		} else if (type instanceof AUndefinedType)
//		{
//			return AUndefinedTypeAssistantTC.equals((AUndefinedType) type, other);
//		} else if (type instanceof AUnionType)
//		{
//			return AUnionTypeAssistantTC.equals((AUnionType) type, other);
//		} else if (type instanceof AUnknownType)
//		{
//			return AUnknownTypeAssistantTC.equals((AUnknownType) type, other);
//		} else if (type instanceof AUnresolvedType)
//		{
//			return AUnresolvedTypeAssistantTC.equals((AUnresolvedType) type, other);
//		} else if (type instanceof AVoidType)
//		{
//			return AVoidTypeAssistantTC.equals((AVoidType) type, other);
//		} else if (type instanceof AVoidReturnType)
//		{
//			return AVoidReturnTypeAssistantTC.equals((AVoidReturnType) type, other);
//		}
//
//		other = deBracket(other);
//		return type.getClass() == other.getClass();

	}

	public static PType deBracket(PType other)
	{

		while (other instanceof ABracketType)
		{
			other = ((ABracketType) other).getType();
		}

		return other;
	}

	public static PType isType(PType type, String typename)
	{
		if (type instanceof ABracketType)
		{
			return ABracketTypeAssistantTC.isType((ABracketType) type, typename);
		} else if (type instanceof SInvariantType)
		{
			if (type instanceof ANamedInvariantType)
			{
				return ANamedInvariantTypeAssistantTC.isType((ANamedInvariantType) type, typename);
			} else if (type instanceof ARecordInvariantType)
			{
				return ARecordInvariantTypeAssistantTC.isType((ARecordInvariantType) type, typename);
			}
		} else if (type instanceof AOptionalType)
		{
			return AOptionalTypeAssistantTC.isType((AOptionalType) type, typename);
		} else if (type instanceof AUnionType)
		{
			return AUnionTypeAssistantTC.isType((AUnionType) type, typename);
		} else if (type instanceof AUnknownType)
		{
			return AUnknownTypeAssistantTC.isType((AUnknownType) type, typename);
		} else if (type instanceof AUnresolvedType)
		{
			return AUnresolvedTypeAssistantTC.isType((AUnresolvedType) type, typename);
		}

		return (PTypeAssistantTC.toDisplay(type).equals(typename)) ? type
				: null;
	}

	private static String toDisplay(PType type)
	{
		try
		{
			return type.apply(af.getTypeDisplayer());
		} catch (AnalysisException e)
		{
			return null;
		}
//		if (type instanceof SBasicType)
//		{
//			if (type instanceof ABooleanBasicType)
//			{
//				return "bool";
//			} else if (type instanceof ACharBasicType)
//			{
//				return "char";
//			} else if (type instanceof SNumericBasicType)
//			{
//				if (type instanceof AIntNumericBasicType)
//				{
//					return "int";
//				} else if (type instanceof ANatNumericBasicType)
//				{
//					return "nat";
//				} else if (type instanceof ANatOneNumericBasicType)
//				{
//					return "nat1";
//				} else if (type instanceof ARationalNumericBasicType)
//				{
//					return "rat";
//				} else if (type instanceof ARealNumericBasicType)
//				{
//					return "real";
//				}
//			} else if (type instanceof ATokenBasicType)
//			{
//				return "token";
//			}
//		} else if (type instanceof ABracketType)
//		{
//			return ABracketTypeAssistantTC.toDisplay((ABracketType) type);
//		} else if (type instanceof AClassType)
//		{
//			return AClassTypeAssistantTC.toDisplay((AClassType) type);
//		} else if (type instanceof AFunctionType)
//		{
//			return AFunctionTypeAssistantTC.toDisplay((AFunctionType) type);
//		} else if (type instanceof SInvariantType)
//		{
//			if (type instanceof ANamedInvariantType)
//			{
//				return ANamedInvariantTypeAssistantTC.toDisplay((ANamedInvariantType) type);
//			} else if (type instanceof ARecordInvariantType)
//			{
//				return ARecordInvariantTypeAssistantTC.toDisplay((ARecordInvariantType) type);
//			}
//		} else if (type instanceof SMapType)
//		{
//			if (type instanceof AInMapMapType)
//			{
//				return AInMapMapTypeAssistantTC.toDisplay((AInMapMapType) type);
//			} else if (type instanceof AMapMapType)
//			{
//				return AMapMapTypeAssistantTC.toDisplay((AMapMapType) type);
//			}
//		} else if (type instanceof AOperationType)
//		{
//			return AOperationTypeAssistantTC.toDisplay((AOperationType) type);
//		} else if (type instanceof AOptionalType)
//		{
//			return AOptionalTypeAssistantTC.toDisplay((AOptionalType) type);
//		} else if (type instanceof AParameterType)
//		{
//			return AParameterTypeAssistantTC.toDisplay((AParameterType) type);
//		} else if (type instanceof AProductType)
//		{
//			return AProductTypeAssistantTC.toDisplay((AProductType) type);
//		} else if (type instanceof AQuoteType)
//		{
//			return AQuoteTypeAssistantTC.toDisplay((AQuoteType) type);
//		} else if (type instanceof SSeqType)
//		{
//			if (type instanceof ASeqSeqType)
//			{
//				return ASeqSeqTypeAssistantTC.toDisplay((ASeqSeqType) type);
//			} else if (type instanceof ASeq1SeqType)
//			{
//				return ASeq1SeqTypeAssistantTC.toDisplay((ASeq1SeqType) type);
//			}
//		} else if (type instanceof ASetType)
//		{
//			return ASetTypeAssistantTC.toDisplay((ASetType) type);
//		} else if (type instanceof AUndefinedType)
//		{
//			return "(undefined)";
//		} else if (type instanceof AUnionType)
//		{
//			return AUnionTypeAssistantTC.toDisplay((AUnionType) type);
//		} else if (type instanceof AUnknownType)
//		{
//			return "?";
//		} else if (type instanceof AUnresolvedType)
//		{
//			return AUnresolvedTypeAssistantTC.toDisplay((AUnresolvedType) type);
//		} else if (type instanceof AVoidType)
//		{
//			return "()";
//		} else if (type instanceof AVoidReturnType)
//		{
//			return "(return)";
//		}
//		assert false : "PTypeAssistant.toDisplay should not hit this case";
//		return null;
	}

	public static boolean isProduct(PType type, int size)
	{
		if (type instanceof ABracketType)
		{
			return ABracketTypeAssistantTC.isProduct((ABracketType) type, size);
		} else if (type instanceof SInvariantType)
		{
			if (type instanceof ANamedInvariantType)
			{
				return ANamedInvariantTypeAssistantTC.isProduct((ANamedInvariantType) type, size);
			}
			return false;
		} else if (type instanceof AOptionalType)
		{
			return AOptionalTypeAssistantTC.isProduct((AOptionalType) type, size);
		} else if (type instanceof AParameterType)
		{
			return true;
		} else if (type instanceof AProductType)
		{
			return AProductTypeAssistantTC.isProduct((AProductType) type, size);
		} else if (type instanceof AUnionType)
		{
			return AUnionTypeAssistantTC.isProduct((AUnionType) type, size);
		} else if (type instanceof AUnknownType)
		{
			return true;
		} else
		{
			return false;
		}
	}

	public static AProductType getProduct(PType type, int size)
	{
		if (type instanceof ABracketType)
		{
			return ABracketTypeAssistantTC.getProduct((ABracketType) type, size);
		} else if (type instanceof SInvariantType)
		{
			if (type instanceof ANamedInvariantType)
			{
				return ANamedInvariantTypeAssistantTC.getProduct((ANamedInvariantType) type, size);
			}
			assert false : "cannot getProduct from non-product type";
			return null;
		} else if (type instanceof AOptionalType)
		{
			return AOptionalTypeAssistantTC.getProduct((AOptionalType) type, size);
		} else if (type instanceof AProductType)
		{
			return AProductTypeAssistantTC.getProduct((AProductType) type, size);
		} else if (type instanceof AUnionType)
		{
			return AUnionTypeAssistantTC.getProduct((AUnionType) type, size);
		} else if (type instanceof AUnknownType)
		{
			return AUnknownTypeAssistantTC.getProduct((AUnknownType) type, size);
		} else
		{
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

	public static boolean isVoid(PType type)
	{
		if (type instanceof AUnionType)
		{
			return AUnionTypeAssistantTC.isVoid((AUnionType) type);
		} else if (type instanceof AVoidType || type instanceof AVoidReturnType)
		{
			return true;
		} else
		{
			return false;
		}
	}

	public static boolean hasVoid(PType type)
	{
		if (type instanceof AUnionType)
		{
			return AUnionTypeAssistantTC.hasVoid((AUnionType) type);
		} else if (type instanceof AVoidType || type instanceof AVoidReturnType)
		{
			return true;
		} else
		{
			return false;
		}
	}

	public static Object deBracket(Object other)
	{
		while (other instanceof ABracketType)
		{
			other = ((ABracketType) other).getType();
		}

		return other;
	}

}
