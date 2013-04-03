package org.overture.tools.astcreator.methods.visitors.adaptor.analysis;

import java.util.HashSet;
import java.util.Set;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.Method;

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
