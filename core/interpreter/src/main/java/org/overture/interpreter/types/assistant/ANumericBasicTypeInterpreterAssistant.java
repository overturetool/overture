package org.overture.interpreter.types.assistant;

import org.overture.interpreter.ast.types.SNumericBasicTypeInterpreter;

public class ANumericBasicTypeInterpreterAssistant {

	public static int getWeight(SNumericBasicTypeInterpreter subn) {
		switch(subn.kindSNumericBasicTypeInterpreter())
		{
			case INT:
				return 2;
			case NAT:
				return 1;
			case NATONE:
				return 0;
			case RATIONAL:
				return 3;
			case REAL:
				return 4;
		}			
		return -1;
	}

}
