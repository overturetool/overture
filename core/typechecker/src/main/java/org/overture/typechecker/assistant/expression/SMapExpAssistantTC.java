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
		switch (expression.kindSMapExp()) {		
		case AMapCompMapExp.kindSMapExp:
			return AMapCompMapExpAssistantTC.getOldNames((AMapCompMapExp) expression);
		case AMapEnumMapExp.kindSMapExp:
			return AMapEnumMapExpAssistantTC.getOldNames((AMapEnumMapExp) expression);
		default:
			assert false : "Should not happen";
			return new LexNameList();
		}
	}

}
