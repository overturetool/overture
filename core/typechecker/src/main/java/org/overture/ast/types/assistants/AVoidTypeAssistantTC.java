package org.overture.ast.types.assistants;

import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;

public class AVoidTypeAssistantTC {

	public static boolean equals(AVoidType type, PType other) {
		other = PTypeAssistant.deBracket(other);

		return (other instanceof AVoidType);
	}

}
