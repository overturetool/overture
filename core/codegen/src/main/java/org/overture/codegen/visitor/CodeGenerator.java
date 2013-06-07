package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.typedeclarations.AClassTypeDeclCG;
import org.overture.codegen.logging.ILogger;

public class CodeGenerator
{
	private ILogger log;

	private CodeGenInfo codeGenInfo;
	
	public CodeGenerator(ILogger log)
	{
		this.log = log;		
		this.codeGenInfo = new CodeGenInfo(this);
	}
			
	public ILogger getLog()
	{
		return log;
	}
		
	public AClassTypeDeclCG generateFrom(SClassDefinition classDef) throws AnalysisException
	{
		return classDef.apply(codeGenInfo.getClassVisitor(), codeGenInfo);
	}
	
	public PExpCG generateFrom(PExp exp) throws AnalysisException
	{
		return exp.apply(codeGenInfo.getExpVisitor(), codeGenInfo);
	}
}
