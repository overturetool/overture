package org.overture.interpreter.assistant.pattern;

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
	protected final String fromModule;

	@SuppressWarnings("static-access")
	public AMapPatternMapletAssistantInterpreter(IInterpreterAssistantFactory af, String fromModule)
	{
		// super(af);
		this.af = af;
		this.fromModule = fromModule;
	}

	public List<NameValuePairList> getAllNamedValues(AMapletPatternMaplet p,
			Entry<Value, Value> maplet, Context ctxt) throws AnalysisException
	{
		List<NameValuePairList> flist = af.createPPatternAssistant(fromModule).getAllNamedValues(p.getFrom(), maplet.getKey(), ctxt);
		List<NameValuePairList> tlist = af.createPPatternAssistant(fromModule).getAllNamedValues(p.getTo(), maplet.getValue(), ctxt);
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

	public List<AIdentifierPattern> findIdentifiers(AMapletPatternMaplet p)
	{
		List<AIdentifierPattern> list = new Vector<AIdentifierPattern>();

		list.addAll(af.createPPatternAssistant(fromModule).findIdentifiers(p.getFrom()));
		list.addAll(af.createPPatternAssistant(fromModule).findIdentifiers(p.getTo()));

		return list;
	}

}
