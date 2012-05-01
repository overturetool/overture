package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapletExp;
import org.overturetool.vdmj.lex.LexNameList;

public class AMapEnumMapExpAssistantTC {

	public static LexNameList getOldNames(AMapEnumMapExp expression) {
		LexNameList list = new LexNameList();

		for (AMapletExp maplet: expression.getMembers())
		{
			list.addAll(PExpAssistantTC.getOldNames(maplet));
		}

		return list;
	}

}
