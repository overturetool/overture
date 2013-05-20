package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.codegen.cgast.AClassCG;
import org.overture.codegen.cgast.AFieldCG;

public class DefVisitorCG extends QuestionAdaptor<CodeGenInfo>
{
	private static final long serialVersionUID = 81602965450922571L;
	
	private CodeGenVisitor rootVisitor;
	
	public DefVisitorCG(CodeGenVisitor rootVisitor)
	{
		this.rootVisitor = rootVisitor;
	}
	
	@Override
	public void caseAClassClassDefinition(AClassClassDefinition node, CodeGenInfo question) throws AnalysisException
	{
		String name = node.getName().getName();
		String access = node.getAccess().getAccess().toString();
		
		AClassCG classCg = new AClassCG();
		classCg.setName(name);
		classCg.setAccess(access);
		
		rootVisitor.registerClass(classCg);
		
		LinkedList<PDefinition> defs = node.getDefinitions();
	
		
		LinkedList<AFieldCG> fields = classCg.getFields();
		for (PDefinition def : defs)
		{
			AFieldCG field = def.apply(question.getFieldVisitor(), question); 
			
			if(field != null)
				fields.add(field);
		}
	}
	
//	@Override
//	public void caseAValueDefinition(AValueDefinition node) throws AnalysisException
//	{
//		String access = node.getAccess().getAccess().toString();
//		String name = node.getPattern().toString();
//		boolean isStatic = true;
//		boolean isFinal = true;
//		String type = node.getType().apply(rootVisitor.getTypeVisitor(), null);
//		String exp = "123";//node.getExpression().apply(rootVisitor.getExpVisitor(), question);
//		
//		AFieldCG field = new AFieldCG();//new AFieldCG(access_, name_, static_, final_, type_, initial_)
//		field.setAccess(access);
//		field.setName(name);
//		field.setStatic(isStatic);
//		field.setFinal(isFinal);
//		field.setType(type);
//		field.setInitial(exp);
//		
//		
//		
//		
//		String className = node.getClassDefinition().getName().getName();
//		AClassCG classCg = rootVisitor.getTree().getClass(className);
//		classCg.getFields().add(field);
//	}
	
//	@Override
//	public void caseAExplicitOperationDefinition(
//			AExplicitOperationDefinition node)
//			throws AnalysisException
//	{	
//		String accessSpecifier = node.getAccess().apply(rootVisitor, question);
//		String operationName = node.getName().apply(rootVisitor, question);
//		String returnType = node.getActualResult().apply(rootVisitor.getTypeVisitor(), question);
//
//		String className = node.getClassDefinition().getName().apply(rootVisitor, question);
//		ClassCG codeGenClass = question.getCodeGenClass(className);
//		codeGenClass.addMethod(new MethodDeinitionCG(accessSpecifier, returnType, operationName));
//		
//		node.getBody().apply(rootVisitor.getStmVisitor(), question);		
//	}
		
}
