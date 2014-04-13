package org.overture.interpreter.assistant.pattern;

import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.typechecker.assistant.pattern.AMapUnionPatternAssistantTC;

public class AMapUnionPatternAssistantInterpreter extends
		AMapUnionPatternAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMapUnionPatternAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

//	public static List<NameValuePairList> getAllNamedValues(
//			AMapUnionPattern pattern, Value expval, Context ctxt)
//			throws PatternMatchException
//	{
//		ValueMap values = null;
//
//		try
//		{
//			values = expval.mapValue(ctxt);
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
//			VdmRuntimeError.patternFail(4155, "Map union pattern does not match expression", pattern.getLocation());
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
//					// Can't match a munion b with {|->}
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
//		// Since the left and right may have specific element members, we
//		// have to permute through the various map orderings to see
//		// whether there are any which match both sides. If the patterns
//		// are not constrained however, the initial ordering will be
//		// fine.
//
//		List<ValueMap> allMaps;
//
//		if (isConstrained(pattern))
//		{
//			allMaps = values.permutedMaps();
//		} else
//		{
//			allMaps = new Vector<ValueMap>();
//			allMaps.add(values);
//		}
//
//		// Now loop through the various splits and attempt to match the l/r
//		// sub-patterns to the split map value.
//
//		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();
//
//		for (Integer lsize : leftSizes)
//		{
//			for (ValueMap setPerm : allMaps)
//			{
//				Iterator<Entry<Value, Value>> iter = setPerm.entrySet().iterator();
//				ValueMap first = new ValueMap();
//
//				for (int i = 0; i < lsize; i++)
//				{
//					Entry<Value, Value> e = iter.next();
//					first.put(e.getKey(), e.getValue());
//				}
//
//				ValueMap second = new ValueMap();
//
//				while (iter.hasNext()) // Everything else in second
//				{
//					Entry<Value, Value> e = iter.next();
//					second.put(e.getKey(), e.getValue());
//				}
//
//				List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
//				int psize = 2;
//				int[] counts = new int[psize];
//
//				try
//				{
//					List<NameValuePairList> lnvps = af.createPPatternAssistant().getAllNamedValues(pattern.getLeft(), new MapValue(first), ctxt);
//					nvplists.add(lnvps);
//					counts[0] = lnvps.size();
//
//					List<NameValuePairList> rnvps = af.createPPatternAssistant().getAllNamedValues(pattern.getRight(), new MapValue(second), ctxt);
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
//			VdmRuntimeError.patternFail(4156, "Cannot match map pattern", pattern.getLocation());
//		}
//
//		return finalResults;
//	}

//	public static boolean isConstrained(AMapUnionPattern pattern)
//	{
//		return PPatternAssistantInterpreter.isConstrained(pattern.getLeft())
//				|| PPatternAssistantInterpreter.isConstrained(pattern.getRight());
//	}

//	public static int getLength(AMapUnionPattern pattern)
//	{
//		int llen = PPatternAssistantInterpreter.getLength(pattern.getLeft());
//		int rlen = PPatternAssistantInterpreter.getLength(pattern.getRight());
//		return llen == PPatternAssistantInterpreter.ANY
//				|| rlen == PPatternAssistantInterpreter.ANY ? PPatternAssistantInterpreter.ANY
//				: llen + rlen;
//	}

//	public static List<AIdentifierPattern> findIdentifiers(
//			AMapUnionPattern pattern)
//	{
//		List<AIdentifierPattern> list = new Vector<AIdentifierPattern>();
//		list.addAll(PPatternAssistantInterpreter.findIdentifiers(pattern.getLeft()));
//		list.addAll(PPatternAssistantInterpreter.findIdentifiers(pattern.getRight()));
//		return list;
//	}

}
