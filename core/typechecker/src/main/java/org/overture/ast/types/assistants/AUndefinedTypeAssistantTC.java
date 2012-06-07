package org.overture.ast.types.assistants;

import org.overture.ast.types.AUndefinedType;

public class AUndefinedTypeAssistantTC {

	public static boolean equals(AUndefinedType type, Object other) {
		
		other = PTypeAssistantTC.deBracket(other);

		return (other instanceof AUndefinedType);
	}
	

}
