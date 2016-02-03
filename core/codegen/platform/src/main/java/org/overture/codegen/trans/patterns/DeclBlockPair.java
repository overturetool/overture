package org.overture.codegen.trans.patterns;

import org.overture.codegen.ir.declarations.AVarDeclCG;
import org.overture.codegen.ir.statements.ABlockStmCG;

public class DeclBlockPair
{
	private AVarDeclCG decl;
	private AVarDeclCG nextDecl;
	private ABlockStmCG block;
	
	public DeclBlockPair(AVarDeclCG decl, AVarDeclCG nextDecl,
			ABlockStmCG block)
	{
		super();
		this.decl = decl;
		this.nextDecl = nextDecl;
		this.block = block;
	}
	public AVarDeclCG getDecl()
	{
		return decl;
	}
	public AVarDeclCG getNextDecl()
	{
		return nextDecl;
	}
	public ABlockStmCG getBlock()
	{
		return block;
	}
}
