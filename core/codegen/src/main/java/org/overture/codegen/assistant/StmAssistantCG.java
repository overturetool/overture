package org.overture.codegen.assistant;

import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;

public class StmAssistantCG extends AssistantBase
{
	public StmAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}
	
	public void injectDeclAsStm(ABlockStmCG block, AVarLocalDeclCG decl)
	{
		ABlockStmCG wrappingBlock = new ABlockStmCG();
		
		wrappingBlock.getLocalDefs().add(decl);
		
		block.getStatements().add(wrappingBlock);
	}
}
