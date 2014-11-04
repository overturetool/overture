package org.overture.interpreter.assistant.pattern;

import java.util.LinkedList;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;

public class PPatternListAssistantInterpreter extends PPatternListAssistantTC implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PPatternListAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	//FIXME: only used in 1 class. move it there.
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
