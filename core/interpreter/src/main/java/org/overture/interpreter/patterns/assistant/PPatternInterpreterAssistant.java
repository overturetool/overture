package org.overture.interpreter.patterns.assistant;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.overture.ast.types.AOptionalType;
import org.overture.interpreter.ast.patterns.ABooleanPatternInterpreter;
import org.overture.interpreter.ast.patterns.ACharacterPatternInterpreter;
import org.overture.interpreter.ast.patterns.AConcatenationPatternInterpreter;
import org.overture.interpreter.ast.patterns.AExpressionPatternInterpreter;
import org.overture.interpreter.ast.patterns.AIdentifierPatternInterpreter;
import org.overture.interpreter.ast.patterns.AIntegerPatternInterpreter;
import org.overture.interpreter.ast.patterns.AQuotePatternInterpreter;
import org.overture.interpreter.ast.patterns.ARealPatternInterpreter;
import org.overture.interpreter.ast.patterns.ARecordPatternInterpreter;
import org.overture.interpreter.ast.patterns.ASeqPatternInterpreter;
import org.overture.interpreter.ast.patterns.ASetPatternInterpreter;
import org.overture.interpreter.ast.patterns.AStringPatternInterpreter;
import org.overture.interpreter.ast.patterns.ATuplePatternInterpreter;
import org.overture.interpreter.ast.patterns.AUnionPatternInterpreter;
import org.overture.interpreter.ast.patterns.PPatternInterpreter;
import org.overture.interpreter.ast.types.ABooleanBasicTypeInterpreter;
import org.overture.interpreter.ast.types.ACharBasicTypeInterpreter;
import org.overture.interpreter.ast.types.AOptionalTypeInterpreter;
import org.overture.interpreter.ast.types.AQuoteTypeInterpreter;
import org.overture.interpreter.ast.types.ARealNumericBasicTypeInterpreter;
import org.overture.interpreter.ast.types.ASeqSeqTypeInterpreter;
import org.overture.interpreter.ast.types.ASetTypeInterpreter;
import org.overture.interpreter.ast.types.AUnknownTypeInterpreter;
import org.overture.interpreter.ast.types.ETypeInterpreter;
import org.overture.interpreter.ast.types.PTypeInterpreter;
import org.overture.interpreter.types.assistant.PTypeInterpreterAssistant;
import org.overture.interpreter.types.assistant.PTypeList;
import org.overture.interpreter.types.assistant.PTypeSet;
import org.overture.interpreter.types.assistant.SNumericBasicTypeInterpreterAssistant;
import org.overturetool.vdmj.lex.LexNameList;

import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.values.FieldMap;
import org.overturetool.vdmj.values.FieldValue;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.NilValue;
import org.overturetool.vdmj.values.RecordValue;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.SetValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueSet;

