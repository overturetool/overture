package org.overture.tools.astcreator.methods;

import java.util.List;
import java.util.Vector;

import org.overture.tools.astcreator.definitions.ExternalJavaClassDefinition;
import org.overture.tools.astcreator.definitions.Field;
import org.overture.tools.astcreator.definitions.Field.StructureType;
import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.definitions.JavaTypes;
import org.overture.tools.astcreator.env.Environment;

public class RemoveChildMethod extends Method
{
	List<Field> fields = new Vector<Field>();

	public RemoveChildMethod(IClassDefinition c)
	{
		super(c);
	}

	@Override
	protected void prepare(Environment env)
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
				if (!field.isList)
				{
					// We need to ignore this since the parent might have been set to this node as a lack of a better
					// parent
					sb.append("\t\tif (this." + field.getName(env)
							+ " == child) {\n");
					sb.append("\t\t\treturn;\n");
					sb.append("\t\t}\n\n");
					continue;
				} else
				{
					sb.append("\t\tif (this." + field.getName(env)
							+ ".contains(child)) {\n");
					sb.append("\t\t\treturn;\n");
					sb.append("\t\t}\n\n");
					continue;
				}
			}

			if ((field.isTokenField && !(field.type instanceof ExternalJavaClassDefinition && ((ExternalJavaClassDefinition) field.type).extendsNode)))
			{
				continue;
			}
			if (!field.isList)
			{
				sb.append("\t\tif (this." + field.getName(env) + " == child) {\n");
				sb.append("\t\t\tthis." + field.getName(env) + " = null;\n");
				sb.append("\t\t\treturn;\n");
				sb.append("\t\t}\n\n");
			} else
			{
				sb.append("\t\tif (this." + field.getName(env)
						+ ".remove(child)) {\n");
				sb.append("\t\t\t	return;\n");
				sb.append("\t\t}\n");
			}
		}
		sb.append("\t\tthrow new RuntimeException(\"Not a child.\");");

		this.body = sb.toString();
	}

	@Override
	protected void prepareVdm(Environment env)
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
				sb.append("\t\tif this." + field.getName(env)
						+ " = child then (\n");
				sb.append("\t\t\tthis." + field.getName(env) + " := null;\n");
				sb.append("\t\t\treturn;\n");
				sb.append("\t\t);\n\n");
			} else
			{
				sb.append("\t\tif this." + field.getName(env)
						+ ".remove(child) then (\n");
				sb.append("\t\t\t	return;\n");
				sb.append("\t\t);\n");
			}
		}
		sb.append("\t\texit new RuntimeException(\"Not a child.\");");

		this.body = sb.toString();
	}
}
