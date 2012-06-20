package org.overture.interpreter.assistant.pattern;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.traces.Permutor;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueMap;

public class AMapPatternAssistantInterpreter
{

	public static List<NameValuePairList> getAllNamedValues(AMapPattern pattern,
			Value expval, Context ctxt) throws PatternMatchException
	{
		ValueMap values = null;

		try
		{
			values = expval.mapValue(ctxt);
		}
		catch (ValueException e)
		{
			RuntimeError.patternFail(e,pattern.getLocation());
		}

		if (values.size() != pattern.getMaplets().size())
		{
			RuntimeError.patternFail(4152, "Wrong number of elements for map pattern",pattern.getLocation());
		}

		// Since the member patterns may indicate specific map members, we
		// have to permute through the various map orderings to see
		// whether there are any which match both sides. If the members
		// are not constrained however, the initial ordering will be
		// fine.

		List<ValueMap> allMaps;

		if (isConstrained(pattern))
		{
			allMaps = values.permutedMaps();
		}
		else
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

		for (ValueMap mapPerm: allMaps)
		{
			Iterator<Entry<Value, Value>> iter = mapPerm.entrySet().iterator();

			List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
			int[] counts = new int[psize];
			int i = 0;

			try
			{
				for (AMapletPatternMaplet p: pattern.getMaplets())
				{
					List<NameValuePairList> pnvps = AMapPatternMapletAssistantInterpreter.getAllNamedValues(p,iter.next(), ctxt);
					nvplists.add(pnvps);
					counts[i++] = pnvps.size();
				}
			}
			catch (Exception e)
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

					for (int p=0; p<psize; p++)
					{
						for (NameValuePair nvp: nvplists.get(p).get(selection[p]))
						{
							Value v = results.get(nvp.name);

							if (v == null)
							{
								results.put(nvp);
							}
							else	// Names match, so values must also
							{
								if (!v.equals(nvp.value))
								{
									RuntimeError.patternFail(4153, "Values do not match map pattern",pattern.getLocation());
								}
							}
						}
					}

					finalResults.add(results.asList());
				}
				catch (PatternMatchException pme)
				{
					// Try next perm then...
				}
			}
		}

		if (finalResults.isEmpty())
		{
			RuntimeError.patternFail(4154, "Cannot match map pattern",pattern.getLocation());
		}

		return finalResults;
	}

	public static boolean isConstrained(AMapPattern pattern)
	{
		for (AMapletPatternMaplet p: pattern.getMaplets())
		{
			if (AMapPatternMapletAssistantInterpreter.isConstrained(p)) return true;
		}

		return false;
	}

	public static int getLength(AMapPattern pattern)
	{
		return pattern.getMaplets().size();
	}

	public static List<AIdentifierPattern> findIdentifiers(AMapPattern pattern)
	{
		List<AIdentifierPattern> list = new Vector<AIdentifierPattern>();

		for (AMapletPatternMaplet p: pattern.getMaplets())
		{
			list.addAll(AMapPatternMapletAssistantInterpreter.findIdentifiers(p));
		}

		return list;
	}

}
