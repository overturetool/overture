package com.lausdahl.ast.creator.methods.visitors.adaptor.analysis;

import java.util.HashSet;
import java.util.Set;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.Method;

public class MergeReturnMethod extends Method
{
	String mergeTypeName = "Object";

	public MergeReturnMethod(IClassDefinition c)
	{
		super(c);
	}

	public MergeReturnMethod(String mergeType)
	{
		super(null);
		this.mergeTypeName = mergeType;
	}

	@Override
	protected void prepare(Environment env)
	{
		this.arguments.add(new Argument(mergeTypeName, "original"));
		this.arguments.add(new Argument(mergeTypeName, "new_"));
		this.name = "mergeReturns";
		this.returnType = mergeTypeName;
		this.isAbstract = true;
	}

	@Override
	public Set<String> getRequiredImports(Environment env)
	{
		return new HashSet<String>();
	}

	@Override
	public Set<String> getRequiredImportsSignature(Environment env)
	{
		return new HashSet<String>();
	}

}
