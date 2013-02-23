package org.overture.interpreter.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.lex.LexNameToken;
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
			LexNameToken iname = ids.get(i).getName();

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
			case CONCATENATION:
				return AConcatenationPatternAssistantInterpreter.findIdentifiers((AConcatenationPattern)pattern);
			case IDENTIFIER:
				return AIdentifierPatternAssistantInterpreter.findIdentifiers((AIdentifierPattern)pattern);
			case MAP:
				return AMapPatternAssistantInterpreter.findIdentifiers((AMapPattern)pattern);
			case MAPUNION:
				return AMapUnionPatternAssistantInterpreter.findIdentifiers((AMapUnionPattern)pattern);
			case RECORD:
				return ARecordPatternAssistantInterpreter.findIndentifiers((ARecordPattern) pattern);
			case SEQ:
				return ASeqPatternAssistantInterpreter.findIdentifiers((ASeqPattern) pattern);
			case SET:
				return ASetPatternAssistantInterpreter.findIdentifiers((ASetPattern) pattern);
			case TUPLE:
				return ATuplePatternAssistantInterpreter.findIdentifiers((ATuplePattern) pattern);
			case UNION:
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
			case BOOLEAN:
				return ABooleanPatternAssistantInterpreter.getAllNamedValues((ABooleanPattern)p,expval,ctxt);
			case CHARACTER:
				return ACharacterPatternAssistantInterpreter.getAllNamedValues((ACharacterPattern)p,expval,ctxt);
			case CONCATENATION:
				return AConcatenationPatternAssistantInterpreter.getAllNamedValues((AConcatenationPattern)p,expval,ctxt);
			case EXPRESSION:
				return AExpressionPatternAssistantInterpreter.getAllNamedValues((AExpressionPattern)p,expval,ctxt);
			case IDENTIFIER:
				return AIdentifierPatternAssistantInterpreter.getAllNamedValues((AIdentifierPattern)p,expval,ctxt);
			case IGNORE:
				return AIgnorePatternAssistantInterpreter.getAllNamedValues((AIgnorePattern)p,expval,ctxt);
			case INTEGER:
				return AIntegerPatternAssistantInterpreter.getAllNamedValues((AIntegerPattern)p,expval,ctxt);
			case MAP:
				return AMapPatternAssistantInterpreter.getAllNamedValues((AMapPattern)p,expval,ctxt);
			case MAPUNION:
				return AMapUnionPatternAssistantInterpreter.getAllNamedValues((AMapUnionPattern) p, expval,ctxt);
			case NIL:
				return ANilPatternAssistantInterpreter.getAllNamedValues((ANilPattern)p,expval,ctxt);
			case QUOTE:
				return AQuotePatternAssistantInterpreter.getAllNamedValues((AQuotePattern)p,expval,ctxt);
			case REAL:
				return ARealPatternAssistantInterpreter.getAllNamedValues((ARealPattern)p,expval,ctxt);
			case RECORD:
				return ARecordPatternAssistantInterpreter.getAllNamedValues((ARecordPattern)p,expval,ctxt);
			case SEQ:
				return ASeqPatternAssistantInterpreter.getAllNamedValues((ASeqPattern)p,expval,ctxt);
			case SET:
				return ASetPatternAssistantInterpreter.getAllNamedValues((ASetPattern)p,expval,ctxt);
			case STRING:
				return AStringPatternAssistantInterpreter.getAllNamedValues((AStringPattern)p,expval,ctxt);
			case TUPLE:
				return ATuplePatternAssistantInterpreter.getAllNamedValues((ATuplePattern)p,expval,ctxt);
			case UNION:
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
			case CONCATENATION:
				return AConcatenationPatternAssistantInterpreter.getLength((AConcatenationPattern) pattern);
			case IDENTIFIER:
				return AIdentifierPatternAssistantInterpreter.getLength((AIdentifierPattern) pattern);
			case IGNORE:
				return AIgnorePatternAssistantInterpreter.getLength((AIgnorePattern) pattern);
			case MAP:
				return AMapPatternAssistantInterpreter.getLength((AMapPattern) pattern);
			case MAPUNION:
				return AMapUnionPatternAssistantInterpreter.getLength((AMapUnionPattern)pattern);
			case SEQ:
				return ASeqPatternAssistantInterpreter.getLength((ASeqPattern) pattern);
			case SET:
				return ASetPatternAssistantInterpreter.getLength((ASetPattern) pattern);
			case STRING:
				return AStringPatternAssistantInterpreter.getLength((AStringPattern)pattern);
			case UNION:
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
			case CONCATENATION:
				return AConcatenationPatternAssistantInterpreter.isConstrained((AConcatenationPattern)pattern);
			case IDENTIFIER:
				return AIdentifierPatternAssistantInterpreter.isConstrained((AIdentifierPattern)pattern);
			case IGNORE:
				return AIgnorePatternAssistantInterpreter.isConstrained((AIgnorePattern)pattern);
			case MAP:
				return AMapPatternAssistantInterpreter.isConstrained((AMapPattern)pattern);
			case MAPUNION:
				return AMapUnionPatternAssistantInterpreter.isConstrained((AMapUnionPattern)pattern);
			case RECORD:
				return ARecordPatternAssistantInterpreter.isConstrained((ARecordPattern) pattern);
			case SEQ:
				return ASeqPatternAssistantInterpreter.isConstrained((ASeqPattern) pattern);
			case SET:
				return ASetPatternAssistantInterpreter.isConstrained((ASetPattern) pattern);
			case TUPLE:
				return ATuplePatternAssistantInterpreter.isConstrained((ATuplePattern) pattern);
			case UNION:
				return AUnionPatternAssistantInterpreter.isConstrained((AUnionPattern)pattern);
			default:
				return true;
		}
	}
}
