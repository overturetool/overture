package org.overture.ast.types.assistants;

import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.PType;

public class AVoidReturnTypeAssistant {

	public static boolean equals(AVoidReturnType type, PType other) {
		other = PTypeAssistant.deBracket(other);

		return (other instanceof AVoidReturnType);
	}

}
