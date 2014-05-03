package org.overture.codegen.analysis.violations;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.codegen.assistant.AssistantManager;

public class ReservedWordsComparison extends NamingComparison
{
	public ReservedWordsComparison(String[] names,
			AssistantManager assistantManager)
	{
		super(names, assistantManager);
	}

	@Override
	public boolean isInvalid(ILexNameToken nameToken)
	{
		return this.getNames().contains(nameToken.getName());
	}

}
