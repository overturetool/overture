package org.overture.codegen.trans.patterns;

import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.statements.ABlockStmIR;

public class DeclBlockPair
{
	private AVarDeclIR decl;
	private AVarDeclIR nextDecl;
	private ABlockStmIR block;
	
	public DeclBlockPair(AVarDeclIR decl, AVarDeclIR nextDecl,
			ABlockStmIR block)
	{
		super();
		this.decl = decl;
		this.nextDecl = nextDecl;
		this.block = block;
	}
	public AVarDeclIR getDecl()
	{
		return decl;
	}
	public AVarDeclIR getNextDecl()
	{
		return nextDecl;
	}
	public ABlockStmIR getBlock()
	{
		return block;
	}
}
