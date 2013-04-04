package org.overture.tools.astcreator.methods;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;

public class SuperConstructorMethod extends Method
{
	public List<Argument> argumentsToSuper = new Vector<Method.Argument>();

	public SuperConstructorMethod(IClassDefinition c,
			Argument... arg)
	{
		super(c);
		if (arg != null)
		{
			for (Argument argument : arg)
			{
				this.argumentsToSuper.add(argument);
			}
		}
	}

	@Override
	protected void prepare(Environment env)
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
