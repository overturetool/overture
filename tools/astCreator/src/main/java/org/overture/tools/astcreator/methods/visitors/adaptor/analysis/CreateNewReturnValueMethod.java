package org.overture.tools.astcreator.methods.visitors.adaptor.analysis;

import java.util.HashSet;
import java.util.Set;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.definitions.PredefinedClassDefinition;
import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.Method;

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
