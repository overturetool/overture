package org.overture.codegen.analysis.violations;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.codegen.assistant.AssistantManager;

public class TypenameComparison extends NamingComparison
{
	public TypenameComparison(String[] names, AssistantManager assistantManager)
	{
		super(names, assistantManager);
	}

	@Override
	public boolean isInvalid(ILexNameToken nameToken)
	{
		if (this.getNames().contains(nameToken.getName()))
		{
			return assistantManager.getTypeAssistant().getTypeDef(nameToken) != null;
		}

		return false;
	}
}
