package org.overture.codegen.trans;

import org.overture.codegen.cgast.declarations.AVarDeclCG;

public class DeclarationTag
{
	private boolean declared;

	private AVarDeclCG successVarDecl;

	public DeclarationTag(boolean declared, AVarDeclCG successVarDecl)
	{
		this.declared = declared;
		this.successVarDecl = successVarDecl;
	}

	public boolean isDeclared()
	{
		return declared;
	}

	public AVarDeclCG getSuccessVarDecl()
	{
		return successVarDecl;
	}
}
