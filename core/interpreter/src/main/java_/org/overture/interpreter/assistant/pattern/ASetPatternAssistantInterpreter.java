package org.overture.interpreter.assistant.pattern;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.assistant.type.PTypeAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.traces.Permutor;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueSet;
import org.overture.typechecker.assistant.pattern.ASetPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;

public class ASetPatternAssistantInterpreter extends ASetPatternAssistantTC
{

	public static List<NameValuePairList> getAllNamedValues(ASetPattern pattern,
			Value expval, Context ctxt) throws PatternMatchException
	{
		ValueSet values = null;

		try
		{
			values = expval.setValue(ctxt);
		}
		catch (ValueException e)
		{
			RuntimeError.patternFail(e,pattern.getLocation());
		}

		if (values.size() != pattern.getPlist().size())
		{
			RuntimeError.patternFail(4119, "Wrong number of elements for set pattern",pattern.getLocation());
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
		}
		else
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

		for (ValueSet setPerm: allSets)
		{
			Iterator<Value> iter = setPerm.iterator();

			List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
			int[] counts = new int[psize];
			int i = 0;

			try
			{
				for (PPattern p: pattern.getPlist())
				{
					List<NameValuePairList> pnvps = PPatternAssistantInterpreter.getAllNamedValues(p,iter.next(), ctxt);
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
									RuntimeError.patternFail(4120, "Values do not match set pattern",pattern.getLocation());
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
			RuntimeError.patternFail(4121, "Cannot match set pattern",pattern.getLocation());
		}

		return finalResults;
	}

	static boolean isConstrained(ASetPattern pattern)
	{
		
		if (PTypeAssistantInterpreter.isUnion(PPatternListAssistantTC.getPossibleType(pattern.getPlist(),pattern.getLocation())))
		{
			return true;	// Set types are various, so we must permute
		}

		for (PPattern p: pattern.getPlist())
		{
			if (PPatternAssistantInterpreter.isConstrained(p)) return true;
		}

		return false;
	}

	public static int getLength(ASetPattern pattern)
	{
		return pattern.getPlist().size();
	}

}
