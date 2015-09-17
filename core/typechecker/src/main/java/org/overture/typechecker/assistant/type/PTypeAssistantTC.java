/*
 * #%~
 * The VDM Type Checker
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.typechecker.assistant.type;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.assistant.type.PTypeAssistant;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexLocation;
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
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.utilities.type.ConcreateTypeImplementor;
import org.overture.typechecker.utilities.type.PTypeResolver;

public class PTypeAssistantTC extends PTypeAssistant implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public PTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public boolean hasSupertype(AClassType cto, PType other)
	{
		return af.createPDefinitionAssistant().hasSupertype(cto.getClassdef(), other);
	}

	public boolean isType(PType type, Class<? extends PType> typeclass)
	{
		try
		{
			return type.apply(af.getPTypeExtendedChecker(), typeclass);
		} catch (AnalysisException e)
		{
			return false;
		}

	}

	public PType polymorph(PType type, ILexNameToken pname, PType actualType)
	{
		try
		{
			return type.apply(af.getConcreateTypeImplementor(), new ConcreateTypeImplementor.Newquestion(pname, actualType));

		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public boolean isUnknown(PType type)
	{
		if (type instanceof AUnionType)
		{
			return isUnknown((AUnionType) type);
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
			return type.apply(af.getUnionBasisChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public AUnionType getUnion(PType type)
	{
		try
		{
			return type.apply(af.getUnionTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public boolean isFunction(PType type)
	{
		try
		{
			return type.apply(af.getPTypeFunctionChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public AFunctionType getFunction(PType type)
	{
		try
		{
			return type.apply(af.getFunctionTypeFinder());
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
			type.apply(af.getTypeUnresolver());
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

	public boolean isClass(PType type, Environment env)
	{
		try
		{
			return type.apply(af.getClassBasisChecker(env));
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public AClassType getClassType(PType type, Environment env)
	{
		try
		{
			return type.apply(af.getClassTypeFinder(env));
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

	public boolean equals(PType type, Object other)
	{
		try
		{
			return type.apply(af.getTypeEqualityChecker(), other);
		} catch (AnalysisException e)
		{
			return false;
		}

	}

	public PType deBracket(PType other)
	{

		while (other instanceof ABracketType)
		{
			other = ((ABracketType) other).getType();
		}

		return other;
	}

	// public static Object deBracket(Object other) // used at pog-string-base, problematic conversion.
	// {
	// while (other instanceof ABracketType)
	// {
	// other = ((ABracketType) other).getType();
	// }
	//
	// return other;
	// }

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

	public boolean equals(LinkedList<PType> parameters, LinkedList<PType> other)
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

	public Object deBracket(Object other)
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
		} catch (AnalysisException e)
		{
			return new PTypeList();
		}
	}

	public PType checkConstraint(PType constraint, PType actual,
			ILexLocation location)
	{
		if (constraint != null)
		{
			if (!af.getTypeComparator().isSubType(actual, constraint, true))
			{
				TypeChecker.report(3327, "Value is not of the right type", location);
				TypeChecker.detail2("Actual", actual, "Expected", constraint);
			}
		}

		return actual;
	}

	public PType possibleConstraint(PType constraint, PType actual,
			ILexLocation location)
	{
		if (constraint != null)
		{
			if (!af.getTypeComparator().compatible(constraint, actual))
			{
				TypeChecker.report(3327, "Value is not of the right type", location);
				TypeChecker.detail2("Actual", actual, "Expected", constraint);
			}
		}

		return actual;
	}

	public PType checkReturnType(PType constraint, PType actual,
			ILexLocation location)
	{
		PTypeAssistantTC assistant = af.createPTypeAssistant();

		if (constraint != null && !(actual instanceof AVoidType)
				&& !assistant.isUnknown(actual))
		{
			if (assistant.hasVoid(actual) && !(constraint instanceof AVoidType))
			{
				TypeChecker.report(3328, "Statement may return void value", location);
				TypeChecker.detail2("Actual", actual, "Expected", constraint);
			} else if (!af.getTypeComparator().compatible(constraint, actual))
			{
				TypeChecker.report(3327, "Value is not of the right type", location);
				TypeChecker.detail2("Actual", actual, "Expected", constraint);
			}
		}

		return actual;
	}
	
	public boolean isUnknown(AUnionType type)
	{
		for (PType t : type.getTypes())
		{
			if (af.createPTypeAssistant().isUnknown(t))
			{
				return true;
			}
		}

		return false;
	}
}
