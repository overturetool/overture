package org.overture.codegen.analysis.violations;

import java.util.Arrays;
import java.util.List;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.codegen.assistant.AssistantManager;

public abstract class NamingComparison
{
	private List<String> names;
	protected AssistantManager assistantManager;
		
	public NamingComparison(String[] names, AssistantManager assistantManager)
	{
		this.names = Arrays.asList(names);
		this.assistantManager = assistantManager != null ? assistantManager : new AssistantManager();
	}
	
	public abstract boolean isInvalid(ILexNameToken nameToken);
	
	public List<String> getNames()
	{
		return this.names;
	}
}
