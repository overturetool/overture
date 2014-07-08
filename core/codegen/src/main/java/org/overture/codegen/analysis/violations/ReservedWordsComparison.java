package org.overture.codegen.analysis.violations;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.codegen.assistant.AssistantManager;

public class ReservedWordsComparison extends NamingComparison
{
	public ReservedWordsComparison(String[] names,
			AssistantManager assistantManager, String correctionPrefix)
	{
		super(names, assistantManager, correctionPrefix);
	}

	@Override
	public boolean mustHandleNameToken(ILexNameToken nameToken)
	{
		return this.getNames().contains(nameToken.getName());
	}

}
