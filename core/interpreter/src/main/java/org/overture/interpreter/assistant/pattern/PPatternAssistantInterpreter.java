package org.overture.interpreter.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.patterns.ARealPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.AStringPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;

public class PPatternAssistantInterpreter extends PPatternAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PPatternAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	/** A value for getLength meaning "any length" */
	public static int ANY = -1;

	public static NameValuePairList getNamedValues(PPattern p, Value expval,
			Context ctxt) throws PatternMatchException
	{
		List<AIdentifierPattern> ids = findIdentifiers(p);

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

	public static List<AIdentifierPattern> findIdentifiers(PPattern pattern)
	{
		try
		{
			return pattern.apply(af.getIdentifierPatternFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return new Vector<AIdentifierPattern>(); // Most have none
		}
//		if (pattern instanceof AConcatenationPattern)
//		{
//			return AConcatenationPatternAssistantInterpreter.findIdentifiers((AConcatenationPattern) pattern);
//		} else if (pattern instanceof AIdentifierPattern)
//		{
//			return AIdentifierPatternAssistantInterpreter.findIdentifiers((AIdentifierPattern) pattern);
//		} else if (pattern instanceof AMapPattern)
//		{
//			return AMapPatternAssistantInterpreter.findIdentifiers((AMapPattern) pattern);
//		} else if (pattern instanceof AMapUnionPattern)
//		{
//			return AMapUnionPatternAssistantInterpreter.findIdentifiers((AMapUnionPattern) pattern);
//		} else if (pattern instanceof ARecordPattern)
//		{
//			return ARecordPatternAssistantInterpreter.findIndentifiers((ARecordPattern) pattern);
//		} else if (pattern instanceof ASeqPattern)
//		{
//			return ASeqPatternAssistantInterpreter.findIdentifiers((ASeqPattern) pattern);
//		} else if (pattern instanceof ASetPattern)
//		{
//			return ASetPatternAssistantInterpreter.findIdentifiers((ASetPattern) pattern);
//		} else if (pattern instanceof ATuplePattern)
//		{
//			return ATuplePatternAssistantInterpreter.findIdentifiers((ATuplePattern) pattern);
//		} else if (pattern instanceof AUnionPattern)
//		{
//			return AUnionPatternAssistantInterpreter.findIdentifiers((AUnionPattern) pattern);
//		} else
//		{
//			return new Vector<AIdentifierPattern>(); // Most have none
//		}
	}

	public static List<NameValuePairList> getAllNamedValues(PPattern pattern,
			Value expval, Context ctxt) throws PatternMatchException
	{
		if (pattern instanceof ABooleanPattern)
		{
			return ABooleanPatternAssistantInterpreter.getAllNamedValues((ABooleanPattern) pattern, expval, ctxt);
		} else if (pattern instanceof ACharacterPattern)
		{
			return ACharacterPatternAssistantInterpreter.getAllNamedValues((ACharacterPattern) pattern, expval, ctxt);
		} else if (pattern instanceof AConcatenationPattern)
		{
			return AConcatenationPatternAssistantInterpreter.getAllNamedValues((AConcatenationPattern) pattern, expval, ctxt);
		} else if (pattern instanceof AExpressionPattern)
		{
			return AExpressionPatternAssistantInterpreter.getAllNamedValues((AExpressionPattern) pattern, expval, ctxt);
		} else if (pattern instanceof AIdentifierPattern)
		{
			return AIdentifierPatternAssistantInterpreter.getAllNamedValues((AIdentifierPattern) pattern, expval, ctxt);
		} else if (pattern instanceof AIgnorePattern)
		{
			return AIgnorePatternAssistantInterpreter.getAllNamedValues((AIgnorePattern) pattern, expval, ctxt);
		} else if (pattern instanceof AIntegerPattern)
		{
			return AIntegerPatternAssistantInterpreter.getAllNamedValues((AIntegerPattern) pattern, expval, ctxt);
		} else if (pattern instanceof AMapPattern)
		{
			return AMapPatternAssistantInterpreter.getAllNamedValues((AMapPattern) pattern, expval, ctxt);
		} else if (pattern instanceof AMapUnionPattern)
		{
			return AMapUnionPatternAssistantInterpreter.getAllNamedValues((AMapUnionPattern) pattern, expval, ctxt);
		} else if (pattern instanceof ANilPattern)
		{
			return ANilPatternAssistantInterpreter.getAllNamedValues((ANilPattern) pattern, expval, ctxt);
		} else if (pattern instanceof AQuotePattern)
		{
			return AQuotePatternAssistantInterpreter.getAllNamedValues((AQuotePattern) pattern, expval, ctxt);
		} else if (pattern instanceof ARealPattern)
		{
			return ARealPatternAssistantInterpreter.getAllNamedValues((ARealPattern) pattern, expval, ctxt);
		} else if (pattern instanceof ARecordPattern)
		{
			return ARecordPatternAssistantInterpreter.getAllNamedValues((ARecordPattern) pattern, expval, ctxt);
		} else if (pattern instanceof ASeqPattern)
		{
			return ASeqPatternAssistantInterpreter.getAllNamedValues((ASeqPattern) pattern, expval, ctxt);
		} else if (pattern instanceof ASetPattern)
		{
			return ASetPatternAssistantInterpreter.getAllNamedValues((ASetPattern) pattern, expval, ctxt);
		} else if (pattern instanceof AStringPattern)
		{
			return AStringPatternAssistantInterpreter.getAllNamedValues((AStringPattern) pattern, expval, ctxt);
		} else if (pattern instanceof ATuplePattern)
		{
			return ATuplePatternAssistantInterpreter.getAllNamedValues((ATuplePattern) pattern, expval, ctxt);
		} else if (pattern instanceof AUnionPattern)
		{
			return AUnionPatternAssistantInterpreter.getAllNamedValues((AUnionPattern) pattern, expval, ctxt);
		} else
		{
			assert false : "Should not happen!";
			return null;
		}
	}

	/**
	 * @return The "length" of the pattern (eg. sequence and set patterns).
	 */

	public static int getLength(PPattern pattern)
	{
		try
		{
			return pattern.apply(af.getLengthFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return 1; // Most have none
		}
//		if (pattern instanceof AConcatenationPattern)
//		{
//			return AConcatenationPatternAssistantInterpreter.getLength((AConcatenationPattern) pattern);
//		} else if (pattern instanceof AIdentifierPattern)
//		{
//			return AIdentifierPatternAssistantInterpreter.getLength((AIdentifierPattern) pattern);
//		} else if (pattern instanceof AIgnorePattern)
//		{
//			return AIgnorePatternAssistantInterpreter.getLength((AIgnorePattern) pattern);
//		} else if (pattern instanceof AMapPattern)
//		{
//			return AMapPatternAssistantInterpreter.getLength((AMapPattern) pattern);
//		} else if (pattern instanceof AMapUnionPattern)
//		{
//			return AMapUnionPatternAssistantInterpreter.getLength((AMapUnionPattern) pattern);
//		} else if (pattern instanceof ASeqPattern)
//		{
//			return ASeqPatternAssistantInterpreter.getLength((ASeqPattern) pattern);
//		} else if (pattern instanceof ASetPattern)
//		{
//			return ASetPatternAssistantInterpreter.getLength((ASetPattern) pattern);
//		} else if (pattern instanceof AStringPattern)
//		{
//			return AStringPatternAssistantInterpreter.getLength((AStringPattern) pattern);
//		} else if (pattern instanceof AUnionPattern)
//		{
//			return AUnionPatternAssistantInterpreter.getLength((AUnionPattern) pattern);
//		} else
//		{
//			return 1; // Most only identify one member
//		}
	}

	/**
	 * @return True if the pattern has constraints, such that matching values should be permuted, where necessary, to
	 *         find a match.
	 */
	public static boolean isConstrained(PPattern pattern)
	{
		if (pattern instanceof AConcatenationPattern)
		{
			return AConcatenationPatternAssistantInterpreter.isConstrained((AConcatenationPattern) pattern);
		} else if (pattern instanceof AIdentifierPattern)
		{
			return AIdentifierPatternAssistantInterpreter.isConstrained((AIdentifierPattern) pattern);
		} else if (pattern instanceof AIgnorePattern)
		{
			return AIgnorePatternAssistantInterpreter.isConstrained((AIgnorePattern) pattern);
		} else if (pattern instanceof AMapPattern)
		{
			return AMapPatternAssistantInterpreter.isConstrained((AMapPattern) pattern);
		} else if (pattern instanceof AMapUnionPattern)
		{
			return AMapUnionPatternAssistantInterpreter.isConstrained((AMapUnionPattern) pattern);
		} else if (pattern instanceof ARecordPattern)
		{
			return ARecordPatternAssistantInterpreter.isConstrained((ARecordPattern) pattern);
		} else if (pattern instanceof ASeqPattern)
		{
			return ASeqPatternAssistantInterpreter.isConstrained((ASeqPattern) pattern);
		} else if (pattern instanceof ASetPattern)
		{
			return ASetPatternAssistantInterpreter.isConstrained((ASetPattern) pattern);
		} else if (pattern instanceof ATuplePattern)
		{
			return ATuplePatternAssistantInterpreter.isConstrained((ATuplePattern) pattern);
		} else if (pattern instanceof AUnionPattern)
		{
			return AUnionPatternAssistantInterpreter.isConstrained((AUnionPattern) pattern);
		} else
		{
			return true;
		}
	}
}
