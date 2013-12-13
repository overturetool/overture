package org.overture.typechecker.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.pattern.PPatternAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionSet;
import org.overture.typechecker.utilities.pattern.AllDefinitionLocator;
import org.overture.typechecker.utilities.pattern.PatternResolver;

public class PPatternAssistantTC extends PPatternAssistant
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public PPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	/**
	 * Get a set of definitions for the pattern's variables. Note that if the pattern includes duplicate variable names,
	 * these are collapse into one.
	 */
	public static List<PDefinition> getDefinitions(PPattern rp, PType ptype,
			NameScope scope)
	{
		PDefinitionSet set = af.createPDefinitionSet();
		set.addAll(af.createPPatternAssistant().getAllDefinitions(rp, ptype, scope));
		List<PDefinition> result = new Vector<PDefinition>(set);
		return result;
	}

	/**
	 * Get a complete list of all definitions, including duplicates. This method should only be used only by PP
	 */
	private List<PDefinition> getAllDefinitions(PPattern pattern,
			PType ptype, NameScope scope)
	{
		try
		{
			return pattern.apply(af.getAllDefinitionLocator(),new AllDefinitionLocator.NewQuestion(ptype, scope));
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public static void typeResolve(PPattern pattern,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{
		try
		{
			pattern.apply(af.getPatternResolver(), new PatternResolver.NewQuestion(rootVisitor, question));
		} catch (AnalysisException e)
		{
			
		}
	}

	public static void unResolve(PPattern pattern)
	{
		try
		{
			pattern.apply(af.getPatternUnresolver());
		} catch (AnalysisException e)
		{
			
		}
	}

	public static PType getPossibleType(PPattern pattern)
	{
		try
		{
			return pattern.apply(af.getPossibleTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public static boolean matches(PPattern pattern, PType expType)
	{
		return TypeComparator.compatible(getPossibleType(pattern), expType);
	}

	public static PExp getMatchingExpression(PPattern pattern)
	{
		try
		{
			return pattern.apply(af.getMatchingExpressionFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public static boolean isSimple(PPattern pattern)
	{
		try
		{
			return pattern.apply(af.getSimplePatternChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public static boolean alwaysMatches(PPattern pattern)
	{
		try
		{
			return pattern.apply(af.getAlwaysMatchingPatternChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

}
