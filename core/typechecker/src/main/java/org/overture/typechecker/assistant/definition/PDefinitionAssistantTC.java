package org.overture.typechecker.assistant.definition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.definition.PDefinitionAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;
import org.overture.typechecker.util.HelpLexNameToken;
import org.overture.typechecker.utilities.NameFinder;
import org.overture.typechecker.utilities.TypeFinder;
import org.overture.typechecker.utilities.TypeResolver;

public class PDefinitionAssistantTC extends PDefinitionAssistant
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public PDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public boolean equals(PDefinition d, Object other) // Used for sets of definitions.
	{
		try
		{
			return d.apply(af.getDefinitionEqualityChecker(), other);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public static boolean hasSupertype(SClassDefinition aClassDefDefinition,
			PType other)
	{

		if (PTypeAssistantTC.equals(af.createPDefinitionAssistant().getType(aClassDefDefinition), other))
		{
			return true;
		} else
		{
			for (PType type : aClassDefDefinition.getSupertypes())
			{
				AClassType sclass = (AClassType) type;

				if (PTypeAssistantTC.hasSupertype(sclass, other))
				{
					return true;
				}
			}
		}
		return false;

	}

	public static boolean isFunctionOrOperation(PDefinition possible)
	{
		return isFunction(possible) || isOperation(possible);
	}

	public static PDefinition findType(List<PDefinition> definitions,
			ILexNameToken name, String fromModule)
	{

		for (PDefinition d : definitions)
		{
			PDefinition def = findType(d, name, fromModule);

			if (def != null)
			{
				return def;
			}
		}

		return null;

	}

	public static PDefinition findType(PDefinition d, ILexNameToken sought,
			String fromModule)
	{
		try
		{
			return d.apply(af.getTypeFinder(),new TypeFinder.Newquestion(sought, fromModule));// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public static PDefinition findName(PDefinition d, ILexNameToken sought,
			NameScope scope)
	{
		
		try
		{
			return d.apply(af.getNameFinder(),new NameFinder.Newquestion(sought, scope));// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}
		
	}

	public static PDefinition findNameBaseCase(PDefinition d,
			ILexNameToken sought, NameScope scope)
	{
		if (HelpLexNameToken.isEqual(d.getName(), sought))
		{
			if ((d.getNameScope() == NameScope.STATE && !scope.matches(NameScope.STATE))
					|| (d.getNameScope() == NameScope.OLDSTATE && !scope.matches(NameScope.OLDSTATE)))
			{

				TypeChecker.report(3302, "State variable '"
						+ sought.getFullName()
						+ "' cannot be accessed from this context", sought.getLocation());
			}

			markUsed(d);
			return d;
		}

		return null;

	}

	public static void markUsed(PDefinition d)
	{
		try
		{
			 d.apply(af.getUsedMarker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			
		}
	}

	public static void unusedCheck(PDefinition d)
	{
		try
		{
			 d.apply(af.getUnusedChecker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			
		}
	}

	public void unusedCheckBaseCase(PDefinition d)
	{
		if (!PDefinitionAssistantTC.isUsed(d))
		{
			TypeCheckerErrors.warning(5000, "Definition '" + d.getName()
					+ "' not used", d.getLocation(), d);
			markUsed(d); // To avoid multiple warnings
		}

	}

	public static List<PDefinition> getDefinitions(PDefinition d)
	{
		try
		{
			return d.apply(af.getDefinitionCollector());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public static PDefinition getSelfDefinition(PDefinition d)
	{
		try
		{
			return d.apply(af.getSelfDefinitionFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public static LexNameList getVariableNames(PDefinition d)
	{
		try
		{
			return d.apply(af.getVariableNameCollector());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public static boolean isStatic(PDefinition fdef)
	{
		return PAccessSpecifierAssistantTC.isStatic(fdef.getAccess());
	}

	public static PDefinition deref(PDefinition d)
	{
		try
		{
			return d.apply(af.getDereferer());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public static boolean isCallableOperation(PDefinition d)
	{
		try
		{
			return d.apply(af.getCallableOperationChecker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return false;
		}

	}

	public static boolean isUsed(PDefinition d)
	{
		try
		{
			return d.apply(af.getUsedChecker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return false;
		}

	}

	public static void implicitDefinitions(PDefinition d, Environment env)
	{
		try
		{
			 d.apply(af.getImplicitDefinitionFinder(), env);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			
		}

	}

	public static void typeResolve(PDefinition d,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{
		try
		{
			d.apply(af.getTypeResolver(), new TypeResolver.NewQuestion(rootVisitor, question));// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			
		}

	}

	public PType getType(PDefinition d)
	{
		try
		{
			return d.apply(af.getDefinitionTypeFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public boolean isUpdatable(PDefinition d)
	{
		try
		{
			return d.apply(af.getUpdatableChecker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public static String kind(PDefinition d)
	{
		try
		{
			return d.apply(af.getKindFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}


	}

	public static boolean isFunction(PDefinition d)
	{
		try
		{
			return d.apply(af.getFunctionChecker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	
	public static boolean isOperation(PDefinition d)
	{
		try
		{
			return d.apply(af.getOperationChecker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return false;
		}
	}


	/**
	 * Check a DefinitionList for incompatible duplicate pattern definitions.
	 */
	public static List<PDefinition> checkDuplicatePatterns(PDefinition d,
			List<PDefinition> defs)
	{
		Set<PDefinition> noDuplicates = new HashSet<PDefinition>();

		for (PDefinition d1 : defs)
		{
			for (PDefinition d2 : defs)
			{
				if (d1 != d2 && d1.getName() != null && d2.getName() != null
						&& d1.getName().equals(d2.getName()))
				{
					if (!TypeComparator.compatible(d1.getType(), d2.getType()))
					{
						TypeCheckerErrors.report(3322, "Duplicate patterns bind to different types", d.getLocation(), d);
						TypeCheckerErrors.detail2(d1.getName().getName(), d1.getType(), d2.getName().getName(), d2.getType());
					}
				}
			}

			noDuplicates.add(d1);
		}

		return new Vector<PDefinition>(noDuplicates);
	}

}
