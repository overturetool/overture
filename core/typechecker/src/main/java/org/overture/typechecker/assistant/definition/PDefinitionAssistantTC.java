package org.overture.typechecker.assistant.definition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.definition.PDefinitionAssistant;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.utilities.DefinitionFinder;
import org.overture.typechecker.utilities.DefinitionTypeResolver;
import org.overture.typechecker.utilities.NameFinder;

//TODO Add assistant Javadoc
/**
 * Top-Level assistant. Will probably remain present for conveniency's sake but the static access will be disallowed.
 * 
 * @author ldc
 */
public class PDefinitionAssistantTC extends PDefinitionAssistant
{
	protected ITypeCheckerAssistantFactory af;

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

	public boolean hasSupertype(SClassDefinition aClassDefDefinition,
			PType other)
	{

		if (af.createPTypeAssistant().equals(af.createPDefinitionAssistant().getType(aClassDefDefinition), other))
		{
			return true;
		} else
		{
			for (PType type : aClassDefDefinition.getSupertypes())
			{
				AClassType sclass = (AClassType) type;

				if (af.createPTypeAssistant().hasSupertype(sclass, other))
				{
					return true;
				}
			}
		}
		return false;

	}

	public boolean isFunctionOrOperation(PDefinition possible)
	{
		return af.createPDefinitionAssistant().isFunction(possible)
				|| af.createPDefinitionAssistant().isOperation(possible);
	}

	public PDefinition findType(List<PDefinition> definitions,
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

	public PDefinition findType(PDefinition d, ILexNameToken sought,
			String fromModule)
	{
		try
		{
			return d.apply(af.getDefinitionFinder(), new DefinitionFinder.Newquestion(sought, fromModule));// FIXME:
																											// should we
			// handle exceptions
			// like this
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public PDefinition findName(PDefinition d, ILexNameToken sought,
			NameScope scope)
	{

		try
		{
			return d.apply(af.getNameFinder(), new NameFinder.Newquestion(sought, scope));// FIXME: should we handle
																							// exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public PDefinition findNameBaseCase(PDefinition d, ILexNameToken sought,
			NameScope scope)
	{
		if (af.getLexNameTokenAssistant().isEqual(d.getName(), sought))
		{
			if (d.getNameScope() == NameScope.STATE
					&& !scope.matches(NameScope.STATE)
					|| d.getNameScope() == NameScope.OLDSTATE
					&& !scope.matches(NameScope.OLDSTATE))
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

	public void markUsed(PDefinition d)
	{
		try
		{
			d.apply(af.getUsedMarker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{

		}
	}

	public void unusedCheck(PDefinition d)
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
		if (!af.createPDefinitionAssistant().isUsed(d))
		{
			TypeCheckerErrors.warning(5000, "Definition '" + d.getName()
					+ "' not used", d.getLocation(), d);
			markUsed(d); // To avoid multiple warnings
		}

	}

	public List<PDefinition> getDefinitions(PDefinition d)
	{
		try
		{
			return d.apply(af.getDefinitionCollector());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public PDefinition getSelfDefinition(PDefinition d)
	{
		try
		{
			return d.apply(af.getSelfDefinitionFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public LexNameList getVariableNames(PDefinition d)
	{
		try
		{
			return d.apply(af.getVariableNameCollector());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public boolean isStatic(PDefinition fdef)
	{
		return af.createPAccessSpecifierAssistant().isStatic(fdef.getAccess());
	}

	public PDefinition deref(PDefinition d)
	{
		try
		{
			return d.apply(af.getDereferer());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public boolean isCallableOperation(PDefinition d)
	{
		try
		{
			return d.apply(af.getCallableOperationChecker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return false;
		}

	}

	public boolean isUsed(PDefinition d)
	{
		try
		{
			return d.apply(af.getUsedChecker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return false;
		}

	}

	public void implicitDefinitions(PDefinition d, Environment env)
	{
		try
		{
			d.apply(af.getImplicitDefinitionFinder(), env);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{

		}

	}

	public void typeResolve(PDefinition d,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{
		try
		{
			d.apply(af.getDefinitionTypeResolver(), new DefinitionTypeResolver.NewQuestion(rootVisitor, question));// FIXME:
																													// should
																													// we
			// handle exceptions
			// like this
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

	public String kind(PDefinition d)
	{
		try
		{
			return d.apply(af.getKindFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public boolean isFunction(PDefinition d)
	{
		try
		{
			return d.apply(af.getFunctionChecker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public boolean isOperation(PDefinition d)
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
	 * 
	 * @param d
	 * @param defs
	 * @return
	 */
	public List<PDefinition> checkDuplicatePatterns(PDefinition d,
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
					if (!af.getTypeComparator().compatible(d1.getType(), d2.getType()))
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

	public boolean isSubclassResponsibility(PDefinition d)
	{
		while (d instanceof AInheritedDefinition)
		{
			AInheritedDefinition aid = (AInheritedDefinition) d;
			d = aid.getSuperdef();
		}

		if (d instanceof AExplicitOperationDefinition)
		{
			AExplicitOperationDefinition op = (AExplicitOperationDefinition) d;
			return op.getBody() instanceof ASubclassResponsibilityStm;
		} else if (d instanceof AImplicitOperationDefinition)
		{
			AImplicitOperationDefinition op = (AImplicitOperationDefinition) d;
			return op.getBody() instanceof ASubclassResponsibilityStm;
		} else if (d instanceof AExplicitFunctionDefinition)
		{
			AExplicitFunctionDefinition fn = (AExplicitFunctionDefinition) d;
			return fn.getBody() instanceof ASubclassResponsibilityExp;
		} else if (d instanceof AImplicitFunctionDefinition)
		{
			AImplicitFunctionDefinition fn = (AImplicitFunctionDefinition) d;
			return fn.getBody() instanceof ASubclassResponsibilityExp;
		}

		return false;
	}
}