public class PPatternInterpreterAssistant
{
	public static NameValuePairList getNamedValues(PPatternInterpreter p,
			Value expval, Context ctxt) throws PatternMatchException
	{
		switch (p.kindPPatternInterpreter())
		{
			case BOOLEAN:
			{
				ABooleanPatternInterpreter pattern = (ABooleanPatternInterpreter) p;
				NameValuePairList result = new NameValuePairList();

				try
				{
					if (expval.boolValue(ctxt) != pattern.getValue().value)
					{
						patternFail(4106, "Boolean pattern match failed", p);
					}
				} catch (ValueException e)
				{
					patternFail(e, p);
				}

				return result;
			}

			case CHARACTER:
			{
				ACharacterPatternInterpreter pattern = (ACharacterPatternInterpreter) p;
				NameValuePairList result = new NameValuePairList();

				try
				{
					if (expval.charValue(ctxt) != pattern.getValue().unicode)
					{
						patternFail(4107, "Character pattern match failed", p);
					}
				} catch (ValueException e)
				{
					patternFail(e, p);
				}

				return result;
			}

			case CONCATENATION:
			{
				return getNamedValues((AConcatenationPatternInterpreter) p, expval, ctxt);
			}

			case EXPRESSION:
			{
				AExpressionPatternInterpreter pattern = (AExpressionPatternInterpreter) p;
				NameValuePairList result = new NameValuePairList();

				if (!expval.equals(pattern.getExp().apply(Value.evaluator, ctxt)))
				{
					patternFail(4110, "Expression pattern match failed", p);
				}

				return result; // NB no values for a match, as there's no definition
			}

			case IDENTIFIER:
			{
				AIdentifierPatternInterpreter pattern = (AIdentifierPatternInterpreter) p;
				NameValuePairList result = new NameValuePairList();
				result.add(new NameValuePair(pattern.getName(), expval));
				return result;
			}

			case IGNORE:
			{
				return new NameValuePairList();
			}

			case INTEGER:
			{
				AIntegerPatternInterpreter pattern = (AIntegerPatternInterpreter) p;
				NameValuePairList result = new NameValuePairList();

				try
				{
					if (expval.intValue(ctxt) != pattern.getValue().value)
					{
						patternFail(4111, "Integer pattern match failed", p);
					}
				} catch (ValueException e)
				{
					patternFail(e, p);
				}

				return result;
			}

			case NIL:
			{
				NameValuePairList result = new NameValuePairList();

				if (!(expval.deref() instanceof NilValue))
				{
					patternFail(4106, "Nil pattern match failed", p);
				}

				return result;
			}

			case QUOTE:
			{
				AQuotePatternInterpreter pattern = (AQuotePatternInterpreter) p;
				NameValuePairList result = new NameValuePairList();

				try
				{
					if (!expval.quoteValue(ctxt).equals(pattern.getValue().value))
					{
						patternFail(4112, "Quote pattern match failed", p);
					}
				} catch (ValueException e)
				{
					patternFail(e, p);
				}

				return result;
			}

			case REAL:
			{
				ARealPatternInterpreter pattern = (ARealPatternInterpreter) p;
				NameValuePairList result = new NameValuePairList();

				try
				{
					if (expval.realValue(ctxt) != pattern.getValue().value)
					{
						patternFail(4113, "Real pattern match failed", p);
					}
				} catch (ValueException e)
				{
					patternFail(e, p);
				}

				return result;
			}

			case RECORD:
			{
				ARecordPatternInterpreter pattern = (ARecordPatternInterpreter) p;
				return getNamedValues(pattern, expval, ctxt);
			}

			case SEQ:
			{
				return getNamedValues((ASeqPatternInterpreter) p, expval, ctxt);
			}

			case SET:
			{
				return getNamedValues((ASetPatternInterpreter) p, expval, ctxt);
			}

			case STRING:
			{
				AStringPatternInterpreter pattern = (AStringPatternInterpreter) p;
				NameValuePairList result = new NameValuePairList();

				try
				{
					if (!expval.stringValue(ctxt).equals(pattern.getValue().value))
					{
						patternFail(4122, "String pattern match failed", p);
					}
				} catch (ValueException e)
				{
					patternFail(e, p);
				}

				return result;
			}

			case TUPLE:
				return getNamedValues((ATuplePatternInterpreter) p, expval, ctxt);

			case UNION:
			{
				return getNamedValues((AUnionPatternInterpreter) p, expval, ctxt);
			}

		}
		assert false : "Error in getNamedValues in PPattern";
		return null;
	}

