package org.overture.interpreter.assistant.pattern;

import java.util.LinkedList;

import org.overture.ast.patterns.PPattern;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;

public class PPatternListAssistantInterpreter extends PPatternListAssistantTC {

	public static boolean isConstrained(LinkedList<PPattern> plist)
	{
		for (PPattern p: plist)
		{
			if (PPatternAssistantInterpreter.isConstrained(p)) return true;		// NB. OR
		}

		return false;
	}
		
}
