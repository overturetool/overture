package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.definitions.ExternalJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.utils.NameUtil;

public class SetMethod extends Method {
	Field f;

	public SetMethod(IClassDefinition c, Field f, Environment env) {
		super(c, env);
		this.f = f;
	}

	@Override
	protected void prepare() {
		IClassDefinition c = classDefinition;
		this.name = "set"
				+ NameUtil.javaClassName(f.getName());
		this.arguments.add(new Argument(f.getMethodArgumentType(), "value"));

		javaDoc = "\t/**\n";
		javaDoc += "\t * Sets the {@code " + f.getName()
				+ "} child of this {@link " + c.getName().getName() + "} node.\n";
		javaDoc += "\t * @param value the new {@code " + f.getName()
				+ "} child of this {@link " + c.getName().getName() + "} node\n";
		javaDoc += "\t*/";

		StringBuilder sb = new StringBuilder();

		if ((!f.isTokenField && !(env.classToType.get(c)==ClassType.Custom)) || (f.type instanceof ExternalJavaClassDefinition && ((ExternalJavaClassDefinition)f.type).extendsNode)) {
			if (f.isList) {
				sb.append("\t\tif (this." + f.getName() + ".equals(value)) {\n");
				sb.append("\t\t\treturn;\n");
				sb.append("\t\t}\n");
				sb.append("\t\tthis." + f.getName() + ".clear();\n");
				sb.append("\t\tif (value != null) {\n");
				sb.append("\t\t\tthis." + f.getName() + ".addAll(value);\n");
				sb.append("\t\t}\n");

			}else
			{
				switch(f.structureType)
				{
					case Graph:
						sb.append("\t\tif( value != null && value.parent() == null) {\n");
						sb.append("\t\t\tvalue.parent(this);\n");
						sb.append("\t\t}\n");
						break;
					case Tree:
						if(!f.isAspect)
						{
							sb.append("\t\tif (this." + f.getName() + " != null) {\n");
							sb.append("\t\t\tthis." + f.getName() + ".parent(null);\n");
							sb.append("\t\t}\n");
							sb.append("\t\tif (value != null) {\n");
							sb.append("\t\t\tif (value.parent() != null) {\n");
							sb.append("\t\t\t\tvalue.parent().removeChild(value);\n");
							sb.append("\t\t}\n");
							sb.append("\t\t\tvalue.parent(this);\n");
							sb.append("\t\t}\n");
						}
						break;
					
				}
								
				sb.append("\t\tthis." + f.getName() + " = value;\n");
			}
		}else if (f.isTokenField || f.isAspect) {
			sb.append("\t\tthis." + f.getName() + " = value;");
		}

		this.body = sb.toString();
	}

	@Override
	protected void prepareVdm() {
		IClassDefinition c = classDefinition;
		this.name = "set"
				+ NameUtil.javaClassName(f.getName());
		this.arguments.add(new Argument(f.getMethodArgumentType(), "value"));

		javaDoc = "\t/**\n";
		javaDoc += "\t * Sets the {@code " + f.getName()
				+ "} child of this {@link " + c.getName() + "} node.\n";
		javaDoc += "\t * @param value the new {@code " + f.getName()
				+ "} child of this {@link " + c.getName() + "} node\n";
		javaDoc += "\t*/";

		StringBuilder sb = new StringBuilder();

		if (!f.isTokenField && !f.isAspect
				&& !(env.classToType.get(c)==ClassType.Custom)) {
			if (!f.isList) {
				sb.append("\t\tif this." + f.getName() + " <> null then(\n");
				sb.append("\t\t\tthis." + f.getName() + ".parent(null);\n");
				sb.append("\t\t);\n");
				sb.append("\t\tif value <> null then (\n");
				sb.append("\t\t\tif value.parent() <> null then (\n");
				sb.append("\t\t\t\tvalue.parent().removeChild(value);\n");
				sb.append("\t\t);\n");
				sb.append("\t\t\tvalue.parent(this);\n");
				sb.append("\t\t);\n");
				sb.append("\t\tthis." + f.getName() + " := value;\n");
			} else {

				sb.append("\t\tif value = this." + f.getName() + " then (\n");
				sb.append("\t\t\treturn;\n");
				sb.append("\t\t);\n");
				sb.append("\t\tthis." + f.getName() + ".clear();\n");
				sb.append("\t\tthis." + f.getName() + ".addAll(value);\n");

			}
		}

		else if (f.isTokenField || f.isAspect) {
			sb.append("\t\tthis." + f.getName() + " := value;");
		}

		this.body = sb.toString();
	}
}
