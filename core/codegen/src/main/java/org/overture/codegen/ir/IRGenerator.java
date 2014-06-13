package org.overture.codegen.ir;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
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
			
	public IRClassDeclStatus generateFrom(SClassDefinition classDef) throws AnalysisException
	{
		codeGenInfo.clearNodes();
		
		AClassDeclCG classCg = classDef.apply(codeGenInfo.getClassVisitor(), codeGenInfo);
		Set<NodeInfo> unsupportedNodes = copyGetUnsupportedNodes();
		
		return new IRClassDeclStatus(classDef.getName().getName(), classCg, unsupportedNodes);
	}
	
	public IRExpStatus generateFrom(PExp exp) throws AnalysisException
	{
		codeGenInfo.clearNodes();
		
		SExpCG expCg = exp.apply(codeGenInfo.getExpVisitor(), codeGenInfo);
		Set<NodeInfo> unsupportedNodes = copyGetUnsupportedNodes();
		
		return new IRExpStatus(expCg, unsupportedNodes);
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
