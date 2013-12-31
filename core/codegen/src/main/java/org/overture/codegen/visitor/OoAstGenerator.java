package org.overture.codegen.visitor;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.logging.ILogger;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.ClassDeclStatus;
import org.overture.codegen.utils.ExpStatus;

public class OoAstGenerator
{
	private OoAstInfo codeGenInfo;
	
	public OoAstGenerator(ILogger log)
	{
		this.codeGenInfo = new OoAstInfo(this);
		Logger.setLog(log);
	}
			
	public ClassDeclStatus generateFrom(SClassDefinition classDef) throws AnalysisException
	{
		codeGenInfo.clearNodes();
		
		AClassDeclCG classCg = classDef.apply(codeGenInfo.getClassVisitor(), codeGenInfo);
		Set<INode> unsupportedNodes = copyGetUnsupportedNodes();
		
		return new ClassDeclStatus(classCg, unsupportedNodes);
	}
	
	public ExpStatus generateFrom(PExp exp) throws AnalysisException
	{
		codeGenInfo.clearNodes();
		
		PExpCG expCg = exp.apply(codeGenInfo.getExpVisitor(), codeGenInfo);
		Set<INode> unsupportedNodes = copyGetUnsupportedNodes();
		
		return new ExpStatus(expCg, unsupportedNodes);
	}
	
	private Set<INode> copyGetUnsupportedNodes()
	{
		return new HashSet<INode>(codeGenInfo.getUnsupportedNodes());
	}
	
	public AInterfaceDeclCG getQuotes()
	{
		return codeGenInfo.getQuotes();
	}
}
