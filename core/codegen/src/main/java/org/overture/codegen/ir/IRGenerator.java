package org.overture.codegen.ir;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.logging.ILogger;
import org.overture.codegen.logging.Logger;

public class IRGenerator
{
	private IRInfo codeGenInfo;
	
	public IRGenerator(ILogger log)
	{
		this.codeGenInfo = new IRInfo();
		Logger.setLog(log);
	}
			
	public ClassDeclStatus generateFrom(SClassDefinition classDef) throws AnalysisException
	{
		codeGenInfo.clearNodes();
		
		AClassDeclCG classCg = classDef.apply(codeGenInfo.getClassVisitor(), codeGenInfo);
		Set<NodeInfo> unsupportedNodes = copyGetUnsupportedNodes();
		
		return new ClassDeclStatus(classDef.getName().getName(), classCg, unsupportedNodes);
	}
	
	public ExpStatus generateFrom(PExp exp) throws AnalysisException
	{
		codeGenInfo.clearNodes();
		
		PExpCG expCg = exp.apply(codeGenInfo.getExpVisitor(), codeGenInfo);
		Set<NodeInfo> unsupportedNodes = copyGetUnsupportedNodes();
		
		return new ExpStatus(expCg, unsupportedNodes);
	}
	
	private Set<NodeInfo> copyGetUnsupportedNodes()
	{
		return new HashSet<NodeInfo>(codeGenInfo.getUnsupportedNodes());
	}
	
	public AInterfaceDeclCG getQuotes()
	{
		return codeGenInfo.getQuotes();
	}
	
	public IRInfo getIRInfo()
	{
		return codeGenInfo;
	}
}
