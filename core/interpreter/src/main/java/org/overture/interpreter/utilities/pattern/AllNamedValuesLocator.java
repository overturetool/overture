package org.overture.interpreter.utilities.pattern;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.ast.patterns.ANamePatternPair;
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.patterns.AObjectPattern;
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
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.traces.Permutor;
import org.overture.interpreter.values.FieldMap;
import org.overture.interpreter.values.FieldValue;
import org.overture.interpreter.values.MapValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.values.NilValue;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.RecordValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueMap;
import org.overture.interpreter.values.ValueSet;

public class AllNamedValuesLocator
		extends
		QuestionAnswerAdaptor<AllNamedValuesLocator.Newquestion, List<NameValuePairList>>
{
	public static class Newquestion
	{
		Value expval;
		Context ctxt;

		public Newquestion(Value expval, Context ctxt)
		{
			this.expval = expval;
			this.ctxt = ctxt;
		}
	}

	protected final IInterpreterAssistantFactory af;
	protected final String fromModule;

	public AllNamedValuesLocator(IInterpreterAssistantFactory af, String fromModule)
	{
		this.af = af;
		this.fromModule = fromModule;
	}

	@Override
	public List<NameValuePairList> caseABooleanPattern(ABooleanPattern pattern,
			Newquestion question) throws AnalysisException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();

		try
		{
			if (question.expval.boolValue(question.ctxt) != pattern.getValue().getValue())
			{
				VdmRuntimeError.patternFail(4106, "Boolean pattern match failed", pattern.getLocation());
			}
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		result.add(new NameValuePairList());
		return result;
	}

	@Override
	public List<NameValuePairList> caseACharacterPattern(
			ACharacterPattern pattern, Newquestion question)
			throws AnalysisException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();

		try
		{
			if (question.expval.charValue(question.ctxt) != pattern.getValue().getValue())
			{
				VdmRuntimeError.patternFail(4107, "Character pattern match failed", pattern.getLocation());
			}
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		result.add(new NameValuePairList());
		return result;
	}

	@Override
	public List<NameValuePairList> caseAConcatenationPattern(
			AConcatenationPattern pattern, Newquestion question)
			throws AnalysisException
	{
		ValueList values = null;

		try
		{
			values = question.expval.seqValue(question.ctxt);
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		int llen = af.createPPatternAssistant(fromModule).getLength(pattern.getLeft());
		int rlen = af.createPPatternAssistant(fromModule).getLength(pattern.getRight());
		int size = values.size();

		if (llen == PPatternAssistantInterpreter.ANY && rlen > size
				|| rlen == PPatternAssistantInterpreter.ANY && llen > size
				|| rlen != PPatternAssistantInterpreter.ANY
				&& llen != PPatternAssistantInterpreter.ANY
				&& size != llen + rlen)
		{
			VdmRuntimeError.patternFail(4108, "Sequence concatenation pattern does not match expression", pattern.getLocation());
		}

		// If the left and right sizes are ANY (ie. flexible) then we have to
		// generate a set of splits of the values, and offer these to sub-matches
		// to see whether they fit. Otherwise, there is just one split at this level.

		List<Integer> leftSizes = new Vector<Integer>();

		if (llen == PPatternAssistantInterpreter.ANY)
		{
			if (rlen == PPatternAssistantInterpreter.ANY)
			{
//				if (size == 0)
//				{
//					// Can't match a ^ b with []
//				} else
				if (size % 2 == 1)
				{
					// Odd => add the middle, then those either side
					int half = size / 2 + 1;
					if (half > 0)
					{
						leftSizes.add(half);
					}

					for (int delta = 1; half - delta > 0; delta++)
					{
						leftSizes.add(half + delta);
						leftSizes.add(half - delta);
					}

					leftSizes.add(0);
				} else
				{
					// Even => add those either side of the middle
					int half = size / 2;
					if (half > 0)
					{
						leftSizes.add(half);
					}

					for (int delta = 1; half - delta > 0; delta++)
					{
						leftSizes.add(half + delta);
						leftSizes.add(half - delta);
					}

					leftSizes.add(size);
					leftSizes.add(0);
				}
			} else
			{
				leftSizes.add(size - rlen);
			}
		} else
		{
			leftSizes.add(llen);
		}

		// Now loop through the various splits and attempt to match the l/r
		// sub-patterns to the split sequence value.

		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();

		for (Integer lsize : leftSizes)
		{
			Iterator<Value> iter = values.iterator();
			ValueList head = new ValueList();

			for (int i = 0; i < lsize; i++)
			{
				head.add(iter.next());
			}

			ValueList tail = new ValueList();

			while (iter.hasNext()) // Everything else in second
			{
				tail.add(iter.next());
			}

			List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
			int psize = 2;
			int[] counts = new int[psize];

			try
			{
				List<NameValuePairList> lnvps = af.createPPatternAssistant(fromModule).getAllNamedValues(pattern.getLeft(), new SeqValue(head), question.ctxt);
				nvplists.add(lnvps);
				counts[0] = lnvps.size();

				List<NameValuePairList> rnvps = af.createPPatternAssistant(fromModule).getAllNamedValues(pattern.getRight(), new SeqValue(tail), question.ctxt);
				nvplists.add(rnvps);
				counts[1] = rnvps.size();
			} catch (PatternMatchException e)
			{
				continue;
			}

			Permutor permutor = new Permutor(counts);

			while (permutor.hasNext())
			{
				try
				{
					NameValuePairMap results = new NameValuePairMap();
					int[] selection = permutor.next();

					for (int p = 0; p < psize; p++)
					{
						for (NameValuePair nvp : nvplists.get(p).get(selection[p]))
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
									VdmRuntimeError.patternFail(4109, "Values do not match concatenation pattern", pattern.getLocation());
								}
							}
						}
					}

					finalResults.add(results.asList()); // Consistent set of nvps
				} catch (PatternMatchException pme)
				{
					// try next perm
				}
			}
		}

		if (finalResults.isEmpty())
		{
			VdmRuntimeError.patternFail(4109, "Values do not match concatenation pattern", pattern.getLocation());
		}

		return finalResults;
	}

	@Override
	public List<NameValuePairList> caseAExpressionPattern(
			AExpressionPattern pattern, Newquestion question)
			throws AnalysisException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();

		try
		{
			if (!question.expval.equals(pattern.getExp().apply(VdmRuntime.getExpressionEvaluator(), question.ctxt)))
			{
				VdmRuntimeError.patternFail(4110, "Expression pattern match failed", pattern.getLocation());
			}
		} catch (AnalysisException e)
		{
			if (e instanceof PatternMatchException)
			{
				throw (PatternMatchException) e;
			}
			e.printStackTrace();
		}

		result.add(new NameValuePairList());
		return result; // NB no values for a match, as there's no definition
	}

	@Override
	public List<NameValuePairList> caseAIdentifierPattern(
			AIdentifierPattern pattern, Newquestion question)
			throws AnalysisException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();
		NameValuePairList list = new NameValuePairList();
		list.add(new NameValuePair(pattern.getName(), question.expval));
		result.add(list);
		return result;
	}

	@Override
	public List<NameValuePairList> caseAIgnorePattern(AIgnorePattern pattern,
			Newquestion question) throws AnalysisException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();
		result.add(new NameValuePairList());
		return result;
	}

	@Override
	public List<NameValuePairList> caseAIntegerPattern(AIntegerPattern pattern,
			Newquestion question) throws AnalysisException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();

		try
		{
			if (question.expval.intValue(question.ctxt) != pattern.getValue().getValue())
			{
				VdmRuntimeError.patternFail(4111, "Integer pattern match failed", pattern.getLocation());
			}
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		result.add(new NameValuePairList());
		return result;
	}

	@Override
	public List<NameValuePairList> caseAMapPattern(AMapPattern pattern,
			Newquestion question) throws AnalysisException
	{
		ValueMap values = null;

		try
		{
			values = question.expval.mapValue(question.ctxt);
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		if (values.size() != pattern.getMaplets().size())
		{
			VdmRuntimeError.patternFail(4152, "Wrong number of elements for map pattern", pattern.getLocation());
		}

		// Since the member patterns may indicate specific map members, we
		// have to permute through the various map orderings to see
		// whether there are any which match both sides. If the members
		// are not constrained however, the initial ordering will be
		// fine.

		List<ValueMap> allMaps;

		if (pattern.apply(af.getConstrainedPatternChecker()))
		{
			allMaps = values.permutedMaps();
		} else
		{
			allMaps = new Vector<ValueMap>();
			allMaps.add(values);
		}

		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();
		int psize = pattern.getMaplets().size();

		if (pattern.getMaplets().isEmpty())
		{
			finalResults.add(new NameValuePairList());
			return finalResults;
		}

		for (ValueMap mapPerm : allMaps)
		{
			Iterator<Entry<Value, Value>> iter = mapPerm.entrySet().iterator();

			List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
			int[] counts = new int[psize];
			int i = 0;

			try
			{
				for (AMapletPatternMaplet p : pattern.getMaplets())
				{
					List<NameValuePairList> pnvps = af.createAMapPatternMapletAssistant(fromModule).getAllNamedValues(p, iter.next(), question.ctxt);
					nvplists.add(pnvps);
					counts[i++] = pnvps.size();
				}
			} catch (Exception e)
			{
				continue;
			}

			Permutor permutor = new Permutor(counts);

			while (permutor.hasNext())
			{
				try
				{
					NameValuePairMap results = new NameValuePairMap();
					int[] selection = permutor.next();

					for (int p = 0; p < psize; p++)
					{
						for (NameValuePair nvp : nvplists.get(p).get(selection[p]))
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
									VdmRuntimeError.patternFail(4153, "Values do not match map pattern", pattern.getLocation());
								}
							}
						}
					}

					finalResults.add(results.asList());
				} catch (PatternMatchException pme)
				{
					// Try next perm then...
				}
			}
		}

		if (finalResults.isEmpty())
		{
			VdmRuntimeError.patternFail(4154, "Cannot match map pattern", pattern.getLocation());
		}

		return finalResults;
	}

	@Override
	public List<NameValuePairList> caseAMapUnionPattern(
			AMapUnionPattern pattern, Newquestion question)
			throws AnalysisException
	{
		ValueMap values = null;

		try
		{
			values = question.expval.mapValue(question.ctxt);
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		int llen = af.createPPatternAssistant(fromModule).getLength(pattern.getLeft());
		int rlen = af.createPPatternAssistant(fromModule).getLength(pattern.getRight());
		int size = values.size();

		if (llen == PPatternAssistantInterpreter.ANY && rlen > size
				|| rlen == PPatternAssistantInterpreter.ANY && llen > size
				|| rlen != PPatternAssistantInterpreter.ANY
				&& llen != PPatternAssistantInterpreter.ANY
				&& size != llen + rlen)
		{
			VdmRuntimeError.patternFail(4155, "Map union pattern does not match expression", pattern.getLocation());
		}

		// If the left and right sizes are zero (ie. flexible) then we have to
		// generate a set of splits of the values, and offer these to sub-matches
		// to see whether they fit. Otherwise, there is just one split at this level.

		List<Integer> leftSizes = new Vector<Integer>();

		if (llen == PPatternAssistantInterpreter.ANY)
		{
			if (rlen == PPatternAssistantInterpreter.ANY)
			{
				if (size == 0)
				{
					// Can't match a munion b with {|->}
				} else if (size % 2 == 1)
				{
					// Odd => add the middle, then those either side
					int half = size / 2 + 1;
					if (half > 0)
					{
						leftSizes.add(half);
					}

					for (int delta = 1; half - delta > 0; delta++)
					{
						leftSizes.add(half + delta);
						leftSizes.add(half - delta);
					}

					leftSizes.add(0);
				} else
				{
					// Even => add those either side of the middle
					int half = size / 2;
					if (half > 0)
					{
						leftSizes.add(half);
					}

					for (int delta = 1; half - delta > 0; delta++)
					{
						leftSizes.add(half + delta);
						leftSizes.add(half - delta);
					}

					leftSizes.add(size);
					leftSizes.add(0);
				}
			} else
			{
				leftSizes.add(size - rlen);
			}
		} else
		{
			leftSizes.add(llen);
		}

		// Since the left and right may have specific element members, we
		// have to permute through the various map orderings to see
		// whether there are any which match both sides. If the patterns
		// are not constrained however, the initial ordering will be
		// fine.

		List<ValueMap> allMaps;

		if (pattern.apply(af.getConstrainedPatternChecker()))
		{
			allMaps = values.permutedMaps();
		} else
		{
			allMaps = new Vector<ValueMap>();
			allMaps.add(values);
		}

		// Now loop through the various splits and attempt to match the l/r
		// sub-patterns to the split map value.

		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();

		for (Integer lsize : leftSizes)
		{
			for (ValueMap setPerm : allMaps)
			{
				Iterator<Entry<Value, Value>> iter = setPerm.entrySet().iterator();
				ValueMap first = new ValueMap();

				for (int i = 0; i < lsize; i++)
				{
					Entry<Value, Value> e = iter.next();
					first.put(e.getKey(), e.getValue());
				}

				ValueMap second = new ValueMap();

				while (iter.hasNext()) // Everything else in second
				{
					Entry<Value, Value> e = iter.next();
					second.put(e.getKey(), e.getValue());
				}

				List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
				int psize = 2;
				int[] counts = new int[psize];

				try
				{
					List<NameValuePairList> lnvps = af.createPPatternAssistant(fromModule).getAllNamedValues(pattern.getLeft(), new MapValue(first), question.ctxt);
					nvplists.add(lnvps);
					counts[0] = lnvps.size();

					List<NameValuePairList> rnvps = af.createPPatternAssistant(fromModule).getAllNamedValues(pattern.getRight(), new MapValue(second), question.ctxt);
					nvplists.add(rnvps);
					counts[1] = rnvps.size();
				} catch (Exception e)
				{
					continue;
				}

				Permutor permutor = new Permutor(counts);

				while (permutor.hasNext())
				{
					try
					{
						NameValuePairMap results = new NameValuePairMap();
						int[] selection = permutor.next();

						for (int p = 0; p < psize; p++)
						{
							for (NameValuePair nvp : nvplists.get(p).get(selection[p]))
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
										VdmRuntimeError.patternFail(4126, "Values do not match union pattern", pattern.getLocation());
									}
								}
							}
						}

						finalResults.add(results.asList());
					} catch (PatternMatchException pme)
					{
						// Try next perm then...
					}
				}
			}
		}

		if (finalResults.isEmpty())
		{
			VdmRuntimeError.patternFail(4156, "Cannot match map pattern", pattern.getLocation());
		}

		return finalResults;
	}

	@Override
	public List<NameValuePairList> caseANilPattern(ANilPattern pattern,
			Newquestion question) throws AnalysisException
	{
		// return ANilPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
		List<NameValuePairList> result = new Vector<NameValuePairList>();

		if (!(question.expval.deref() instanceof NilValue))
		{
			VdmRuntimeError.patternFail(4106, "Nil pattern match failed", pattern.getLocation());
		}

		result.add(new NameValuePairList());
		return result;
	}

	@Override
	public List<NameValuePairList> caseAQuotePattern(AQuotePattern pattern,
			Newquestion question) throws AnalysisException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();

		try
		{
			if (!question.expval.quoteValue(question.ctxt).equals(pattern.getValue().getValue()))
			{
				VdmRuntimeError.patternFail(4112, "Quote pattern match failed", pattern.getLocation());
			}
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		result.add(new NameValuePairList());
		return result;
	}

	@Override
	public List<NameValuePairList> caseARealPattern(ARealPattern pattern,
			Newquestion question) throws AnalysisException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();

		try
		{
			if (question.expval.realValue(question.ctxt) != pattern.getValue().getValue())
			{
				VdmRuntimeError.patternFail(4113, "Real pattern match failed", pattern.getLocation());
			}
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		result.add(new NameValuePairList());
		return result;
	}

	@Override
	public List<NameValuePairList> caseARecordPattern(ARecordPattern pattern,
			Newquestion question) throws AnalysisException
	{
		FieldMap fields = null;
		RecordValue exprec = null;

		try
		{
			exprec = question.expval.recordValue(question.ctxt);
			fields = exprec.fieldmap;
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		// if (!type.equals(exprec.type))
		if (!question.ctxt.assistantFactory.getTypeComparator().compatible(pattern.getType(), exprec.type))
		{
			VdmRuntimeError.patternFail(4114, "Record type does not match pattern", pattern.getLocation());
		}

		if (fields.size() != pattern.getPlist().size())
		{
			VdmRuntimeError.patternFail(4115, "Record expression does not match pattern", pattern.getLocation());
		}

		Iterator<FieldValue> iter = fields.iterator();
		List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
		int psize = pattern.getPlist().size();
		int[] counts = new int[psize];
		int i = 0;

		for (PPattern p : pattern.getPlist())
		{
			List<NameValuePairList> pnvps = af.createPPatternAssistant(fromModule).getAllNamedValues(p, iter.next().value, question.ctxt);
			nvplists.add(pnvps);
			counts[i++] = pnvps.size();
		}

		Permutor permutor = new Permutor(counts);
		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();

		if (pattern.getPlist().isEmpty())
		{
			finalResults.add(new NameValuePairList());
			return finalResults;
		}

		while (permutor.hasNext())
		{
			try
			{
				NameValuePairMap results = new NameValuePairMap();
				int[] selection = permutor.next();

				for (int p = 0; p < psize; p++)
				{
					for (NameValuePair nvp : nvplists.get(p).get(selection[p]))
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
								VdmRuntimeError.patternFail(4116, "Values do not match record pattern", pattern.getLocation());
							}
						}
					}
				}

				finalResults.add(results.asList()); // Consistent set of nvps
			} catch (PatternMatchException pme)
			{
				// try next perm
			}
		}

		if (finalResults.isEmpty())
		{
			VdmRuntimeError.patternFail(4116, "Values do not match record pattern", pattern.getLocation());
		}

		return finalResults;
	}

	@Override
	public List<NameValuePairList> caseASeqPattern(ASeqPattern pattern,
			Newquestion question) throws AnalysisException
	{
		ValueList values = null;

		try
		{
			values = question.expval.seqValue(question.ctxt);
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		if (values.size() != pattern.getPlist().size())
		{
			VdmRuntimeError.patternFail(4117, "Wrong number of elements for sequence pattern", pattern.getLocation());
		}

		ListIterator<Value> iter = values.listIterator();
		List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
		int psize = pattern.getPlist().size();
		int[] counts = new int[psize];
		int i = 0;

		for (PPattern p : pattern.getPlist())
		{
			List<NameValuePairList> pnvps = af.createPPatternAssistant(fromModule).getAllNamedValues(p, iter.next(), question.ctxt);
			nvplists.add(pnvps);
			counts[i++] = pnvps.size();
		}

		Permutor permutor = new Permutor(counts);
		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();

		if (pattern.getPlist().isEmpty())
		{
			finalResults.add(new NameValuePairList());
			return finalResults;
		}

		while (permutor.hasNext())
		{
			try
			{
				NameValuePairMap results = new NameValuePairMap();
				int[] selection = permutor.next();

				for (int p = 0; p < psize; p++)
				{
					for (NameValuePair nvp : nvplists.get(p).get(selection[p]))
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
								VdmRuntimeError.patternFail(4118, "Values do not match sequence pattern", pattern.getLocation());
							}
						}
					}
				}

				finalResults.add(results.asList()); // Consistent set of nvps
			} catch (PatternMatchException pme)
			{
				// try next perm
			}
		}

		if (finalResults.isEmpty())
		{
			VdmRuntimeError.patternFail(4118, "Values do not match sequence pattern", pattern.getLocation());
		}

		return finalResults;
	}

	@Override
	public List<NameValuePairList> caseASetPattern(ASetPattern pattern,
			Newquestion question) throws AnalysisException
	{
		// return ASetPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
		ValueSet values = null;

		try
		{
			values = question.expval.setValue(question.ctxt);
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		if (values.size() != pattern.getPlist().size())
		{
			VdmRuntimeError.patternFail(4119, "Wrong number of elements for set pattern", pattern.getLocation());
		}

		// Since the member patterns may indicate specific set members, we
		// have to permute through the various set orderings to see
		// whether there are any which match both sides. If the members
		// are not constrained however, the initial ordering will be
		// fine.

		List<ValueSet> allSets;

		if (pattern.apply(af.getConstrainedPatternChecker()))
		{
			allSets = values.permutedSets();
		} else
		{
			allSets = new Vector<ValueSet>();
			allSets.add(values);
		}

		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();
		int psize = pattern.getPlist().size();

		if (pattern.getPlist().isEmpty())
		{
			finalResults.add(new NameValuePairList());
			return finalResults;
		}

		for (ValueSet setPerm : allSets)
		{
			Iterator<Value> iter = setPerm.iterator();

			List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
			int[] counts = new int[psize];
			int i = 0;

			try
			{
				for (PPattern p : pattern.getPlist())
				{
					List<NameValuePairList> pnvps = af.createPPatternAssistant(fromModule).getAllNamedValues(p, iter.next(), question.ctxt);
					nvplists.add(pnvps);
					counts[i++] = pnvps.size();
				}
			} catch (Exception e)
			{
				continue;
			}

			Permutor permutor = new Permutor(counts);

			while (permutor.hasNext())
			{
				try
				{
					NameValuePairMap results = new NameValuePairMap();
					int[] selection = permutor.next();

					for (int p = 0; p < psize; p++)
					{
						for (NameValuePair nvp : nvplists.get(p).get(selection[p]))
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
									VdmRuntimeError.patternFail(4120, "Values do not match set pattern", pattern.getLocation());
								}
							}
						}
					}

					finalResults.add(results.asList());
				} catch (PatternMatchException pme)
				{
					// Try next perm then...
				}
			}
		}

		if (finalResults.isEmpty())
		{
			VdmRuntimeError.patternFail(4121, "Cannot match set pattern", pattern.getLocation());
		}

		return finalResults;
	}

	@Override
	public List<NameValuePairList> caseAStringPattern(AStringPattern pattern,
			Newquestion question) throws AnalysisException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();

		try
		{
			if (!question.expval.stringValue(question.ctxt).equals(pattern.getValue().getValue()))
			{
				VdmRuntimeError.patternFail(4122, "String pattern match failed", pattern.getLocation());
			}
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		result.add(new NameValuePairList());
		return result;
	}

	@Override
	public List<NameValuePairList> caseATuplePattern(ATuplePattern pattern,
			Newquestion question) throws AnalysisException
	{
		ValueList values = null;

		try
		{
			values = question.expval.tupleValue(question.ctxt);
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		if (values.size() != pattern.getPlist().size())
		{
			VdmRuntimeError.patternFail(4123, "Tuple expression does not match pattern", pattern.getLocation());
		}

		ListIterator<Value> iter = values.listIterator();
		List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
		int psize = pattern.getPlist().size();
		int[] counts = new int[psize];
		int i = 0;

		for (PPattern p : pattern.getPlist())
		{
			List<NameValuePairList> pnvps = af.createPPatternAssistant(fromModule).getAllNamedValues(p, iter.next(), question.ctxt);

			nvplists.add(pnvps);
			counts[i++] = pnvps.size();
		}

		Permutor permutor = new Permutor(counts);
		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();

		while (permutor.hasNext())
		{
			try
			{
				NameValuePairMap results = new NameValuePairMap();
				int[] selection = permutor.next();

				for (int p = 0; p < psize; p++)
				{
					for (NameValuePair nvp : nvplists.get(p).get(selection[p]))
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
								VdmRuntimeError.patternFail(4124, "Values do not match tuple pattern", pattern.getLocation());
							}
						}
					}
				}

				finalResults.add(results.asList()); // Consistent set of nvps
			} catch (PatternMatchException pme)
			{
				// try next perm
			}
		}

		if (finalResults.isEmpty())
		{
			VdmRuntimeError.patternFail(4124, "Values do not match tuple pattern", pattern.getLocation());
		}

		return finalResults;
	}

	@Override
	public List<NameValuePairList> caseAUnionPattern(AUnionPattern pattern,
			Newquestion question) throws AnalysisException
	{
		ValueSet values = null;

		try
		{
			values = question.expval.setValue(question.ctxt);
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		int llen = af.createPPatternAssistant(fromModule).getLength(pattern.getLeft());
		int rlen = af.createPPatternAssistant(fromModule).getLength(pattern.getRight());
		int size = values.size();

		if (llen == PPatternAssistantInterpreter.ANY && rlen > size
				|| rlen == PPatternAssistantInterpreter.ANY && llen > size
				|| rlen != PPatternAssistantInterpreter.ANY
				&& llen != PPatternAssistantInterpreter.ANY
				&& size != llen + rlen)
		{
			VdmRuntimeError.patternFail(4125, "Set union pattern does not match expression", pattern.getLocation());
		}

		// If the left and right sizes are zero (ie. flexible) then we have to
		// generate a set of splits of the values, and offer these to sub-matches
		// to see whether they fit. Otherwise, there is just one split at this level.

		List<Integer> leftSizes = new Vector<Integer>();

		if (llen == PPatternAssistantInterpreter.ANY)
		{
			if (rlen == PPatternAssistantInterpreter.ANY)
			{
//				if (size == 0)
//				{
//					// Can't match a union b with {}
//				} else
				if (size % 2 == 1)
				{
					// Odd => add the middle, then those either side
					int half = size / 2 + 1;
					if (half > 0)
					{
						leftSizes.add(half);
					}

					for (int delta = 1; half - delta > 0; delta++)
					{
						leftSizes.add(half + delta);
						leftSizes.add(half - delta);
					}

					leftSizes.add(0);
				} else
				{
					// Even => add those either side of the middle
					int half = size / 2;
					if (half > 0)
					{
						leftSizes.add(half);
					}

					for (int delta = 1; half - delta > 0; delta++)
					{
						leftSizes.add(half + delta);
						leftSizes.add(half - delta);
					}

					leftSizes.add(size);
					leftSizes.add(0);
				}
			} else
			{
				leftSizes.add(size - rlen);
			}
		} else
		{
			leftSizes.add(llen);
		}

		// Since the left and right may have specific set members, we
		// have to permute through the various set orderings to see
		// whether there are any which match both sides. If the patterns
		// are not constrained however, the initial ordering will be
		// fine.

		List<ValueSet> allSets;

		if (pattern.apply(af.getConstrainedPatternChecker()))
		{
			allSets = values.permutedSets();
		} else
		{
			allSets = new Vector<ValueSet>();
			allSets.add(values);
		}

		// Now loop through the various splits and attempt to match the l/r
		// sub-patterns to the split set value.

		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();

		for (Integer lsize : leftSizes)
		{
			for (ValueSet setPerm : allSets)
			{
				Iterator<Value> iter = setPerm.iterator();
				ValueSet first = new ValueSet();

				for (int i = 0; i < lsize; i++)
				{
					first.add(iter.next());
				}

				ValueSet second = new ValueSet();

				while (iter.hasNext()) // Everything else in second
				{
					second.add(iter.next());
				}

				List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
				int psize = 2;
				int[] counts = new int[psize];

				try
				{
					List<NameValuePairList> lnvps = af.createPPatternAssistant(fromModule).getAllNamedValues(pattern.getLeft(), new SetValue(first), question.ctxt);
					nvplists.add(lnvps);
					counts[0] = lnvps.size();

					List<NameValuePairList> rnvps = af.createPPatternAssistant(fromModule).getAllNamedValues(pattern.getRight(), new SetValue(second), question.ctxt);
					nvplists.add(rnvps);
					counts[1] = rnvps.size();
				} catch (Exception e)
				{
					continue;
				}

				Permutor permutor = new Permutor(counts);

				while (permutor.hasNext())
				{
					try
					{
						NameValuePairMap results = new NameValuePairMap();
						int[] selection = permutor.next();

						for (int p = 0; p < psize; p++)
						{
							for (NameValuePair nvp : nvplists.get(p).get(selection[p]))
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
										VdmRuntimeError.patternFail(4126, "Values do not match union pattern", pattern.getLocation());
									}
								}
							}
						}

						finalResults.add(results.asList());
					} catch (PatternMatchException pme)
					{
						// Try next perm then...
					}
				}
			}
		}

		if (finalResults.isEmpty())
		{
			VdmRuntimeError.patternFail(4127, "Cannot match set pattern", pattern.getLocation());
		}

		return finalResults;
	}

	@Override
	public List<NameValuePairList> caseAObjectPattern(AObjectPattern pattern,
			Newquestion question) throws AnalysisException
	{
		ObjectValue objval = null;

		try
		{
			objval = question.expval.objectValue(question.ctxt);
		}
		catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, pattern.getLocation());
		}

		if (!question.ctxt.assistantFactory.getTypeComparator().isSubType(objval.getType(), pattern.getType()))
		{
			VdmRuntimeError.patternFail(4114, "Object type does not match pattern", pattern.getLocation());
		}

		List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
		int psize = pattern.getFields().size();
		int[] counts = new int[psize];
		int i = 0;

		for (ANamePatternPair npp : pattern.getFields())
		{
			Value fval = objval.get(npp.getName(), false);
			
			if (fval == null)	// Field does not exist in this object
			{
				VdmRuntimeError.patternFail(4114, "Object type does not match pattern", pattern.getLocation());
			}
			
			List<NameValuePairList> pnvps = af.createPPatternAssistant(fromModule).getAllNamedValues(npp.getPattern(), fval, question.ctxt);
			nvplists.add(pnvps);
			counts[i++] = pnvps.size();
		}

		Permutor permutor = new Permutor(counts);
		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();

		if (pattern.getFields().isEmpty())
		{
			finalResults.add(new NameValuePairList());
			return finalResults;
		}

		while (permutor.hasNext())
		{
			try
			{
				NameValuePairMap results = new NameValuePairMap();
				int[] selection = permutor.next();

				for (int p = 0; p < psize; p++)
				{
					for (NameValuePair nvp : nvplists.get(p).get(selection[p]))
					{
						Value v = results.get(nvp.name);

						if (v == null)
						{
							results.put(nvp);
						}
						else
						{
							if (!v.equals(nvp.value))
							{
								VdmRuntimeError.patternFail(4116, "Values do not match record pattern", pattern.getLocation());
							}
						}
					}
				}

				finalResults.add(results.asList()); // Consistent set of nvps
			}
			catch (PatternMatchException pme)
			{
				// try next perm
			}
		}

		if (finalResults.isEmpty())
		{
			VdmRuntimeError.patternFail(4116, "Values do not match record pattern", pattern.getLocation());
		}

		return finalResults;
	}

	@Override
	public List<NameValuePairList> defaultPPattern(PPattern pattern,
			Newquestion question) throws AnalysisException
	{
		assert false : "Should not happen!";
		return null;
	}

	@Override
	public List<NameValuePairList> createNewReturnValue(INode node,
			Newquestion question) throws AnalysisException
	{
		assert false : "Should not happen!";
		return null;
	}

	@Override
	public List<NameValuePairList> createNewReturnValue(Object node,
			Newquestion question) throws AnalysisException
	{
		assert false : "Should not happen!";
		return null;
	}

}