	public static NameValuePairList getNamedValues(
			AConcatenationPatternInterpreter p, Value expval, Context ctxt)
			throws PatternMatchException
	{
		ValueList values = null;

		try
		{
			values = expval.seqValue(ctxt);
		} catch (ValueException e)
		{
			patternFail(e, p);
		}

		int llen = getLength(p.getLeft());
		int rlen = getLength(p.getRight());
		int size = values.size();

		if ((llen == 0 && rlen > size) || (rlen == 0 && llen > size)
				|| (rlen > 0 && llen > 0 && size != llen + rlen))
		{
			patternFail(4108, "Sequence concatenation pattern does not match expression", p);
		}

		if (llen == 0)
		{
			if (rlen == 0)
			{
				// Divide size roughly between l/r
				llen = size / 2;
				rlen = size - llen;
			} else
			{
				// Take rlen from size and give to llen
				llen = size - rlen;
			}
		} else
		{
			if (rlen == 0)
			{
				// Take llen from size and give to rlen
				rlen = size - llen;
			}
		}

		assert llen + rlen == size : "Pattern match internal error";

		Iterator<Value> iter = values.iterator();
		ValueList head = new ValueList();

		for (int i = 0; i < llen; i++)
		{
			head.add(iter.next());
		}

		ValueList tail = new ValueList();

		while (iter.hasNext()) // Everything else in second
		{
			tail.add(iter.next());
		}

		NameValuePairList matches = new NameValuePairList();
		matches.addAll(getNamedValues(p.getLeft(), new SeqValue(head), ctxt));
		matches.addAll(getNamedValues(p.getRight(), new SeqValue(tail), ctxt));
		NameValuePairMap results = new NameValuePairMap();

		for (NameValuePair nvp : matches)
		{
			Value v = results.get(nvp.name);

			if (v == null)
			{
				results.put(nvp);
			} else
			// Names match, so values must also
			{
				if (!v.equals(nvp.value))
				{
					patternFail(4109, "Values do not match concatenation pattern", p);
				}
			}
		}

		return results.asList();
	}

	public static NameValuePairList getNamedValues(
			ARecordPatternInterpreter pattern, Value expval, Context ctxt)
			throws PatternMatchException
	{
		FieldMap fields = null;
		RecordValue exprec = null;

		try
		{
			exprec = expval.recordValue(ctxt);
			fields = exprec.fieldmap;
		} catch (ValueException e)
		{
			patternFail(e, pattern);
		}

		// if (!type.equals(exprec.type))
		if (!TypeComparator.compatible(pattern.getType(), exprec.type))//TODO :-( do we really need this again
		{
			patternFail(4114, "Record type does not match pattern", pattern);
		}

		if (fields.size() != pattern.getPlist().size())
		{
			patternFail(4115, "Record expression does not match pattern", pattern);
		}

		Iterator<FieldValue> iter = fields.iterator();
		NameValuePairMap results = new NameValuePairMap();

		for (PPatternInterpreter p : pattern.getPlist())
		{
			for (NameValuePair nvp : getNamedValues(p, iter.next().value, ctxt))
			{
				Value v = results.get(nvp.name);

				if (v == null)
				{
					results.put(nvp);
				} else
				// Names match, so values must also
				{
					if (!v.equals(nvp.value))
					{
						patternFail(4116, "Values do not match record pattern", pattern);
					}
				}
			}
		}

		return results.asList();
	}

	public static NameValuePairList getNamedValues(
			ASeqPatternInterpreter pattern, Value expval, Context ctxt)
			throws PatternMatchException
	{
		ValueList values = null;

		try
		{
			values = expval.seqValue(ctxt);
		} catch (ValueException e)
		{
			patternFail(e, pattern);
		}

		if (values.size() != pattern.getPlist().size())
		{
			patternFail(4117, "Wrong number of elements for sequence pattern", pattern);
		}

		ListIterator<Value> iter = values.listIterator();
		NameValuePairMap results = new NameValuePairMap();

		for (PPatternInterpreter p : pattern.getPlist())
		{
			for (NameValuePair nvp : getNamedValues(p, iter.next(), ctxt))
			{
				Value v = results.get(nvp.name);

				if (v == null)
				{
					results.put(nvp);
				} else
				// Names match, so values must also
				{
					if (!v.equals(nvp.value))
					{
						patternFail(4118, "Values do not match sequence pattern", pattern);
					}
				}
			}
		}

		return results.asList();
	}

