package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AEmptyDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.PDeclCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.constants.IOoAstConstants;
import org.overture.codegen.ooast.OoAstInfo;
import org.overture.codegen.utils.AnalysisExceptionCG;

public class ClassVisitorCG extends AbstractVisitorCG<OoAstInfo, AClassDeclCG>
{
	public ClassVisitorCG()
	{
	}
	
	@Override
	public AClassDeclCG caseAClassClassDefinition(AClassClassDefinition node, OoAstInfo question) throws AnalysisException
	{
		String name = node.getName().getName();
		String access = node.getAccess().getAccess().toString();
		boolean isAbstract = node.getIsAbstract();
		boolean isStatic = false;
		LinkedList<ILexNameToken> superNames = node.getSupernames();
		
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
		LinkedList<ARecordDeclCG> innerClasses = classCg.getRecords();
		
		for (PDefinition def : defs)
		{
			PDeclCG decl = def.apply(question.getDeclVisitor(), question);
		
			if(decl == null)
			{
				continue;//Unspported stuff returns null by default
			}
			if(decl instanceof AFieldDeclCG)
			{
				fields.add((AFieldDeclCG) decl);
			}
			else if(decl instanceof AMethodDeclCG)
			{
				methods.add((AMethodDeclCG) decl);
			}
			else if(decl instanceof ARecordDeclCG)
			{
				innerClasses.add((ARecordDeclCG) decl);
			}
			else if(decl instanceof AEmptyDeclCG)
			;//Empty declarations are used to indicate constructs that can be ignored during the
			 //construction of the OO AST. 
			else
			{
				throw new AnalysisExceptionCG("Unexpected definition in class: " + name + ": " + def.getName().getName(), def.getLocation());
			}
		}
		
		boolean defaultConstructorExplicit = false;
		for(AMethodDeclCG method : methods)
		{
			if(method.getIsConstructor() && method.getFormalParams().isEmpty())
			{
				defaultConstructorExplicit = true;
				break;
			}
		}
		
		if(!defaultConstructorExplicit)
		{
			AMethodDeclCG constructor = new AMethodDeclCG();

			AClassTypeCG classType = new AClassTypeCG();
			classType.setName(name);
			
			AMethodTypeCG methodType = new AMethodTypeCG();
			methodType.setResult(classType);
			
			constructor.setMethodType(methodType);
			constructor.setAccess(IOoAstConstants.PUBLIC);
			constructor.setIsConstructor(true);
			constructor.setName(name);
			constructor.setBody(new ABlockStmCG());
			
			methods.add(constructor);
		}
		
		return classCg;
	}
	
}
