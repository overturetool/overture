package org.overture.typechecker.assistant.expression;


import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ARecordModifierAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ARecordModifierAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(
			ARecordModifier rm) {
		return PExpAssistantTC.getOldNames(rm.getValue());
	}

}
