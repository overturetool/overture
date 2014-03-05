package org.overture.codegen.analysis.violations;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.codegen.assistant.TypeAssistantCG;

public class TypenameComparison extends NamingComparison
{

	public TypenameComparison(String[] names)
	{
		super(names);
	}

	@Override
	public boolean isInvalid(ILexNameToken nameToken)
	{
		TypeAssistantCG typeAsssitant = new TypeAssistantCG();
		
		if (this.getNames().contains(nameToken.getName()))
		{
			return typeAsssitant.getTypeDef(nameToken) != null;
		}

		return false;
	}
}
