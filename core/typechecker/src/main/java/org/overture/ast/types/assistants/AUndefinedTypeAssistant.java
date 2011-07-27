package org.overture.ast.types.assistants;

import org.overture.ast.types.AUndefinedType;
import org.overture.ast.types.PType;

public class AUndefinedTypeAssistant {

	public static boolean equals(AUndefinedType type, PType other) {
		
		other = PTypeAssistant.deBracket(other);

		return (other instanceof AUndefinedType);
	}

}
