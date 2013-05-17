package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAdaptor;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.codegen.cgast.AClassCG;
import org.overture.codegen.cgast.AFieldCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.naming.TemplateParameters;
import org.overture.codegen.newstuff.ContextManager;
import org.overture.codegen.nodes.FieldCG;

public class DefVisitorCG extends AnalysisAdaptor
{
	private static final long serialVersionUID = 81602965450922571L;
	
	private CodeGenVisitor rootVisitor;
	
	public DefVisitorCG(CodeGenVisitor rootVisitor)
	{
		this.rootVisitor = rootVisitor;
	}
	
	@Override
	public void caseAClassClassDefinition(AClassClassDefinition node) throws AnalysisException
	{
		String name = node.getName().getName();
		String access = node.getAccess().getAccess().toString();
		
		AClassCG classCg = new AClassCG();
		classCg.setName(name);
		classCg.setAccess(access);
		
		rootVisitor.getTree().registerClass(classCg);
		
		LinkedList<PDefinition> defs = node.getDefinitions();
		
		for (PDefinition def : defs)
		{
			def.apply(this);
		}
	}
	
	@Override
	public void caseAValueDefinition(AValueDefinition node) throws AnalysisException
	{
		String access = node.getAccess().getAccess().toString();
		String name = node.getPattern().toString();
		boolean isStatic = true;
		boolean isFinal = true;
		String type = node.getType().apply(rootVisitor.getTypeVisitor(), null);
		String exp = "123";//node.getExpression().apply(rootVisitor.getExpVisitor(), question);
		
		AFieldCG field = new AFieldCG();//new AFieldCG(access_, name_, static_, final_, type_, initial_)
		field.setAccess(access);
		field.setName(name);
		field.setStatic(isStatic);
		field.setFinal(isFinal);
		field.setType(type);
		field.setInitial(exp);

		String className = node.getClassDefinition().getName().getName();
		AClassCG classCg = rootVisitor.getTree().getClass(className);
		classCg.getFields().add(field);
	}
		
//		String className = node.getName().apply(rootVisitor, question);
//		String accessSpecifier = node.getAccess().apply(rootVisitor, question);
//
//		ClassCG classCg = new ClassCG(className, accessSpecifier);
//		
//		question.registerCodeGenClass(classCg);
//		
//		LinkedList<PDefinition> definitions = node.getDefinitions();
//
//		for (PDefinition def : definitions)
//			def.apply(rootVisitor.getDefVisitor(), question);
//		
//		return null;
	
//	@Override
//	public INode caseAExplicitOperationDefinition(
//			AExplicitOperationDefinition node, CodeGenContextMap question)
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
//		
//		return null;
//	}
		
}
