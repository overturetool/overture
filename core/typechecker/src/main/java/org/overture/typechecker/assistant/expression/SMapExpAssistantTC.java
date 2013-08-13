package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.SMapExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class SMapExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public SMapExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(SMapExp expression) {
		if (expression instanceof AMapCompMapExp) {
			return AMapCompMapExpAssistantTC.getOldNames((AMapCompMapExp) expression);
		} else if (expression instanceof AMapEnumMapExp) {
			return AMapEnumMapExpAssistantTC.getOldNames((AMapEnumMapExp) expression);
		} else {
			assert false : "Should not happen";
			return new LexNameList();
		}
	}

}
