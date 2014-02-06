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
		if (this.getNames().contains(nameToken.getName()))
		{
			return TypeAssistantCG.getTypeDef(nameToken) != null;
		}

		return false;
	}
}
