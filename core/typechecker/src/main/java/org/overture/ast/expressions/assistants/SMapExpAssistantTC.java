package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.SMapExp;
import org.overturetool.vdmj.lex.LexNameList;

public class SMapExpAssistantTC {

	public static LexNameList getOldNames(SMapExp expression) {
		switch (expression.kindSMapExp()) {		
		case MAPCOMP:
			return AMapCompMapExpAssistantTC.getOldNames((AMapCompMapExp) expression);
		case MAPENUM:
			return AMapEnumMapExpAssistantTC.getOldNames((AMapEnumMapExp) expression);
		default:
			assert false : "Should not happen";
			return new LexNameList();
		}
	}

}
