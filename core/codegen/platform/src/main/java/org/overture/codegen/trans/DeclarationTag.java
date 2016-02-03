package org.overture.codegen.trans;

import org.overture.codegen.ir.declarations.AVarDeclIR;

public class DeclarationTag
{
	private boolean declared;

	private AVarDeclIR successVarDecl;

	public DeclarationTag(boolean declared, AVarDeclIR successVarDecl)
	{
		this.declared = declared;
		this.successVarDecl = successVarDecl;
	}

	public boolean isDeclared()
	{
		return declared;
	}

	public AVarDeclIR getSuccessVarDecl()
	{
		return successVarDecl;
	}
}
