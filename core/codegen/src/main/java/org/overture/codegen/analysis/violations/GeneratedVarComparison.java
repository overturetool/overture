package org.overture.codegen.analysis.violations;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.codegen.assistant.AssistantManager;

public class GeneratedVarComparison extends NamingComparison
{
	public GeneratedVarComparison(String[] names,
			AssistantManager assistantManager, String correctionPrefix)
	{
		super(names, assistantManager, correctionPrefix);
	}

	@Override
	public boolean mustHandleNameToken(ILexNameToken nameToken)
	{
		if(assistantManager.getTypeAssistant().getTypeDef(nameToken) != null)
		{
			return false;
		}
		
		PDefinition def = nameToken.getAncestor(PDefinition.class);
		
		if(def instanceof SOperationDefinition ||
		   def instanceof SFunctionDefinition)
		{
			return false;
		}
		
		for(String name : this.getNames())
		{
			if(nameToken.getName().startsWith(name))
			{
				return true;
			}
		}
		
		return false;
	}
}
