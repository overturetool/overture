package org.overture.typechecker.assistant.type;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AVoidReturnTypeAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AVoidReturnTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
//	public static boolean equals(AVoidReturnType type, Object other) {
//		other = PTypeAssistantTC.deBracket(other);
//
//		return (other instanceof AVoidReturnType);
//	}

}
