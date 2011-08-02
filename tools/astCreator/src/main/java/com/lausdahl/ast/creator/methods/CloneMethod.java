package com.lausdahl.ast.creator.methods;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.ExternalEnumJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.Field.StructureType;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.definitions.JavaTypes;

public class CloneMethod extends Method
{
	ClassType classType;

	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;
		this.name = "clone";

		this.returnType = c.getName();
//		this.requiredImports.add("java.util.Map");

		StringBuilder sbDoc = new StringBuilder();
		sbDoc.append("\t/**\n");
		sbDoc.append("\t * Returns a deep clone of this {@link " + c.getName()
				+ "} node.\n");
		sbDoc.append("\t * @return a deep clone of this {@link " + c.getName()
				+ "} node\n");
		sbDoc.append("\t */");
		StringBuilder sb = new StringBuilder();

		List<Field> fields = new Vector<Field>();

		for (Field field : classDefinition.getInheritedFields())
		{
			if (!classDefinition.refinesField(field.getName()))
			{
				fields.add(field);
			}
		}

		fields.addAll(c.getFields());

		switch (classType)
		{
			case Production:
			case SubProduction:
				this.annotation ="@Override";
				this.isAbstract = true;
				break;
			case Alternative:
			case Custom:

			case Unknown:
				sb.append("\t\treturn new " + c.getName() + "(\n");

				if (!fields.isEmpty())
				{
					String tmp = "";
					for (Field f : fields)
					{
						String name = f.getName();

						if (classDefinition.isRefinedField(f))
						{
							name = f.getCast() + name;
						}

						if(f.structureType==StructureType.Graph)
						{
							tmp += ("\t\t\t" + name + ",\n");
						}else if (f.isList && !f.isDoubleList)
						{
							tmp += ("\t\t\tcloneList"
									+ (f.isTypeExternalNotNode() ? "External"
											: "") + "(" + name + "),\n");
						} else if (f.isDoubleList)
						{
							tmp += ("\t\t\tcloneListList(" + name + "),\n");
						} else
						{
							if (JavaTypes.isPrimitiveType(f.getType())
									|| f.type instanceof ExternalEnumJavaClassDefinition)
							{
								tmp += ("\t\t\t" + name + ",\n");
							} else
							{
								tmp += ("\t\t\tcloneNode(" + name + "),\n");
							}
						}
					}
					sb.append(tmp.substring(0, tmp.length() - 2) + "\n");
				}

				sb.append("\t\t);");
				break;

			case Token:
				sb.append("\t\treturn new " + c.getName() + "( ");

				if (!fields.isEmpty())
				{
					String tmp = "";
					for (Field f : fields)
					{
						String name = f.getName();

						if (classDefinition.isRefinedField(f))
						{
							name = f.getCast() + name;
						}

						tmp += ("get"
								+ CommonTreeClassDefinition.javaClassName(name) + "(), ");
					}
					sb.append(tmp.substring(0, tmp.length() - 2));
				}

				sb.append(");");

				break;

		}

		this.javaDoc = sbDoc.toString();
		this.body = sb.toString();
	}

	public CloneMethod(IClassDefinition c, ClassType classType, Environment env)
	{
		super(c, env);
		this.classType = classType;
	}

	@Override
	public Set<String> getRequiredImports()
	{
		Set<String> imports = new HashSet<String>();
		imports.addAll(super.getRequiredImports());
		for (Field f : classDefinition.getInheritedFields())
		{
			if (classDefinition.isRefinedField(f))
			{
				imports.addAll(f.getRequiredImports());
			}
		}
		return imports;
	}

	@Override
	protected void prepareVdm()
	{
		skip = true;
	}
}
