package org.overture.codegen.trans;

import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;

public class DeclarationTag
{
	private boolean declared;

	private AVarLocalDeclCG successVarDecl;
	
	public DeclarationTag(boolean declared, AVarLocalDeclCG successVarDecl)
	{
		this.declared = declared;
		this.successVarDecl = successVarDecl;
	}

	public boolean isDeclared()
	{
		return declared;
	}
	
	public AVarLocalDeclCG getSuccessVarDecl()
	{
		return successVarDecl;
	}
}
