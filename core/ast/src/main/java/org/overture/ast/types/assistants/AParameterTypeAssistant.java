package org.overture.ast.types.assistants;

import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.ARealNumericBasicType;

public class AParameterTypeAssistant {

	public static boolean isNumeric(AParameterType type) {
		return true;
	}
	
	public static ARealNumericBasicType getNumeric(AParameterType type) {
		return AstFactory.newARealNumericBasicType(type.getLocation());
	}
	
}
