package org.overture.typechecker.assistant.type;

import org.overture.ast.types.AVoidType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AVoidTypeAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AVoidTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static boolean equals(AVoidType type, Object other) {
		other = PTypeAssistantTC.deBracket(other);

		return (other instanceof AVoidType);
	}

}
