package com.lausdahl.ast.creator.methods;

import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.ExternalJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.Field.StructureType;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.JavaTypes;
import com.lausdahl.ast.creator.env.Environment;

public class RemoveChildMethod extends Method
{
	List<Field> fields = new Vector<Field>();

	public RemoveChildMethod(IClassDefinition c, Environment env)
	{
		super(c, env);
		this.env = env;
	}

	@Override
	protected void prepare()
	{
		fields.clear();
		fields.addAll(classDefinition.getInheritedFields());
		fields.addAll(classDefinition.getFields());

		javaDoc = "\t/**\n";
		javaDoc += "\t * Removes the {@link " + env.iNode.getName().getName()
				+ "} {@code child} as a child of this {@link "
				+ classDefinition.getName().getName() + "} node.\n";
		javaDoc += "\t * Do not call this method with any graph fields of this node. This will cause any child's\n\t * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.\n";
		javaDoc += "\t * @param child the child node to be removed from this {@link "
				+ classDefinition.getName().getName() + "} node\n";
		javaDoc += "\t * @throws RuntimeException if {@code child} is not a child of this {@link "
				+ classDefinition.getName().getName() + "} node\n";
		javaDoc += "\t */";

		this.name = "removeChild";
		this.arguments.add(new Argument(env.iNode.getName().getName(), "child"));

		StringBuilder sb = new StringBuilder();

		for (Field field : fields)
		{
			if (JavaTypes.isPrimitiveType(field.type.getName().getName()))
			{
				continue;
			}
			if (field.structureType == StructureType.Graph)
			{
				// We need to ignore this since the parent might have been set to this node as a lack of a better parent
				sb.append("\t\tif (this." + field.getName() + " == child) {\n");
				sb.append("\t\t\treturn;\n");
				sb.append("\t\t}\n\n");
				continue;
			}

			if ((field.isTokenField && !(field.type instanceof ExternalJavaClassDefinition && ((ExternalJavaClassDefinition) field.type).extendsNode)))
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
		fields.clear();
		fields.addAll(classDefinition.getInheritedFields());
		fields.addAll(classDefinition.getFields());

		javaDoc = "\t/**\n";
		javaDoc += "\t * Removes the {@link Node} {@code child} as a child of this {@link "
				+ classDefinition.getName() + "} node.\n";
		javaDoc += "\t * @param child the child node to be removed from this {@link "
				+ classDefinition.getName() + "} node\n";
		javaDoc += "\t * @throws RuntimeException if {@code child} is not a child of this {@link "
				+ classDefinition.getName() + "} node\n";
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
				sb.append("\t\tif this." + field.getName()
						+ " = child then (\n");
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
