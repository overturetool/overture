package org.overture.interpreter.assistant.pattern;

import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.interpreter.assistant.type.PTypeAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.pattern.AMapPatternAssistantTC;

public class AMapPatternMapletAssistantInterpreter extends
		AMapPatternAssistantTC
{


	public static boolean isConstrained(AMapletPatternMaplet p)
	{
		if (PPatternAssistantInterpreter.isConstrained(p.getFrom()) || PPatternAssistantInterpreter.isConstrained(p.getTo()))
		{
			return true;
		}

		return (PTypeAssistantInterpreter.isUnion(PPatternAssistantInterpreter.getPossibleType(p.getFrom())) ||  
				PTypeAssistantInterpreter.isUnion(PPatternAssistantInterpreter.getPossibleType(p.getTo())));
	}

	public static List<NameValuePairList> getAllNamedValues(
			AMapletPatternMaplet p, Entry<Value, Value> maplet, Context ctxt) throws PatternMatchException
	{
		List<NameValuePairList> flist = PPatternAssistantInterpreter.getAllNamedValues(p.getFrom(), maplet.getKey(), ctxt);
		List<NameValuePairList> tlist = PPatternAssistantInterpreter.getAllNamedValues(p.getTo(), maplet.getKey(), ctxt);
		List<NameValuePairList> results = new Vector<NameValuePairList>();

		for (NameValuePairList f: flist)
		{
			for (NameValuePairList t: tlist)
			{
				NameValuePairList both = new NameValuePairList();
				both.addAll(f);
				both.addAll(t);
				results.add(both);	// Every combination of from/to mappings
			}
		}

		return results;
	}

}
