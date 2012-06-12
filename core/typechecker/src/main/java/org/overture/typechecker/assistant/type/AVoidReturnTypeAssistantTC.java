package org.overture.typechecker.assistant.type;

import org.overture.ast.types.AVoidReturnType;

public class AVoidReturnTypeAssistantTC {

	public static boolean equals(AVoidReturnType type, Object other) {
		other = PTypeAssistantTC.deBracket(other);

		return (other instanceof AVoidReturnType);
	}

}
