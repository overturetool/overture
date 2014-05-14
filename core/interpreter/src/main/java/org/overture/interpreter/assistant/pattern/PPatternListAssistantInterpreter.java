package org.overture.interpreter.assistant.pattern;

import java.util.LinkedList;

import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;

public class PPatternListAssistantInterpreter extends PPatternListAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PPatternListAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public boolean isConstrained(LinkedList<PPattern> plist)
	{
		for (PPattern p : plist)
		{
			if (af.createPPatternAssistant().isConstrained(p))
			{
				return true; // NB. OR
			}
		}

		return false;
	}

}
