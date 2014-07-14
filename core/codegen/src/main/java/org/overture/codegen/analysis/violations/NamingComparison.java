package org.overture.codegen.analysis.violations;

import java.util.Arrays;
import java.util.List;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.codegen.assistant.AssistantManager;

public abstract class NamingComparison
{
	protected List<String> names;
	protected AssistantManager assistantManager;
	protected String correctionPrefix;
		
	public NamingComparison(String[] names, AssistantManager assistantManager, String correctionPrefix)
	{
		this.names = Arrays.asList(names);
		this.assistantManager = assistantManager;
		this.correctionPrefix = correctionPrefix;
	}
	
	public abstract boolean mustHandleNameToken(ILexNameToken nameToken);
	
	public void correctNameToken(ILexNameToken nameToken)
	{
		String module = nameToken.getModule();
		String correctedName = correctionPrefix + nameToken.getName();
		ILexLocation location = nameToken.getLocation();
		boolean old = nameToken.getOld();
		boolean explicit = nameToken.getExplicit();
		
		LexNameToken replaceMent = new LexNameToken(module, correctedName, location, old, explicit);
		nameToken.parent().replaceChild(nameToken, replaceMent);
	}
	
	public List<String> getNames()
	{
		return this.names;
	}
}
