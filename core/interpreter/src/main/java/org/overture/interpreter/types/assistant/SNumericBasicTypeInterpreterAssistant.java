package org.overture.interpreter.types.assistant;

import org.overture.interpreter.ast.types.AIntNumericBasicTypeInterpreter;
import org.overture.interpreter.ast.types.ANatNumericBasicTypeInterpreter;
import org.overture.interpreter.ast.types.ANatOneNumericBasicTypeInterpreter;
import org.overture.interpreter.ast.types.PTypeInterpreter;
import org.overturetool.interpreter.vdmj.lex.LexLocation;




public class SNumericBasicTypeInterpreterAssistant {

	

	public static PTypeInterpreter typeOf(long value, LexLocation location) {
		if (value > 0)
		{
			return new ANatOneNumericBasicTypeInterpreter(location,false);
		}
		else if (value >= 0)
		{
			return new ANatNumericBasicTypeInterpreter(location,false);
		}
		else
		{
			return new AIntNumericBasicTypeInterpreter(location,false);
		}
	}

}
