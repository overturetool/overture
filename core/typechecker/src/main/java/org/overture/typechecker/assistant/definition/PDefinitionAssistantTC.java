package org.overture.typechecker.assistant.definition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.definition.PDefinitionAssistant;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
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
		if (d instanceof AExternalDefinition)
		{
			AExternalDefinitionAssistantTC.markUsed((AExternalDefinition) d);
		} else if (d instanceof AImportedDefinition)
		{
			AImportedDefinitionAssistantTC.markUsed((AImportedDefinition) d);
		} else if (d instanceof AInheritedDefinition)
		{
			AInheritedDefinitionAssistantTC.markUsed((AInheritedDefinition) d);
		} else if (d instanceof ARenamedDefinition)
		{
			ARenamedDefinitionAssistantTC.markUsed((ARenamedDefinition) d);
			d.setUsed(true);
		} else
		{
			d.setUsed(true);
		}
	}

	public static void unusedCheck(PDefinition d)
	{
		if (d instanceof AEqualsDefinition)
		{
			AEqualsDefinitionAssistantTC.unusedCheck((AEqualsDefinition) d);
		} else if (d instanceof AMultiBindListDefinition)
		{
			AMultiBindListDefinitionAssistantTC.unusedCheck((AMultiBindListDefinition) d);
		} else if (d instanceof AStateDefinition)
		{
			AStateDefinitionAssistantTC.unusedCheck((AStateDefinition) d);
		} else if (d instanceof AValueDefinition)
		{
			AValueDefinitionAssistantTC.unusedCheck((AValueDefinition) d);
		} else
		{
			unusedCheckBaseCase(d);
		}
	}

	public static void unusedCheckBaseCase(PDefinition d)
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
		if (d instanceof AImportedDefinition)
		{
			if (d instanceof AImportedDefinition)
			{
				return deref(((AImportedDefinition) d).getDef());
			}
		} else if (d instanceof AInheritedDefinition)
		{
			if (d instanceof AInheritedDefinition)
			{
				return deref(((AInheritedDefinition) d).getSuperdef());
			}
		} else if (d instanceof ARenamedDefinition)
		{
			if (d instanceof ARenamedDefinition)
			{
				return deref(((ARenamedDefinition) d).getDef());
			}
		}
		return d;

	}

	public static boolean isCallableOperation(PDefinition d)
	{
		if (d instanceof AExplicitOperationDefinition)
		{
			return true;
		} else if (d instanceof AImplicitOperationDefinition)
		{
			return ((AImplicitOperationDefinition) d).getBody() != null;
		} else if (d instanceof AImportedDefinition)
		{
			return isCallableOperation(((AImportedDefinition) d).getDef());
		} else if (d instanceof AInheritedDefinition)
		{
			return isCallableOperation(((AInheritedDefinition) d).getSuperdef());
		} else if (d instanceof ARenamedDefinition)
		{
			return isCallableOperation(((ARenamedDefinition) d).getDef());
		} else
		{
			return false;
		}
	}

	public static boolean isUsed(PDefinition d)
	{
		if (d instanceof AExternalDefinition)
		{
			return AExternalDefinitionAssistantTC.isUsed((AExternalDefinition) d);
		} else if (d instanceof AInheritedDefinition)
		{
			return AInheritedDefinitionAssistantTC.isUsed((AInheritedDefinition) d);
		} else
		{
			return d.getUsed();
		}

	}

	public static void implicitDefinitions(PDefinition d, Environment env)
	{
		if (d instanceof SClassDefinition)
		{
			SClassDefinitionAssistantTC.implicitDefinitions((SClassDefinition) d, env);
		} else if (d instanceof AClassInvariantDefinition)
		{
		} else if (d instanceof AEqualsDefinition)
		{
		} else if (d instanceof AExplicitFunctionDefinition)
		{
			AExplicitFunctionDefinitionAssistantTC.implicitDefinitions((AExplicitFunctionDefinition) d, env);
		} else if (d instanceof AExplicitOperationDefinition)
		{
			AExplicitOperationDefinitionAssistantTC.implicitDefinitions((AExplicitOperationDefinition) d, env);
		} else if (d instanceof AImplicitFunctionDefinition)
		{
			AImplicitFunctionDefinitionAssistantTC.implicitDefinitions((AImplicitFunctionDefinition) d, env);
		} else if (d instanceof AImplicitOperationDefinition)
		{
			AImplicitOperationDefinitionAssistantTC.implicitDefinitions((AImplicitOperationDefinition) d, env);
		} else if (d instanceof AStateDefinition)
		{
			AStateDefinitionAssistantTC.implicitDefinitions((AStateDefinition) d, env);
		} else if (d instanceof AThreadDefinition)
		{
			AThreadDefinitionAssistantTC.implicitDefinitions((AThreadDefinition) d, env);
		} else if (d instanceof ATypeDefinition)
		{
			ATypeDefinitionAssistantTC.implicitDefinitions((ATypeDefinition) d, env);
		} else
		{
			return;
		}

	}

	public static void typeResolve(PDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{
		if (d instanceof SClassDefinition)
		{
			SClassDefinitionAssistantTC.typeResolve((SClassDefinition) d, rootVisitor, question);
		} else if (d instanceof AExplicitFunctionDefinition)
		{
			AExplicitFunctionDefinitionAssistantTC.typeResolve((AExplicitFunctionDefinition) d, rootVisitor, question);
		} else if (d instanceof AExplicitOperationDefinition)
		{
			AExplicitOperationDefinitionAssistantTC.typeResolve((AExplicitOperationDefinition) d, rootVisitor, question);
		} else if (d instanceof AImplicitFunctionDefinition)
		{
			AImplicitFunctionDefinitionAssistantTC.typeResolve((AImplicitFunctionDefinition) d, rootVisitor, question);
		} else if (d instanceof AImplicitOperationDefinition)
		{
			AImplicitOperationDefinitionAssistantTC.typeResolve((AImplicitOperationDefinition) d, rootVisitor, question);
		} else if (d instanceof AInstanceVariableDefinition)
		{
			AInstanceVariableDefinitionAssistantTC.typeResolve((AInstanceVariableDefinition) d, rootVisitor, question);
		} else if (d instanceof ALocalDefinition)
		{
			ALocalDefinitionAssistantTC.typeResolve((ALocalDefinition) d, rootVisitor, question);
		} else if (d instanceof ARenamedDefinition)
		{
			ARenamedDefinitionAssistantTC.typeResolve((ARenamedDefinition) d, rootVisitor, question);
		} else if (d instanceof AStateDefinition)
		{
			AStateDefinitionAssistantTC.typeResolve((AStateDefinition) d, rootVisitor, question);
		} else if (d instanceof ATypeDefinition)
		{
			ATypeDefinitionAssistantTC.typeResolve((ATypeDefinition) d, rootVisitor, question);
		} else if (d instanceof AValueDefinition)
		{
			AValueDefinitionAssistantTC.typeResolve((AValueDefinition) d, rootVisitor, question);
		} else
		{
			return;
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

	public static boolean isUpdatable(PDefinition d)
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
