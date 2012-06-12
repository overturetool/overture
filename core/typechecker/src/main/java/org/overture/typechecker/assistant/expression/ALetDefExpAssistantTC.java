package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;

public class ALetDefExpAssistantTC {

	public static LexNameList getOldNames(ALetDefExp expression) {
		LexNameList list = PDefinitionListAssistantTC.getOldNames(expression.getLocalDefs());
		list.addAll(PExpAssistantTC.getOldNames(expression.getExpression()));
		return list;
	}

}
