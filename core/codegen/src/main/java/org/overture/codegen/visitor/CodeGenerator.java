package org.overture.codegen.visitor;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.logging.ILogger;
import org.overture.codegen.logging.Logger;

public class CodeGenerator
{
	private CodeGenInfo codeGenInfo;
	
	public CodeGenerator(ILogger log)
	{
		this.codeGenInfo = new CodeGenInfo(this);
		Logger.setLog(log);
	}
			
	public AClassDeclCG generateFrom(SClassDefinition classDef) throws AnalysisException
	{
		return classDef.apply(codeGenInfo.getClassVisitor(), codeGenInfo);
	}
	
	public PExpCG generateFrom(PExp exp) throws AnalysisException
	{
		return exp.apply(codeGenInfo.getExpVisitor(), codeGenInfo);
	}
	
	public List<String> getQuotes()
	{
		return codeGenInfo.getQuoteValues();
	}
}
