package com.lausdahl.ast.creator.methods.visitors.adaptor.analysis;

import java.util.HashSet;
import java.util.Set;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.Method;

public class MergeReturnMethod extends Method
{
	String mergeTypeName = "Object";

	public MergeReturnMethod(IClassDefinition c, Environment env)
	{
		super(c, env);
	}

	public MergeReturnMethod(String mergeType)
	{
		super(null, null);
		this.mergeTypeName = mergeType;
	}

	@Override
	protected void prepare()
	{
		this.arguments.add(new Argument(mergeTypeName, "original"));
		this.arguments.add(new Argument(mergeTypeName, "new_"));
		this.name = "mergeReturns";
		this.returnType = mergeTypeName;
		this.isAbstract = true;
	}

	@Override
	public Set<String> getRequiredImports()
	{
		return new HashSet<String>();
	}

	@Override
	public Set<String> getRequiredImportsSignature()
	{
		return new HashSet<String>();
	}

}
