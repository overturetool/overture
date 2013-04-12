package org.overture.interpreter.assistant.pattern;

import java.util.List;
import java.util.Vector;

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
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;

public class PPatternAssistantInterpreter extends PPatternAssistantTC
{
	/** A value for getLength meaning "any length" */
	protected static int ANY = -1;

	public static NameValuePairList getNamedValues(PPattern p, Value expval, Context ctxt) throws PatternMatchException
	{
		List<AIdentifierPattern> ids = findIdentifiers(p);

		// Go through the list of IDs, marking duplicate names as constrained. This is
		// because we have to permute sets that contain duplicate variables, so that
		// we catch permutations that match constrained values of the variable from
		// elsewhere in the pattern.

		int count = ids.size();

		for (int i=0; i<count; i++)
		{
			ILexNameToken iname = ids.get(i).getName();

			for (int j=i+1; j<count; j++)
			{
				if (iname.equals(ids.get(j).getName()))
				{
					ids.get(i).setConstrained(true);
					ids.get(j).setConstrained(true);
				}
			}
		}

		List<NameValuePairList> all = getAllNamedValues(p,expval, ctxt);
		return all.get(0);		// loose choice here!
	}


	public static List<AIdentifierPattern> findIdentifiers(PPattern pattern)
	{
		switch (pattern.kindPPattern())
		{
			case AConcatenationPattern.kindPPattern:
				return AConcatenationPatternAssistantInterpreter.findIdentifiers((AConcatenationPattern)pattern);
			case AIdentifierPattern.kindPPattern:
				return AIdentifierPatternAssistantInterpreter.findIdentifiers((AIdentifierPattern)pattern);
			case AMapPattern.kindPPattern:
				return AMapPatternAssistantInterpreter.findIdentifiers((AMapPattern)pattern);
			case AMapUnionPattern.kindPPattern:
				return AMapUnionPatternAssistantInterpreter.findIdentifiers((AMapUnionPattern)pattern);
			case ARecordPattern.kindPPattern:
				return ARecordPatternAssistantInterpreter.findIndentifiers((ARecordPattern) pattern);
			case ASeqPattern.kindPPattern:
				return ASeqPatternAssistantInterpreter.findIdentifiers((ASeqPattern) pattern);
			case ASetPattern.kindPPattern:
				return ASetPatternAssistantInterpreter.findIdentifiers((ASetPattern) pattern);
			case ATuplePattern.kindPPattern:
				return ATuplePatternAssistantInterpreter.findIdentifiers((ATuplePattern) pattern);
			case AUnionPattern.kindPPattern:
				return AUnionPatternAssistantInterpreter.findIdentifiers((AUnionPattern) pattern);
			default:
				return new Vector<AIdentifierPattern>();		// Most have none
		}
	}


	public static List<NameValuePairList> getAllNamedValues(PPattern p, Value expval,
			Context ctxt) throws PatternMatchException
	{
		//Abstract method, needs to be implemented by all subclasses
		switch (p.kindPPattern())
		{
			case ABooleanPattern.kindPPattern:
				return ABooleanPatternAssistantInterpreter.getAllNamedValues((ABooleanPattern)p,expval,ctxt);
			case ACharacterPattern.kindPPattern:
				return ACharacterPatternAssistantInterpreter.getAllNamedValues((ACharacterPattern)p,expval,ctxt);
			case AConcatenationPattern.kindPPattern:
				return AConcatenationPatternAssistantInterpreter.getAllNamedValues((AConcatenationPattern)p,expval,ctxt);
			case AExpressionPattern.kindPPattern:
				return AExpressionPatternAssistantInterpreter.getAllNamedValues((AExpressionPattern)p,expval,ctxt);
			case AIdentifierPattern.kindPPattern:
				return AIdentifierPatternAssistantInterpreter.getAllNamedValues((AIdentifierPattern)p,expval,ctxt);
			case AIgnorePattern.kindPPattern:
				return AIgnorePatternAssistantInterpreter.getAllNamedValues((AIgnorePattern)p,expval,ctxt);
			case AIntegerPattern.kindPPattern:
				return AIntegerPatternAssistantInterpreter.getAllNamedValues((AIntegerPattern)p,expval,ctxt);
			case AMapPattern.kindPPattern:
				return AMapPatternAssistantInterpreter.getAllNamedValues((AMapPattern)p,expval,ctxt);
			case AMapUnionPattern.kindPPattern:
				return AMapUnionPatternAssistantInterpreter.getAllNamedValues((AMapUnionPattern) p, expval,ctxt);
			case ANilPattern.kindPPattern:
				return ANilPatternAssistantInterpreter.getAllNamedValues((ANilPattern)p,expval,ctxt);
			case AQuotePattern.kindPPattern:
				return AQuotePatternAssistantInterpreter.getAllNamedValues((AQuotePattern)p,expval,ctxt);
			case ARealPattern.kindPPattern:
				return ARealPatternAssistantInterpreter.getAllNamedValues((ARealPattern)p,expval,ctxt);
			case ARecordPattern.kindPPattern:
				return ARecordPatternAssistantInterpreter.getAllNamedValues((ARecordPattern)p,expval,ctxt);
			case ASeqPattern.kindPPattern:
				return ASeqPatternAssistantInterpreter.getAllNamedValues((ASeqPattern)p,expval,ctxt);
			case ASetPattern.kindPPattern:
				return ASetPatternAssistantInterpreter.getAllNamedValues((ASetPattern)p,expval,ctxt);
			case AStringPattern.kindPPattern:
				return AStringPatternAssistantInterpreter.getAllNamedValues((AStringPattern)p,expval,ctxt);
			case ATuplePattern.kindPPattern:
				return ATuplePatternAssistantInterpreter.getAllNamedValues((ATuplePattern)p,expval,ctxt);
			case AUnionPattern.kindPPattern:
				return AUnionPatternAssistantInterpreter.getAllNamedValues((AUnionPattern)p,expval,ctxt);
			default:
				assert false : "Should not happen!";
				return null;
		}
	}

