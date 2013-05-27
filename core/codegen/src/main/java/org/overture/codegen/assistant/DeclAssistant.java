package org.overture.codegen.assistant;

import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.types.PTypeCG;

public class DeclAssistant
{

	public DeclAssistant()
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
	
}
