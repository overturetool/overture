package org.overture.typechecker.assistant.type;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AUndefinedTypeAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AUndefinedTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
//	public static boolean equals(AUndefinedType type, Object other) {
//		
//		other = PTypeAssistantTC.deBracket(other);
//
//		return (other instanceof AUndefinedType);
//	}
	

}
