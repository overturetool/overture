package org.overture.ast.assistant.type;

import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.SNumericBasicType;

public class SNumericBasicTypeAssistant {

	public static int getWeight(SNumericBasicType subn) {
		switch(subn.kindSNumericBasicType())
		{
			case AIntNumericBasicType.kindSNumericBasicType:
				return 2;
			case ANatNumericBasicType.kindSNumericBasicType:
				return 1;
			case ANatOneNumericBasicType.kindSNumericBasicType:
				return 0;
			case ARationalNumericBasicType.kindSNumericBasicType:
				return 3;
			case ARealNumericBasicType.kindSNumericBasicType:
				return 4;
		}			
		return -1;
	}
	
}
