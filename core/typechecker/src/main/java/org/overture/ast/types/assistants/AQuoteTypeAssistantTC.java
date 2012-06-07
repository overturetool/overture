package org.overture.ast.types.assistants;

import org.overture.ast.types.AQuoteType;

public class AQuoteTypeAssistantTC {

	public static String toDisplay(AQuoteType exptype) {
		return "<" + exptype.getValue() + ">";
	}

	public static boolean equals(AQuoteType type, Object other) {
		other = PTypeAssistantTC.deBracket(other);

		if (other instanceof AQuoteType)
		{
			AQuoteType qother = (AQuoteType)other;
			return type.getValue().value.equals(qother.getValue().value);
		}

		return false;
	}

}
