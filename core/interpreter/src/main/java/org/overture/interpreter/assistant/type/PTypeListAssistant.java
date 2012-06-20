package org.overture.interpreter.assistant.type;

import java.util.List;

import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Quantifier;
import org.overture.interpreter.values.QuantifierList;
import org.overture.interpreter.values.TupleValue;
import org.overture.interpreter.values.ValueList;

public class PTypeListAssistant  
{

	public static ValueList getAllValues(List<PType> linkedList, Context ctxt) throws ValueException
	{
		QuantifierList quantifiers = new QuantifierList();
		int n = 0;

		for (PType t: linkedList)
		{
			LexNameToken name = new LexNameToken("#", String.valueOf(n), t.getLocation());
			PPattern p = AstFactory.newAIdentifierPattern(name);
			Quantifier q = new Quantifier(p, PTypeAssistantInterpreter.getAllValues(t,ctxt));
			quantifiers.add(q);
		}

		quantifiers.init();
		ValueList results = new ValueList();

		while (quantifiers.hasNext(ctxt))
		{
			NameValuePairList nvpl = quantifiers.next();
			ValueList list = new ValueList();

			for (NameValuePair nvp: nvpl)
			{
				list.add(nvp.value);
			}
			
			results.add(new TupleValue(list));
		}
		
		return results;
	}

}
