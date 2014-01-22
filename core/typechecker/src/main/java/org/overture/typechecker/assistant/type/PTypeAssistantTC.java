package org.overture.typechecker.assistant.type;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.assistant.type.PTypeAssistant;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.utilities.type.ConcreateTypeImplementor;
import org.overture.typechecker.utilities.type.PTypeResolver;

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
		return af.createPDefinitionAssistant().hasSupertype(cto.getClassdef(), other);
	}

	public static boolean isType(PType type, Class<? extends PType> typeclass)
	{
		try
		{
			return type.apply(af.getPTypeExtendedChecker(), typeclass);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return false;
		}

	}

	public PType polymorph(PType type, ILexNameToken pname,
			PType actualType)
	{
		try
		{
			return type.apply(af.getConcreateTypeImplementor(), new ConcreateTypeImplementor.Newquestion(pname, actualType));// FIXME:
																																// should
																																// we
																																// handle
																																// exceptions
																																// like
																																// this
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public boolean isUnknown(PType type)
	{
		if (type instanceof AUnionType)
		{
			return af.createAUnionTypeAssistant().isUnknown((AUnionType) type);
		} else if (type instanceof AUnknownType)
		{
			return true;
		}
		return false;
	}

	public boolean isUnion(PType type)
	{
		try
		{
			return type.apply(af.getUnionBasisChecker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public AUnionType getUnion(PType type)
	{
		try
		{
			return type.apply(af.getUnionTypeFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public boolean isFunction(PType type)
	{
		try
		{
			return type.apply(af.getPTypeFunctionChecker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public AFunctionType getFunction(PType type)
	{
		try
		{
			return type.apply(af.getFunctionTypeFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public PType typeResolve(PType type, ATypeDefinition root,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question)
	{
		try
		{
			return type.apply(af.getPTypeResolver(), new PTypeResolver.Newquestion(root, rootVisitor, question));
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public void unResolve(PType type)
	{
		try
		{
			type.apply(af.getTypeUnresolver());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{

		}
	}

	public boolean isOperation(PType type)
	{
		try
		{
			return type.apply(af.getOperationBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public AOperationType getOperation(PType type)
	{
		try
		{
			return type.apply(af.getOperationTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public boolean isSeq(PType type)
	{
		try
		{
			return type.apply(af.getSeqBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public SSeqType getSeq(PType type)
	{
		try
		{
			return type.apply(af.getSeqTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public boolean isMap(PType type)
	{
		try
		{
			return type.apply(af.getMapBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public SMapType getMap(PType type)
	{
		try
		{
			return type.apply(af.getMapTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public boolean isSet(PType type)
	{
		try
		{
			return type.apply(af.getSetBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public ASetType getSet(PType type)
	{
		try
		{
			return type.apply(af.getSetTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public boolean isRecord(PType type)
	{
		try
		{
			return type.apply(af.getRecordBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public boolean isTag(PType type)
	{
		try
		{
			return type.apply(af.getTagBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public ARecordInvariantType getRecord(PType type)
	{
		try
		{
			return type.apply(af.getRecordTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public boolean isClass(PType type)
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

	public AProductType getProduct(PType type)
	{
		try
		{
			return type.apply(af.getProductTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public boolean isProduct(PType type)
	{
		try
		{
			return type.apply(af.getProductBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public boolean narrowerThan(PType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
	{
		try
		{
			return type.apply(af.getNarrowerThanComparator(), accessSpecifier);
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public boolean narrowerThanBaseCase(PType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
	{
		if (type.getDefinitions() != null)
		{
			boolean result = false;
			for (PDefinition d : type.getDefinitions())
			{
				result = result
						|| af.createPAccessSpecifierAssistant().narrowerThan(d.getAccess(), accessSpecifier);
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
			return type.apply(af.getTypeEqualityChecker(), other);
		} catch (AnalysisException e)
		{
			return false;
		}

	}

	public static PType deBracket(PType other)
	{

		while (other instanceof ABracketType)
		{
			other = ((ABracketType) other).getType();
		}

		return other;
	}

	public PType isType(PType type, String typename)
	{
		try
		{
			return type.apply(af.getPTypeFinder(), typename);
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public String toDisplay(PType type)
	{
		try
		{
			return type.apply(af.getTypeDisplayer());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public boolean isProduct(PType type, int size)
	{
		try
		{
			return type.apply(af.getProductExtendedChecker(), size);
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public AProductType getProduct(PType type, int size)
	{
		try
		{
			return type.apply(af.getProductExtendedTypeFinder(), size);
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public boolean equals(LinkedList<PType> parameters,
			LinkedList<PType> other)
	{

		if (parameters.size() != other.size())
		{
			return false;
		}

		for (int i = 0; i < parameters.size(); i++)
		{
			if (!af.createPTypeAssistant().equals(parameters.get(i), other.get(i)))
			{
				return false;
			}
		}

		return true;
	}

	public boolean isVoid(PType type)
	{
		try
		{
			return type.apply(af.getVoidBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public boolean hasVoid(PType type)
	{
		try
		{
			return type.apply(af.getVoidExistanceChecker());
		} catch (AnalysisException e)
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

	public PTypeList getComposeTypes(PType type)
	{
		try
		{
			return type.apply(af.getComposeTypeCollector());
		}
		catch (AnalysisException e)
		{
			return new PTypeList();
		}
	}
}