	public static NameValuePairList getNamedValues(
			ASetPatternInterpreter pattern, Value expval, Context ctxt)
			throws PatternMatchException
	{
		ValueSet values = null;

		try
		{
			values = expval.setValue(ctxt);
		} catch (ValueException e)
		{
			patternFail(e, pattern);
		}

		if (values.size() != pattern.getPlist().size())
		{
			patternFail(4119, "Wrong number of elements for set pattern", pattern);
		}

		// Since the member patterns may indicate specific set members, we
		// have to permute through the various set orderings to see
		// whether there are any which match both sides. If the members
		// are not constrained however, the initial ordering will be
		// fine.

		List<ValueSet> allSets;

		if (isConstrained(pattern))
		{
			allSets = values.permutedSets();
		} else
		{
			allSets = new Vector<ValueSet>();
			allSets.add(values);
		}

		for (ValueSet setPerm : allSets)
		{
			Iterator<Value> iter = setPerm.iterator();

			try
			{
				NameValuePairMap results = new NameValuePairMap();

				for (PPatternInterpreter p : pattern.getPlist())
				{
					for (NameValuePair nvp : getNamedValues(p, iter.next(), ctxt))
					{
						Value v = results.get(nvp.name);

						if (v == null)
						{
							results.put(nvp);
						} else
						// Names match, so values must also
						{
							if (!v.equals(nvp.value))
							{
								patternFail(4120, "Values do not match set pattern", pattern);
							}
						}
					}
				}

				return results.asList();
			} catch (PatternMatchException pme)
			{
				// Try next perm then...
			}
		}

		patternFail(4121, "Cannot match set pattern", pattern);
		return null;
	}

	public static NameValuePairList getNamedValues(
			ATuplePatternInterpreter pattern, Value expval, Context ctxt)
			throws PatternMatchException
	{
		ValueList values = null;

		try
		{
			values = expval.tupleValue(ctxt);
		} catch (ValueException e)
		{
			patternFail(e, pattern);
		}

		if (values.size() != pattern.getPlist().size())
		{
			patternFail(4123, "Tuple expression does not match pattern", pattern);
		}

		ListIterator<Value> iter = values.listIterator();
		NameValuePairMap results = new NameValuePairMap();

		for (PPatternInterpreter p : pattern.getPlist())
		{
			for (NameValuePair nvp : getNamedValues(p, iter.next(), ctxt))
			{
				Value v = results.get(nvp.name);

				if (v == null)
				{
					results.put(nvp);
				} else
				// Names match, so values must also
				{
					if (!v.equals(nvp.value))
					{
						patternFail(4124, "Values do not match tuple pattern", pattern);
					}
				}
			}
		}

		return results.asList();
	}

	public static NameValuePairList getNamedValues(
			AUnionPatternInterpreter pattern, Value expval, Context ctxt)
			throws PatternMatchException
	{
		ValueSet values = null;

		try
		{
			values = expval.setValue(ctxt);
		} catch (ValueException e)
		{
			patternFail(e, pattern);
		}

		int llen = getLength(pattern.getLeft());
		int rlen = getLength(pattern.getRight());
		int size = values.size();

		if ((llen == 0 && rlen > size) || (rlen == 0 && llen > size)
				|| (rlen > 0 && llen > 0 && size != llen + rlen))
		{
			patternFail(4125, "Set union pattern does not match expression", pattern);
		}

		if (llen == 0)
		{
			if (rlen == 0)
			{
				// Divide size roughly between l/r
				llen = size / 2;
				rlen = size - llen;
			} else
			{
				// Take rlen from size and give to llen
				llen = size - rlen;
			}
		} else
		{
			if (rlen == 0)
			{
				// Take llen from size and give to rlen
				rlen = size - llen;
			}
		}

		assert llen + rlen == size : "Pattern match internal error";

		// Since the left and right may have specific set members, we
		// have to permute through the various set orderings to see
		// whether there are any which match both sides. If the patterns
		// are not constrained however, the initial ordering will be
		// fine.

		List<ValueSet> allSets;

		if (isConstrained(pattern))
		{
			allSets = values.permutedSets();
		} else
		{
			allSets = new Vector<ValueSet>();
			allSets.add(values);
		}

		for (ValueSet setPerm : allSets)
		{
			Iterator<Value> iter = setPerm.iterator();
			ValueSet first = new ValueSet();

			for (int i = 0; i < llen; i++)
			{
				first.add(iter.next());
			}

			ValueSet second = new ValueSet();

			while (iter.hasNext()) // Everything else in second
			{
				second.add(iter.next());
			}

			try
			{
				NameValuePairList matches = new NameValuePairList();
				matches.addAll(getNamedValues(pattern.getLeft(), new SetValue(first), ctxt));
				matches.addAll(getNamedValues(pattern.getRight(), new SetValue(second), ctxt));
				NameValuePairMap results = new NameValuePairMap();

				for (NameValuePair nvp : matches)
				{
					Value v = results.get(nvp.name);

					if (v == null)
					{
						results.put(nvp);
					} else
					// Names match, so values must also
					{
						if (!v.equals(nvp.value))
						{
							patternFail(4126, "Values do not match union pattern", pattern);
						}
					}
				}

				return results.asList();
			} catch (PatternMatchException pme)
			{
				// Try next perm then...
			}
		}

		patternFail(4127, "Cannot match set pattern", pattern);
		return null;
	}

