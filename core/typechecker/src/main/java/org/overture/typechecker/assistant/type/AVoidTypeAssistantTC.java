package org.overture.typechecker.assistant.type;

import org.overture.ast.types.AVoidType;

public class AVoidTypeAssistantTC {

	public static boolean equals(AVoidType type, Object other) {
		other = PTypeAssistantTC.deBracket(other);

		return (other instanceof AVoidType);
	}

}
