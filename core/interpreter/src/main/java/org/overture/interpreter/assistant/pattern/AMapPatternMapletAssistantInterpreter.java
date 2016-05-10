package org.overture.interpreter.assistant.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;

public class AMapPatternMapletAssistantInterpreter implements IAstAssistant

{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMapPatternMapletAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

	public List<NameValuePairList> getAllNamedValues(AMapletPatternMaplet p,
			Entry<Value, Value> maplet, Context ctxt) throws AnalysisException
	{
		List<NameValuePairList> flist = af.createPPatternAssistant().getAllNamedValues(p.getFrom(), maplet.getKey(), ctxt);
		List<NameValuePairList> tlist = af.createPPatternAssistant().getAllNamedValues(p.getTo(), maplet.getValue(), ctxt);
		List<NameValuePairList> results = new ArrayList<NameValuePairList>();

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

	public List<AIdentifierPattern> findIdentifiers(AMapletPatternMaplet p)
	{
		List<AIdentifierPattern> list = new ArrayList<AIdentifierPattern>();

		list.addAll(af.createPPatternAssistant().findIdentifiers(p.getFrom()));
		list.addAll(af.createPPatternAssistant().findIdentifiers(p.getTo()));

		return list;
	}

}