	private static int getLength(PPatternInterpreter p)
	{
		switch (p.kindPPatternInterpreter())
		{
			case CONCATENATION:
			{
				AConcatenationPatternInterpreter pattern = (AConcatenationPatternInterpreter) p;
				return getLength(pattern.getLeft())
						+ getLength(pattern.getRight());
			}
			case IDENTIFIER:
			case IGNORE:
				return 0; // Special value meaning "any length"
			case RECORD:
			{
				ARecordPatternInterpreter pattern = (ARecordPatternInterpreter) p;
				return pattern.getPlist().size();
			}
			case SEQ:
			{
				ASeqPatternInterpreter pattern = (ASeqPatternInterpreter) p;
				return pattern.getPlist().size();
			}
			case SET:
			{
				ASetPatternInterpreter pattern = (ASetPatternInterpreter) p;
				return pattern.getPlist().size();
			}
			case TUPLE:
			{
				ATuplePatternInterpreter pattern = (ATuplePatternInterpreter) p;
				return pattern.getPlist().size();
			}
			case UNION:
			{
				AUnionPatternInterpreter pattern = (AUnionPatternInterpreter) p;
				return getLength(pattern.getLeft())
						+ getLength(pattern.getRight());
			}
		}
		return 1; // Most only identify one member
	}

	/**
	 * @return True if the pattern has constraints, such that matching values should be permuted, where necessary, to
	 *         find a match.
	 */
	private static boolean isConstrained(PPatternInterpreter p)
	{
		switch (p.kindPPatternInterpreter())
		{
			case CONCATENATION:
			{
				AConcatenationPatternInterpreter pattern = (AConcatenationPatternInterpreter) p;
				return isConstrained(pattern.getLeft())
						|| isConstrained(pattern.getRight());
			}
			case IDENTIFIER:
				return false; // The variable can be anything
			case IGNORE:
				return false;
			case RECORD:
			{
				ARecordPatternInterpreter pattern = (ARecordPatternInterpreter) p;
				for (PPatternInterpreter p1 : pattern.getPlist())
				{
					if (isConstrained(p1))
						return true;
				}

				return false;
			}
			case SEQ:
			{
				ASeqPatternInterpreter pattern = (ASeqPatternInterpreter) p;
				for (PPatternInterpreter p1 : pattern.getPlist())
				{
					if (isConstrained(p1))
						return true;
				}

				return false;
			}
			case SET:
			{
				ASetPatternInterpreter pattern = (ASetPatternInterpreter) p;
				if (PPatternListAssistant.getPossibleType(pattern.getPlist(), pattern.getLocation()).kindPTypeInterpreter().equals(ETypeInterpreter.UNION))
				{
					return true; // Set types are various, so we must permute
				}

				for (PPatternInterpreter p1 : pattern.getPlist())
				{
					if (isConstrained(p1))
						return true;
				}

				return false;
			}
			case TUPLE:
			{
				ATuplePatternInterpreter pattern = (ATuplePatternInterpreter) p;
				for (PPatternInterpreter p1 : pattern.getPlist())
				{
					if (isConstrained(p1))
						return true;
				}

				return false;
			}
			case UNION:
			{
				AUnionPatternInterpreter pattern = (AUnionPatternInterpreter) p;
				return isConstrained(pattern.getLeft())
						|| isConstrained(pattern.getRight());
			}
		}
		return true;
	}

