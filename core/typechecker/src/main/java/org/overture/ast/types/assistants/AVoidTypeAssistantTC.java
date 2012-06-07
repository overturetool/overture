package org.overture.ast.types.assistants;

import org.overture.ast.types.AVoidType;

public class AVoidTypeAssistantTC {

	public static boolean equals(AVoidType type, Object other) {
		other = PTypeAssistantTC.deBracket(other);

		return (other instanceof AVoidType);
	}

}
