package org.overture.interpreter.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.utilities.pattern.AllNamedValuesLocator;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;

public class PPatternAssistantInterpreter extends PPatternAssistantTC implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PPatternAssistantInterpreter(IInterpreterAssistantFactory af, String fromModule)
	{
		super(af, fromModule);
		this.af = af;
	}

	/** A value for getLength meaning "any length" */
	public static int ANY = -1;

	public NameValuePairList getNamedValues(PPattern p, Value expval,
			Context ctxt) throws AnalysisException
	{
		List<AIdentifierPattern> ids = af.createPPatternAssistant(fromModule).findIdentifiers(p);

		// Go through the list of IDs, marking duplicate names as constrained. This is
		// because we have to permute sets that contain duplicate variables, so that
		// we catch permutations that match constrained values of the variable from
		// elsewhere in the pattern.

		int count = ids.size();

		for (int i = 0; i < count; i++)
		{
			ILexNameToken iname = ids.get(i).getName();

			for (int j = i + 1; j < count; j++)
			{
				if (iname.equals(ids.get(j).getName()))
				{
					ids.get(i).setConstrained(true);
					ids.get(j).setConstrained(true);
				}
			}
		}

		List<NameValuePairList> all = getAllNamedValues(p, expval, ctxt);
		return all.get(0); // loose choice here!
	}

	public List<AIdentifierPattern> findIdentifiers(PPattern pattern)
	{
		try
		{
			return pattern.apply(af.getIdentifierPatternFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return new Vector<AIdentifierPattern>(); // Most have none
		}

	}

	public List<NameValuePairList> getAllNamedValues(PPattern pattern,
			Value expval, Context ctxt) throws AnalysisException
	{

		return pattern.apply(af.getAllNamedValuesLocator(fromModule), new AllNamedValuesLocator.Newquestion(expval, ctxt));
	}

	/**
	 * @param pattern
	 * @return The "length" of the pattern (eg. sequence and set patterns).
	 */

	public int getLength(PPattern pattern)
	{
		try
		{
			return pattern.apply(af.getLengthFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return 1; // Most only identify one member
		}
	}

	/**
	 * @param pattern
	 * @return True if the pattern has constraints, such that matching values should be permuted, where necessary, to
	 *         find a match.
	 */
	public boolean isConstrained(PPattern pattern)
	{
		try
		{
			return pattern.apply(af.getConstrainedPatternChecker());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return true;
		}

	}
}
