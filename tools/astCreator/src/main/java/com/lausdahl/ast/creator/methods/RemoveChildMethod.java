package com.lausdahl.ast.creator.methods;

import java.util.List;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class RemoveChildMethod extends Method
{
	List<Field> fields;
	Environment env;

	public RemoveChildMethod(IClassDefinition c, List<Field> fields,Environment env)
	{
		super(c,env);
		this.fields = fields;
		this.env = env;
	}

	@Override
	protected void prepare()
	{

		javaDoc = "\t/**\n";
		javaDoc += "\t * Removes the {@link Node} {@code child} as a child of this {@link "+classDefinition.getName()+"} node.\n";
		javaDoc += "\t * @param child the child node to be removed from this {@link "+classDefinition.getName()+"} node\n";
		javaDoc += "\t * @throws RuntimeException if {@code child} is not a child of this {@link "+classDefinition.getName()+"} node\n";
		javaDoc += "\t */";

		this.name = "removeChild";
		this.arguments.add(new Argument(env.node.getName(), "child"));

		StringBuilder sb = new StringBuilder();

		for (Field field : fields)
		{
			if (field.isTokenField || field.isAspect)
			{
				continue;
			}
			if (!field.isList)
			{
				sb.append("\t\tif (this." + field.getName() + " == child) {\n");
				sb.append("\t\t\tthis." + field.getName() + " = null;\n");
				sb.append("\t\t\treturn;\n");
				sb.append("\t\t}\n\n");
			} else
			{
				sb.append("\t\tif (this." + field.getName()
						+ ".remove(child)) {\n");
				sb.append("\t\t\t	return;\n");
				sb.append("\t\t}\n");
			}
		}
		sb.append("\t\tthrow new RuntimeException(\"Not a child.\");");

		this.body = sb.toString();
	}
	
	@Override
	protected void prepareVdm()
	{
		javaDoc = "\t/**\n";
		javaDoc += "\t * Removes the {@link Node} {@code child} as a child of this {@link "+classDefinition.getName()+"} node.\n";
		javaDoc += "\t * @param child the child node to be removed from this {@link "+classDefinition.getName()+"} node\n";
		javaDoc += "\t * @throws RuntimeException if {@code child} is not a child of this {@link "+classDefinition.getName()+"} node\n";
		javaDoc += "\t */";

		this.name = "removeChild";
		this.arguments.add(new Argument("Node", "child"));

		StringBuilder sb = new StringBuilder();

		for (Field field : fields)
		{
			if (field.isTokenField || field.isAspect)
			{
				continue;
			}
			if (!field.isList)
			{
				sb.append("\t\tif this." + field.getName() + " = child then (\n");
				sb.append("\t\t\tthis." + field.getName() + " := null;\n");
				sb.append("\t\t\treturn;\n");
				sb.append("\t\t);\n\n");
			} else
			{
				sb.append("\t\tif this." + field.getName()
						+ ".remove(child) then (\n");
				sb.append("\t\t\t	return;\n");
				sb.append("\t\t);\n");
			}
		}
		sb.append("\t\texit new RuntimeException(\"Not a child.\");");

		this.body = sb.toString();
	}
}
