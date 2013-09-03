package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AAssignmentDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AAssignmentDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static LexNameList getVariableNames(AAssignmentDefinition d)
	{

		return new LexNameList(d.getName());
	}

}