	/**
	 * @return The "length" of the pattern (eg. sequence and set patterns).
	 */

	public static int getLength(PPattern pattern)
	{
		switch (pattern.kindPPattern())
		{
			case AConcatenationPattern.kindPPattern:
				return AConcatenationPatternAssistantInterpreter.getLength((AConcatenationPattern) pattern);
			case AIdentifierPattern.kindPPattern:
				return AIdentifierPatternAssistantInterpreter.getLength((AIdentifierPattern) pattern);
			case AIgnorePattern.kindPPattern:
				return AIgnorePatternAssistantInterpreter.getLength((AIgnorePattern) pattern);
			case AMapPattern.kindPPattern:
				return AMapPatternAssistantInterpreter.getLength((AMapPattern) pattern);
			case AMapUnionPattern.kindPPattern:
				return AMapUnionPatternAssistantInterpreter.getLength((AMapUnionPattern)pattern);
			case ASeqPattern.kindPPattern:
				return ASeqPatternAssistantInterpreter.getLength((ASeqPattern) pattern);
			case ASetPattern.kindPPattern:
				return ASetPatternAssistantInterpreter.getLength((ASetPattern) pattern);
			case AStringPattern.kindPPattern:
				return AStringPatternAssistantInterpreter.getLength((AStringPattern)pattern);
			case AUnionPattern.kindPPattern:
				return AUnionPatternAssistantInterpreter.getLength((AUnionPattern) pattern);
			default:
				return 1;	// Most only identify one member
		}
	}

	/**
	 * @return True if the pattern has constraints, such that matching
	 * values should be permuted, where necessary, to find a match.
	 */
	public static boolean isConstrained(PPattern pattern)
	{
		switch (pattern.kindPPattern())
		{
			case AConcatenationPattern.kindPPattern:
				return AConcatenationPatternAssistantInterpreter.isConstrained((AConcatenationPattern)pattern);
			case AIdentifierPattern.kindPPattern:
				return AIdentifierPatternAssistantInterpreter.isConstrained((AIdentifierPattern)pattern);
			case AIgnorePattern.kindPPattern:
				return AIgnorePatternAssistantInterpreter.isConstrained((AIgnorePattern)pattern);
			case AMapPattern.kindPPattern:
				return AMapPatternAssistantInterpreter.isConstrained((AMapPattern)pattern);
			case AMapUnionPattern.kindPPattern:
				return AMapUnionPatternAssistantInterpreter.isConstrained((AMapUnionPattern)pattern);
			case ARecordPattern.kindPPattern:
				return ARecordPatternAssistantInterpreter.isConstrained((ARecordPattern) pattern);
			case ASeqPattern.kindPPattern:
				return ASeqPatternAssistantInterpreter.isConstrained((ASeqPattern) pattern);
			case ASetPattern.kindPPattern:
				return ASetPatternAssistantInterpreter.isConstrained((ASetPattern) pattern);
			case ATuplePattern.kindPPattern:
				return ATuplePatternAssistantInterpreter.isConstrained((ATuplePattern) pattern);
			case AUnionPattern.kindPPattern:
				return AUnionPatternAssistantInterpreter.isConstrained((AUnionPattern)pattern);
			default:
				return true;
		}
	}
}
