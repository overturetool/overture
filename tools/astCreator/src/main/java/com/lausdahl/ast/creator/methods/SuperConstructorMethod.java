package com.lausdahl.ast.creator.methods;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;

public class SuperConstructorMethod extends Method
{
	public List<Argument> argumentsToSuper = new Vector<Method.Argument>();

	public SuperConstructorMethod(IClassDefinition c, Environment env,
			Argument... arg)
	{
		super(c, env);
		if (arg != null)
		{
			for (Argument argument : arg)
			{
				this.argumentsToSuper.add(argument);
			}
		}
	}

	@Override
	protected void prepare()
	{
		StringBuilder sb = new StringBuilder();
		isConstructor = true;
		name = classDefinition.getName().getName();
		arguments.addAll(argumentsToSuper);

		sb.append("\t\tsuper(");
		for (Iterator<Argument> iterator = arguments.iterator(); iterator.hasNext();)
		{
			sb.append(iterator.next().name);
			if (iterator.hasNext())
			{
				sb.append(", ");
			}

		}
		sb.append(");");
		body = sb.toString();
	}

}
