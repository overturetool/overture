package org.overture.interpreter.assistant.pattern;

import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;

public class AMapPatternMapletAssistantInterpreter

{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMapPatternMapletAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		//super(af);
		this.af = af;
	}

	public static boolean isConstrained(AMapletPatternMaplet p)
	{
		if (PPatternAssistantInterpreter.isConstrained(p.getFrom())
				|| PPatternAssistantInterpreter.isConstrained(p.getTo()))
		{
			return true;
		}

		return af.createPTypeAssistant().isUnion(af.createPPatternAssistant().getPossibleType(p.getFrom()))
				|| af.createPTypeAssistant().isUnion(af.createPPatternAssistant().getPossibleType(p.getTo()));
	}

	public static List<NameValuePairList> getAllNamedValues(
			AMapletPatternMaplet p, Entry<Value, Value> maplet, Context ctxt)
			throws PatternMatchException
	{
		List<NameValuePairList> flist = PPatternAssistantInterpreter.getAllNamedValues(p.getFrom(), maplet.getKey(), ctxt);
		List<NameValuePairList> tlist = PPatternAssistantInterpreter.getAllNamedValues(p.getTo(), maplet.getValue(), ctxt);
		List<NameValuePairList> results = new Vector<NameValuePairList>();

		for (NameValuePairList f : flist)
		{
			for (NameValuePairList t : tlist)
			{
				NameValuePairList both = new NameValuePairList();
				both.addAll(f);
				both.addAll(t);
				results.add(both); // Every combination of from/to mappings
			}
		}

		return results;
	}

	public static List<AIdentifierPattern> findIdentifiers(
			AMapletPatternMaplet p)
	{
		List<AIdentifierPattern> list = new Vector<AIdentifierPattern>();

		list.addAll(PPatternAssistantInterpreter.findIdentifiers(p.getFrom()));
		list.addAll(PPatternAssistantInterpreter.findIdentifiers(p.getTo()));

		return list;
	}

}
