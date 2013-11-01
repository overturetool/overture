package org.overture.codegen.assistant;

import java.util.LinkedList;

import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.OoAstInfo;

public class DeclAssistantCG
{

	public DeclAssistantCG()
	{	
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
		for(int i = 0; i < OoAstInfo.RESERVED_CLASS_NAMES.length; i++)
			if(OoAstInfo.RESERVED_CLASS_NAMES[i].equals(className))
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
