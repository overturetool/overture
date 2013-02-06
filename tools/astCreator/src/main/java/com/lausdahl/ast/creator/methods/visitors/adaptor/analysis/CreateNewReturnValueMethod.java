package com.lausdahl.ast.creator.methods.visitors.adaptor.analysis;

import java.util.HashSet;
import java.util.Set;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.PredefinedClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.Method;

public class CreateNewReturnValueMethod extends Method
{
	String varName = "retVal";
	private Argument arg = new Argument("Q", "question");

	public CreateNewReturnValueMethod(IClassDefinition c)
	{
		super(c);
	}

	public CreateNewReturnValueMethod(IClassDefinition c, String returnType,
			boolean hasArgument)
	{
		super(c);
		this.returnType = returnType;
		if (!hasArgument)
		{
			arg = null;
		}
	}

	@Override
	protected void prepare(Environment env)
	{
		this.arguments.add(new Argument(classDefinition.getName().getName(), "node"));
		if (arg != null)
		{
			this.arguments.add(arg);
		}
		this.name = "createNewReturnValue";
		this.isAbstract = true;
	}

	@Override
	public Set<String> getRequiredImports(Environment env)
	{
		return getRequiredImportsSignature(env);
	}

	@Override
	public Set<String> getRequiredImportsSignature(Environment env)
	{
		Set<String> imports = new HashSet<String>();
		if (!(classDefinition instanceof PredefinedClassDefinition))
		{
			imports.add(classDefinition.getName().getCanonicalName());
		}
		return imports;
	}

}
