package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMapEnumMapExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMapEnumMapExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AMapEnumMapExp expression) {
		LexNameList list = new LexNameList();

		for (AMapletExp maplet: expression.getMembers())
		{
			list.addAll(PExpAssistantTC.getOldNames(maplet));
		}

		return list;
	}

}
