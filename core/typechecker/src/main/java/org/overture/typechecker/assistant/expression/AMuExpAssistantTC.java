package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AMuExp;
import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMuExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMuExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AMuExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getRecord());

		for (ARecordModifier rm: expression.getModifiers())
		{
			list.addAll(ARecordModifierAssistantTC.getOldNames(rm));
		}

		return list;
	}

}
