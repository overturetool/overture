package org.overture.ast.types.assistants;

import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.AUnknownType;

public class AUnknownTypeAssistant {
	
	public static boolean isNumeric(AUnknownType type) {
		return true;
	}
	
	public static ARealNumericBasicType getNumeric(AUnknownType type) {
		return AstFactory.newARealNumericBasicType(type.getLocation());
	}
	
}
