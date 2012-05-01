package org.overture.ast.expressions.assistants;

import org.overture.ast.definitions.assistants.PDefinitionListAssistantTC;
import org.overture.ast.expressions.ALetDefExp;
import org.overturetool.vdmj.lex.LexNameList;

public class ALetDefExpAssistantTC {

	public static LexNameList getOldNames(ALetDefExp expression) {
		LexNameList list = PDefinitionListAssistantTC.getOldNames(expression.getLocalDefs());
		list.addAll(PExpAssistantTC.getOldNames(expression.getExpression()));
		return list;
	}

}
