package org.overture.interpreter.patterns.assistant;

import java.util.List;

import org.overture.interpreter.ast.patterns.PPatternInterpreter;
import org.overture.interpreter.ast.types.AUnknownTypeInterpreter;
import org.overture.interpreter.ast.types.PTypeInterpreter;
import org.overture.interpreter.types.assistant.PTypeSet;
import org.overturetool.interpreter.vdmj.lex.LexLocation;



public class PPatternListAssistant
{
	public static PTypeInterpreter getPossibleType(List<PPatternInterpreter> list, LexLocation location)
	{
		switch (list.size())
		{
			case 0:
				return new AUnknownTypeInterpreter(location,false);

			case 1:
				return PPatternInterpreterAssistant.getPossibleType(list.get(0));

			default:
        		PTypeSet list2 = new PTypeSet();

        		for (PPatternInterpreter p: list)
        		{
        			list2.add(PPatternInterpreterAssistant.getPossibleType(p));
        		}

        		return list2.getType(location);		// NB. a union of types
		}
	}
}
