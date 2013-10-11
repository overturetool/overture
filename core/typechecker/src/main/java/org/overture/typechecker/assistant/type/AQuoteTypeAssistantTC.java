package org.overture.typechecker.assistant.type;

import org.overture.ast.types.AQuoteType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AQuoteTypeAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AQuoteTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static String toDisplay(AQuoteType exptype) {
		return "<" + exptype.getValue() + ">";
	}

//	public static boolean equals(AQuoteType type, Object other) {
//		other = PTypeAssistantTC.deBracket(other);
//
//		if (other instanceof AQuoteType)
//		{
//			AQuoteType qother = (AQuoteType)other;
//			return type.getValue().getValue().equals(qother.getValue().getValue());
//		}
//
//		return false;
//	}

}
