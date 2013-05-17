package org.overture.codegen.assistant;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.statements.PStm;
import org.overture.codegen.nodes.ClassCG;
import org.overture.codegen.nodes.MethodDeinitionCG;
import org.overture.codegen.visitor.CodeGenContextMap;
import org.overture.codegen.visitor.CodeGenVisitor;

public class StmAssistantCG
{
	private CodeGenVisitor rootVisitor;
	
	public StmAssistantCG(CodeGenVisitor rootVisitor)
	{
		this.rootVisitor = rootVisitor;
	}
	
	public MethodDeinitionCG getMethodDefinition(PStm node, CodeGenContextMap question) throws AnalysisException
	{
		String className = node.getAncestor(SClassDefinition.class).getName().apply(rootVisitor, question);		
		String methodName = node.getAncestor(AExplicitOperationDefinition.class).getName().apply(rootVisitor, question);
		
		ClassCG codeGenClass = question.getCodeGenClass(className);
		
		return codeGenClass.getMethodDefinition(methodName);
	}
}
