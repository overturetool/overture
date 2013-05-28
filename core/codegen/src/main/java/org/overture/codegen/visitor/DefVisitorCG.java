package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.codegen.cgast.AClassTypeDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.PDeclCG;

public class DefVisitorCG extends QuestionAdaptor<CodeGenInfo>
{
	private static final long serialVersionUID = 81602965450922571L;
	
	public DefVisitorCG()
	{
	}
	
	@Override
	public void caseAClassClassDefinition(AClassClassDefinition node, CodeGenInfo question) throws AnalysisException
	{
		String name = node.getName().getName();
		String access = node.getAccess().getAccess().toString();
		boolean isAbstract = node.getIsAbstract();
		
		
		AClassTypeDeclCG classCg = new AClassTypeDeclCG();
		classCg.setName(name);
		classCg.setAccess(access);
		classCg.setAbstract(isAbstract);
		
		question.getRootVisitor().registerClass(classCg);
		
		LinkedList<PDefinition> defs = node.getDefinitions();
		
		LinkedList<AFieldDeclCG> fields = classCg.getFields();
		LinkedList<AMethodDeclCG> methods = classCg.getMethods();
		
		for (PDefinition def : defs)
		{
			PDeclCG decl = def.apply(question.getDeclVisitor(), question);
		
			if(decl == null)
				continue;//Unspported stuff returns null by default
			
			if(decl instanceof AFieldDeclCG)
				fields.add((AFieldDeclCG) decl);
			else if(decl instanceof AMethodDeclCG)
				methods.add((AMethodDeclCG) decl);
			else
				System.out.println("Unexpected def in ClassClassDefinition: " + decl.getClass().getSimpleName() + ", " + decl.toString());
			//TODO:Remove prints
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
		
}
