package org.overture.codegen.nodes;

import java.util.ArrayList;

public class MethodDeinitionCG implements TemplateCollectionElement
{
	private String accessSpecifier;
	private String returnType;
	private String methodName;
	
	private ArrayList<IStatementCG> statements;
	
	public MethodDeinitionCG(String accessSpecifier, String returnType, String methodName)
	{
		this.accessSpecifier = accessSpecifier;
		this.returnType = returnType;
		this.methodName = methodName;
		
		this.statements = new ArrayList<IStatementCG>();
	}

	public String getAccessSpecifier()
	{
		return accessSpecifier;
	}

	public String getReturnType()
	{
		return returnType;
	}

	public String getMethodName()
	{
		return methodName;
	}
	
	public ArrayList<IStatementCG> getStatements()
	{
		return statements;
	}
	
	public void addStatement(IStatementCG statement)
	{
		statements.add(statement);
	}
	
}
