package org.overture.codegen.nodes;

import java.util.ArrayList;

import org.overture.codegen.naming.TemplateParameters;
import org.overture.codegen.visitor.CodeGenContext;

public class ClassCG implements ICommitable
{

	private String className;
	private String accessSpecifier;
	
	private ArrayList<ValueDefinitionCG> valueDefinitions;
	private ArrayList<MethodDeinitionCG> methodDefinitions;
	
	public ClassCG(String className, String accessSpecifier)
	{
		super();
		
		this.className = className;
		this.accessSpecifier = accessSpecifier;
		
		this.valueDefinitions = new ArrayList<ValueDefinitionCG>();
		this.methodDefinitions = new ArrayList<MethodDeinitionCG>();
	}
		
	public String getClassName()
	{
		return className;
	}

	public void addValueDefinition(ValueDefinitionCG valueDef)
	{
		this.valueDefinitions.add(valueDef);
	}
	
	public void addMethod(MethodDeinitionCG methodDef)
	{
		this.methodDefinitions.add(methodDef);
	}
	
	public MethodDeinitionCG getMethodDefinition(String methodName)
	{
		for (MethodDeinitionCG methodDef : methodDefinitions)
		{
			if(methodDef.getMethodName().equals(methodName))
				return methodDef;
		}
		
		return null;
	}

	@Override
	public void commit(CodeGenContext context)
	{
		context.put(TemplateParameters.CLASS_NAME, className);
		context.put(TemplateParameters.CLASS_ACCESS_SPECIFIER, accessSpecifier);
		
		context.put(TemplateParameters.VALUE_DEFS, valueDefinitions);
		context.put(TemplateParameters.METHOD_DEFS, methodDefinitions);
	}
	
}
