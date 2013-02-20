package org.overture.interpreter.assistant.pattern;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.traces.Permutor;
import org.overture.interpreter.values.FieldMap;
import org.overture.interpreter.values.FieldValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.values.RecordValue;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.pattern.ARecordPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;

public class ARecordPatternAssistantInterpreter extends ARecordPatternAssistantTC
{

	public static List<NameValuePairList> getAllNamedValues(ARecordPattern pattern,
			Value expval, Context ctxt) throws PatternMatchException
	{
		FieldMap fields = null;
		RecordValue exprec = null;

		try
		{
			exprec = expval.recordValue(ctxt);
			fields = exprec.fieldmap;
		}
		catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e,pattern.getLocation());
		}

		// if (!type.equals(exprec.type))
		if (!TypeComparator.compatible(pattern.getType(), exprec.type))
		{
			VdmRuntimeError.patternFail(4114, "Record type does not match pattern",pattern.getLocation());
		}

		if (fields.size() != pattern.getPlist().size())
		{
			VdmRuntimeError.patternFail(4115, "Record expression does not match pattern",pattern.getLocation());
		}

		Iterator<FieldValue> iter = fields.iterator();
		List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
		int psize = pattern.getPlist().size();
		int[] counts = new int[psize];
		int i = 0;

		for (PPattern p: pattern.getPlist())
		{
			List<NameValuePairList> pnvps = PPatternAssistantInterpreter.getAllNamedValues(p,iter.next().value, ctxt);
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
								VdmRuntimeError.patternFail(4116, "Values do not match record pattern",pattern.getLocation());
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
			VdmRuntimeError.patternFail(4116, "Values do not match record pattern",pattern.getLocation());
		}

		return finalResults;
	}

	public static boolean isConstrained(ARecordPattern pattern)
	{
		return PPatternListAssistantInterpreter.isConstrained(pattern.getPlist());
	}

	public static List<AIdentifierPattern> findIndentifiers(
			ARecordPattern pattern)
	{
		List<AIdentifierPattern> list = new Vector<AIdentifierPattern>();

		for (PPattern p: pattern.getPlist())
		{
			list.addAll(PPatternAssistantInterpreter.findIdentifiers(p));
		}

		return list;
	}

}
