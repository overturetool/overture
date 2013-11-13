package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AEmptyDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.PDeclCG;

public class ClassVisitorCG extends AbstractVisitorCG<OoAstInfo, AClassDeclCG>//QuestionAnswerAdaptor<CodeGenInfo, AClassTypeDeclCG>
{
	public ClassVisitorCG()
	{
	}
	
	@Override
	public AClassDeclCG caseAClassClassDefinition(AClassClassDefinition node, OoAstInfo question) throws AnalysisException
	{
		String name = node.getName().getName();

		if(!DeclAssistantCG.isValidName(name))
			throw new AnalysisException("Class name: " + name + " is reserved!");
		
		String access = node.getAccess().getAccess().toString();
		boolean isAbstract = node.getIsAbstract();
		boolean isStatic = false;
		LinkedList<ILexNameToken> superNames = node.getSupernames();
		if(superNames.size() > 1)
			throw new AnalysisException("Multiple inheritance not supported.");
		
		AClassDeclCG classCg = new AClassDeclCG();
		classCg.setName(name);
		classCg.setAccess(access);
		classCg.setAbstract(isAbstract);
		classCg.setStatic(isStatic);
		classCg.setStatic(false);
		if(superNames.size() == 1)
			classCg.setSuperName(superNames.get(0).getName());
		
		LinkedList<PDefinition> defs = node.getDefinitions();
		
		LinkedList<AFieldDeclCG> fields = classCg.getFields();
		LinkedList<AMethodDeclCG> methods = classCg.getMethods();
		LinkedList<AClassDeclCG> innerClasses = classCg.getInnerClasses();
		
		for (PDefinition def : defs)
		{
			PDeclCG decl = def.apply(question.getDeclVisitor(), question);
		
			if(decl == null)
				continue;//Unspported stuff returns null by default
			
			if(decl instanceof AFieldDeclCG)
				fields.add((AFieldDeclCG) decl);
			else if(decl instanceof AMethodDeclCG)
			{
				AMethodDeclCG method = (AMethodDeclCG) decl;
				if(DeclAssistantCG.causesMethodOverloading(methods, method))
					throw new AnalysisException("Operation/function name overload is not allowed. Caused by: " + name + "." + method.getName());
				methods.add(method);
			}
			else if(decl instanceof AClassDeclCG)
				innerClasses.add((AClassDeclCG) decl);
			else if(decl instanceof AEmptyDeclCG)
			;//Empty declarations are used to indicate constructs that can be ignored during the
			 //construction of the OO AST. 
			else
				System.out.println("Unexpected def in ClassClassDefinition: " + decl.getClass().getSimpleName() + ", " + decl.toString());
			//TODO:Remove prints
		}
		
		return classCg;
	}
	
}
