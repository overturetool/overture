package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;

public class ALetDefExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ALetDefExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(ALetDefExp expression) {
		LexNameList list = PDefinitionListAssistantTC.getOldNames(expression.getLocalDefs());
		list.addAll(PExpAssistantTC.getOldNames(expression.getExpression()));
		return list;
	}

}
