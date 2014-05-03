package org.overture.codegen.analysis.violations;

import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
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
			PDefinition def = assistantManager.getTypeAssistant().getTypeDef(nameToken);

			if (def instanceof ATypeDefinition)
			{
				ATypeDefinition typeDef = (ATypeDefinition) def;
				return typeDef.getInvType() == null;
			}

			return def != null;
		}

		return false;
	}
}