	/**
	 * Throw a PatternMatchException with the given message.
	 * 
	 * @throws PatternMatchException
	 */

	public static void patternFail(int number, String msg, PPatternInterpreter p)
			throws PatternMatchException
	{
		throw new PatternMatchException(number, msg, p.getLocation());
	}

	/**
	 * Throw a PatternMatchException with a message from the ValueException.
	 * 
	 * @throws PatternMatchException
	 */

	public static Value patternFail(ValueException ve, PPatternInterpreter p)
			throws PatternMatchException
	{
		throw new PatternMatchException(ve.number, ve.getMessage(), p.getLocation());
	}

	/** Get the type(s) that could match this pattern. */
	public static PTypeInterpreter getPossibleType(PPatternInterpreter pattern)
	{
		switch (pattern.kindPPatternInterpreter())
		{
			case BOOLEAN:
				return new ABooleanBasicTypeInterpreter(pattern.getLocation(), false);
			case CHARACTER:
				return new ACharBasicTypeInterpreter(pattern.getLocation(), false);
			case CONCATENATION:
				return new ASeqSeqTypeInterpreter(pattern.getLocation(), false, new AUnknownTypeInterpreter(pattern.getLocation(), false), false);
			case EXPRESSION:
				return new AUnknownTypeInterpreter(pattern.getLocation(), false);
			case IDENTIFIER:
				return new AUnknownTypeInterpreter(pattern.getLocation(), false);
			case IGNORE:
				return new AUnknownTypeInterpreter(pattern.getLocation(), false);
			case INTEGER:
				return SNumericBasicTypeInterpreterAssistant.typeOf(((AIntegerPatternInterpreter) pattern).getValue().value, pattern.getLocation());
			case NIL:
				return new AOptionalTypeInterpreter(pattern.getLocation(), false, new AUnknownTypeInterpreter(pattern.getLocation(), false));
			case QUOTE:
				return new AQuoteTypeInterpreter(pattern.getLocation(), false, ((AQuotePatternInterpreter) pattern).getValue());
			case REAL:
				return new ARealNumericBasicTypeInterpreter(pattern.getLocation(), false);
			case RECORD:
				return ((ARecordPatternInterpreter) pattern).getType();
			case SEQ:
				return new ASeqSeqTypeInterpreter(pattern.getLocation(), false, new AUnknownTypeInterpreter(pattern.getLocation(), false), false);
			case SET:
				return new ASetTypeInterpreter(pattern.getLocation(), false, new AUnknownTypeInterpreter(pattern.getLocation(), false), false, false);
			case STRING:
				return new ASeqSeqTypeInterpreter(pattern.getLocation(), false, new ACharBasicTypeInterpreter(pattern.getLocation(), false), false);
			case TUPLE:
				ATuplePatternInterpreter tupplePattern = (ATuplePatternInterpreter) pattern;
				PTypeList list = new PTypeList();

				for (PPatternInterpreter p : tupplePattern.getPlist())
				{
					list.add(getPossibleType(p));
				}

				return list.getType(tupplePattern.getLocation());
			case UNION:
				AUnionPatternInterpreter unionPattern = (AUnionPatternInterpreter) pattern;
				PTypeSet set = new PTypeSet();

				set.add(getPossibleType(unionPattern.getLeft()));
				set.add(getPossibleType(unionPattern.getRight()));

				PTypeInterpreter s = set.getType(unionPattern.getLocation());

				return PTypeInterpreterAssistant.isUnknown(s) ? new ASetTypeInterpreter(unionPattern.getLocation(), false, null, new AUnknownTypeInterpreter(unionPattern.getLocation(), false), false, false)
						: s;
		}
		return null;
	}

}
