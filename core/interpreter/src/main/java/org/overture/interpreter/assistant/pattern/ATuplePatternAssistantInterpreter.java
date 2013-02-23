package org.overture.interpreter.assistant.pattern;

import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.traces.Permutor;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.pattern.ATuplePatternAssistantTC;

public class ATuplePatternAssistantInterpreter extends ATuplePatternAssistantTC
{

	public static List<NameValuePairList> getAllNamedValues(ATuplePattern pattern,
			Value expval, Context ctxt) throws PatternMatchException
	{
		ValueList values = null;

		try
		{
			values = expval.tupleValue(ctxt);
		}
		catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e,pattern.getLocation());
		}

		if (values.size() != pattern.getPlist().size())
		{
			VdmRuntimeError.patternFail(4123, "Tuple expression does not match pattern",pattern.getLocation());
		}

		ListIterator<Value> iter = values.listIterator();
		List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
		int psize = pattern.getPlist().size();
		int[] counts = new int[psize];
		int i = 0;

		for (PPattern p: pattern.getPlist())
		{
			List<NameValuePairList> pnvps = PPatternAssistantInterpreter.getAllNamedValues(p, iter.next(), ctxt);
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
								VdmRuntimeError.patternFail(4124, "Values do not match tuple pattern",pattern.getLocation());
							}
						}
					}
				}

				finalResults.add(results.asList());		// Consistent set of nvps
			}
			catch (PatternMatchException pme)
			{
				// try next perm
			}
		}

		if (finalResults.isEmpty())
		{
			VdmRuntimeError.patternFail(4124, "Values do not match tuple pattern",pattern.getLocation());
		}

		return finalResults;
	}

	public static boolean isConstrained(ATuplePattern pattern)
	{
		return PPatternListAssistantInterpreter.isConstrained(pattern.getPlist());
	}

	public static List<AIdentifierPattern> findIdentifiers(
			ATuplePattern pattern)
	{
		List<AIdentifierPattern> list = new Vector<AIdentifierPattern>();

		for (PPattern p: pattern.getPlist())
		{
			list.addAll(PPatternAssistantInterpreter.findIdentifiers(p));
		}

		return list;
	}
	
}
