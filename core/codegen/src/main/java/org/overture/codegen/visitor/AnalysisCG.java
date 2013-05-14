package org.overture.codegen.visitor;

import org.overture.codegen.nodes.ClassCG;

public class AnalysisCG
{
	private CodeGenContext codeGenContext;
	private ClassCG codeGenClass;
	public AnalysisCG(CodeGenContext codeGenContext, ClassCG codeGenClass)
	{
		super();
		this.codeGenContext = codeGenContext;
		this.codeGenClass = codeGenClass;
	}
	
	public CodeGenContext getCodeGenContext()
	{
		return codeGenContext;
	}

	public ClassCG getCodeGenClass()
	{
		return codeGenClass;
	}
}
