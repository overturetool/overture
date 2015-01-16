package org.overture.interpreter.assistant.type;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Quantifier;
import org.overture.interpreter.values.QuantifierList;
import org.overture.interpreter.values.TupleValue;
import org.overture.interpreter.values.ValueList;

public class PTypeListAssistant implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PTypeListAssistant(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	//FIXME used in a single class. move it there
	public ValueList getAllValues(List<PType> linkedList, Context ctxt)
			throws AnalysisException
	{
		QuantifierList quantifiers = new QuantifierList();
		int n = 0;

		for (PType t : linkedList)
		{
			LexNameToken name = new LexNameToken("#", String.valueOf(n), t.getLocation());
			PPattern p = AstFactory.newAIdentifierPattern(name);
			Quantifier q = new Quantifier(p, af.createPTypeAssistant().getAllValues(t, ctxt));
			quantifiers.add(q);
		}

		quantifiers.init(ctxt, true);
		ValueList results = new ValueList();

		while (quantifiers.hasNext())
		{
			NameValuePairList nvpl = quantifiers.next();
			ValueList list = new ValueList();

			for (NameValuePair nvp : nvpl)
			{
				list.add(nvp.value);
			}

			results.add(new TupleValue(list));
		}

		return results;
	}

}
