package org.overture.interpreter.assistant.pattern;

import org.overture.ast.patterns.AUnionPattern;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AUnionPatternAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AUnionPatternAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		//super(af);
		this.af = af;
	}

//	public static List<NameValuePairList> getAllNamedValues(
//			AUnionPattern pattern, Value expval, Context ctxt)
//			throws PatternMatchException
//	{
//		ValueSet values = null;
//
//		try
//		{
//			values = expval.setValue(ctxt);
//		} catch (ValueException e)
//		{
//			VdmRuntimeError.patternFail(e, pattern.getLocation());
//		}
//
//		int llen = PPatternAssistantInterpreter.getLength(pattern.getLeft());
//		int rlen = PPatternAssistantInterpreter.getLength(pattern.getRight());
//		int size = values.size();
//
//		if (llen == PPatternAssistantInterpreter.ANY && rlen > size
//				|| rlen == PPatternAssistantInterpreter.ANY && llen > size
//				|| rlen != PPatternAssistantInterpreter.ANY
//				&& llen != PPatternAssistantInterpreter.ANY
//				&& size != llen + rlen)
//		{
//			VdmRuntimeError.patternFail(4125, "Set union pattern does not match expression", pattern.getLocation());
//		}
//
//		// If the left and right sizes are zero (ie. flexible) then we have to
//		// generate a set of splits of the values, and offer these to sub-matches
//		// to see whether they fit. Otherwise, there is just one split at this level.
//
//		List<Integer> leftSizes = new Vector<Integer>();
//
//		if (llen == PPatternAssistantInterpreter.ANY)
//		{
//			if (rlen == PPatternAssistantInterpreter.ANY)
//			{
//				if (size == 0)
//				{
//					// Can't match a union b with {}
//				} else if (size % 2 == 1)
//				{
//					// Odd => add the middle, then those either side
//					int half = size / 2 + 1;
//					if (half > 0)
//					{
//						leftSizes.add(half);
//					}
//
//					for (int delta = 1; half - delta > 0; delta++)
//					{
//						leftSizes.add(half + delta);
//						leftSizes.add(half - delta);
//					}
//
//					leftSizes.add(0);
//				} else
//				{
//					// Even => add those either side of the middle
//					int half = size / 2;
//					if (half > 0)
//					{
//						leftSizes.add(half);
//					}
//
//					for (int delta = 1; half - delta > 0; delta++)
//					{
//						leftSizes.add(half + delta);
//						leftSizes.add(half - delta);
//					}
//
//					leftSizes.add(size);
//					leftSizes.add(0);
//				}
//			} else
//			{
//				leftSizes.add(size - rlen);
//			}
//		} else
//		{
//			leftSizes.add(llen);
//		}
//
//		// Since the left and right may have specific set members, we
//		// have to permute through the various set orderings to see
//		// whether there are any which match both sides. If the patterns
//		// are not constrained however, the initial ordering will be
//		// fine.
//
//		List<ValueSet> allSets;
//
//		if (isConstrained(pattern))
//		{
//			allSets = values.permutedSets();
//		} else
//		{
//			allSets = new Vector<ValueSet>();
//			allSets.add(values);
//		}
//
//		// Now loop through the various splits and attempt to match the l/r
//		// sub-patterns to the split set value.
//
//		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();
//
//		for (Integer lsize : leftSizes)
//		{
//			for (ValueSet setPerm : allSets)
//			{
//				Iterator<Value> iter = setPerm.iterator();
//				ValueSet first = new ValueSet();
//
//				for (int i = 0; i < lsize; i++)
//				{
//					first.add(iter.next());
//				}
//
//				ValueSet second = new ValueSet();
//
//				while (iter.hasNext()) // Everything else in second
//				{
//					second.add(iter.next());
//				}
//
//				List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
//				int psize = 2;
//				int[] counts = new int[psize];
//
//				try
//				{
//					List<NameValuePairList> lnvps = af.createPPatternAssistant().getAllNamedValues(pattern.getLeft(), new SetValue(first), ctxt);
//					nvplists.add(lnvps);
//					counts[0] = lnvps.size();
//
//					List<NameValuePairList> rnvps = af.createPPatternAssistant().getAllNamedValues(pattern.getRight(), new SetValue(second), ctxt);
//					nvplists.add(rnvps);
//					counts[1] = rnvps.size();
//				} catch (Exception e)
//				{
//					continue;
//				}
//
//				Permutor permutor = new Permutor(counts);
//
//				while (permutor.hasNext())
//				{
//					try
//					{
//						NameValuePairMap results = new NameValuePairMap();
//						int[] selection = permutor.next();
//
//						for (int p = 0; p < psize; p++)
//						{
//							for (NameValuePair nvp : nvplists.get(p).get(selection[p]))
//							{
//								Value v = results.get(nvp.name);
//
//								if (v == null)
//								{
//									results.put(nvp);
//								} else
//								// Names match, so values must also
//								{
//									if (!v.equals(nvp.value))
//									{
//										VdmRuntimeError.patternFail(4126, "Values do not match union pattern", pattern.getLocation());
//									}
//								}
//							}
//						}
//
//						finalResults.add(results.asList());
//					} catch (PatternMatchException pme)
//					{
//						// Try next perm then...
//					}
//				}
//			}
//		}
//
//		if (finalResults.isEmpty())
//		{
//			VdmRuntimeError.patternFail(4127, "Cannot match set pattern", pattern.getLocation());
//		}
//
//		return finalResults;
//	}
//
//	public static boolean isConstrained(AUnionPattern pattern)
//	{
//		return PPatternAssistantInterpreter.isConstrained(pattern.getLeft())
//				|| PPatternAssistantInterpreter.isConstrained(pattern.getRight());
//	}

//	public static int getLength(AUnionPattern pattern)
//	{
//		int llen = PPatternAssistantInterpreter.getLength(pattern.getLeft());
//		int rlen = PPatternAssistantInterpreter.getLength(pattern.getRight());
//		return llen == PPatternAssistantInterpreter.ANY
//				|| rlen == PPatternAssistantInterpreter.ANY ? PPatternAssistantInterpreter.ANY
//				: llen + rlen;
//	}

//	public static List<AIdentifierPattern> findIdentifiers(AUnionPattern pattern)
//	{
//		List<AIdentifierPattern> list = new Vector<AIdentifierPattern>();
//		list.addAll(PPatternAssistantInterpreter.findIdentifiers(pattern.getLeft()));
//		list.addAll(PPatternAssistantInterpreter.findIdentifiers(pattern.getRight()));
//		return list;
//	}

}
