package org.overture.codegen.assistant;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.OoAstConstants;
import org.overture.codegen.visitor.CodeGenInfo;

public class DeclAssistantCG
{

	public DeclAssistantCG()
	{	
	}
	
	public static void setLocalDefs(LinkedList<PDefinition> localDefs, LinkedList<ALocalVarDeclCG> localDecls, CodeGenInfo question) throws AnalysisException
	{
		for (PDefinition def : localDefs)
		{
			if(def instanceof AValueDefinition)
			{
				AValueDefinition valueDef = (AValueDefinition) def;
				localDecls.add(DeclAssistantCG.constructLocalVarDecl(valueDef, question));
			}
		}
	}
	
	private static ALocalVarDeclCG constructLocalVarDecl(AValueDefinition valueDef, CodeGenInfo question) throws AnalysisException
	{
		PTypeCG type = valueDef.getType().apply(question.getTypeVisitor(), question);
		String name = valueDef.getPattern().toString();
		PExpCG exp = valueDef.getExpression().apply(question.getExpVisitor(), question);
		
		ALocalVarDeclCG localVarDecl = new ALocalVarDeclCG();
		localVarDecl.setType(type);
		localVarDecl.setName(name);
		localVarDecl.setExp(exp);
		
		return localVarDecl;
		
	}
	
	public AFieldDeclCG constructField(String access, String name, boolean isStatic, boolean isFinal, PTypeCG type, PExpCG exp)
	{
		
		AFieldDeclCG field = new AFieldDeclCG();
		field.setAccess(access);
		field.setName(name);
		field.setStatic(isStatic);
		field.setFinal(isFinal);
		field.setType(type);
		field.setInitial(exp);
		
		return field;
	}
	
	public static boolean isValidName(String className)
	{
		for(int i = 0; i < OoAstConstants.RESERVED_CLASS_NAMES.length; i++)
			if(OoAstConstants.RESERVED_CLASS_NAMES[i].equals(className))
				return false;
		
		return true; 
	}
	
	public static boolean causesMethodOverloading(LinkedList<AMethodDeclCG> methods, AMethodDeclCG method)
	{
		for (AMethodDeclCG aMethodDeclCG : methods)
			if(aMethodDeclCG.getName().equals(method.getName()))
				return true;
		
		return false;
	}
	
}
