package com.lausdahl.ast.creator.methods.visitors.copy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.ExternalJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.definitions.JavaTypes;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.ConvertMethod;
import com.lausdahl.ast.creator.methods.GetMethod;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.utils.NameUtil;

public class CopyNode2ExtendedNode extends Method
{
	private static final String nl = "\n\t\t\t\t\t\t\t";
	IClassDefinition destination;
	// Environment env;
	Environment envDest;

	IClassDefinition factory;
	static List<String> convertArgTypes = new Vector<String>();

	public CopyNode2ExtendedNode()
	{
		super(null, null);

	}

	public CopyNode2ExtendedNode(IClassDefinition source,
			IClassDefinition destination, Environment env, Environment envDest,
			IClassDefinition factory)
	{
		super(source, env);
		this.destination = destination;
		// this.env = env;
		this.envDest = envDest;
		this.factory = factory;
	}

	/**
	 * @throws {@link Exception}
	 * @exception
	 */
	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;
		boolean throwsError = false;
		boolean notUsed = false;

		this.name = "case"
				+ NameUtil.getClassName(c.getName().getName());
		this.arguments.add(new Argument(c.getName().getCanonicalName(), "node"));
		// this.annotation = "@SuppressWarnings(\"unchecked\")";
		this.returnType = envDest.iNode.getName().getCanonicalName();
		StringBuilder bodySb = new StringBuilder();
		if (env.isTreeNode(c)
				&& env.classToType.get(c) == ClassType.SubProduction)
		{
			notUsed = true;
			this.body = "\t\treturn null;//This will never be hit because all subclasses of this has a case implementation.";
		} else if (env.isTreeNode(c)
				&& !(c instanceof ExternalJavaClassDefinition)
				&& env.classToType.get(c) == ClassType.Token)
		{
			this.body = "\t\treturn new "
					+ destination.getName().getCanonicalName()
					+ "(node.getText());";
		} else if (c instanceof ExternalJavaClassDefinition)
		{
			if (((ExternalJavaClassDefinition) c).extendsNode)
			{
				// String cast = "";
				// cast = "(" + destination.getImportName() + ")";
				// cast+= "Factory.convert(node)";
				// this.body = "\t\treturn "+cast+";";
				addToConvertFactory(c, destination);
				this.body = "\t\treturn _factory.convert(node);";
			} else
			{
				this.body = "\t\tthrow new Error(\"Use Factory.convert instead.\");";
				throwsError = true;
			}
		} else
		{

			bodySb.append("\t\treturn new "
					+ destination.getName().getCanonicalName() + "(");

			List<Field> fields = new Vector<Field>();
			fields.addAll(c.getInheritedFields());
			fields.addAll(c.getFields());

			List<Field> destFields = new Vector<Field>();

			destFields.addAll(destination.getInheritedFields());

			destFields.addAll(destination.getFields());

			Iterator<Field> sourceFields = fields.iterator();
			Iterator<Field> destinationFields = destFields.iterator();

			Field to = null;
			Field from = null;
			while (destinationFields.hasNext())
			{
				to = destinationFields.next();
				if (from == null && sourceFields.hasNext())
				{
					from = sourceFields.next();
				}

				if (from != null && to.getName().equals(from.getName()))
				{
					doInset(bodySb, to, from);
					if (sourceFields.hasNext())
					{
						from = sourceFields.next();
					}
				} else
				{
					doInset(bodySb, to, null);
				}
			}

			if (bodySb.toString().endsWith("," + nl))
			{
				bodySb.delete(bodySb.length() - (1 + nl.length()), bodySb.length());
			}
			bodySb.append(");");

			this.body = bodySb.toString();
		}

		StringBuilder sb = new StringBuilder();
		if (!notUsed)
		{
			sb.append("\t/**\n");
			sb.append("\t* Called by the {@link " + c.getName().getName()
					+ "} node from {@link " + c.getName().getName()
					+ "#apply(Switch)}.\n");
			sb.append("\t* @param node the calling {@link "
					+ c.getName()
					+ "} node\n"
					+ (throwsError ? "\t* @throws an {@link Error} is thrown because the argument cannot be converted into {@link "
							+ envDest.iNode.getName().getName()
							+ "} the _factory.convert method should be used instead.\n"
							: ""));
			sb.append("\t*/");
		} else
		{
			sb.append("\t/**\n");
			sb.append("\t* Called by the {@link "
					+ c.getName().getName()
					+ "} node from {@link "
					+ c.getName().getName()
					+ "#apply(Switch)}.\n"
					+ "\t * <i>This method will never be callable because all subclasses here of has a overloaded method implemented.</i>\n");
			sb.append("\t* @param node the calling {@link " + c.getName()
					+ "} node\n");
			sb.append("\t*/");
		}
		this.javaDoc = sb.toString();
	}

	private void doInset(StringBuilder sb, Field to, Field from)
	{
		if (from == null)
		{
			sb.append("null," + nl);
			return;
		}
		if (classDefinition.refinesField(from.getName())
				&& !classDefinition.isRefinedField(from))
		{
			return;
		}

		Method getMethod = new GetMethod(classDefinition, from, env);
		getMethod.getJavaSourceCode();
		String getMethodName = getMethod.name;
		String getter = "node." + getMethodName + "()";
		sb.append("(" + getter + " == null ? null : ");
		if (from.type instanceof ExternalJavaClassDefinition)
		{
			String cast = "";
			if (to.isTokenField)
			{
				cast = "(" + to.type.getName().getCanonicalName() + ")";
			}
			if (to.isDoubleList)
			{
				cast = "("
						+ Environment.collectionDef.getName().getCanonicalName()
						+ "<? extends "
						+ Environment.listDef.getName().getCanonicalName()
						+ "<" + to.type.getName().getCanonicalName() + ">>)";
			} else if (to.isList)
			{
				cast = "(" + Environment.listDef.getName().getCanonicalName()
						+ "<? extends " + to.type.getName().getCanonicalName()
						+ ">)";
			}

			if (((ExternalJavaClassDefinition) from.type).extendsNode)
			{
				if (to.isDoubleList)
				{
					sb.append(cast + "copyListList(" + getter + ")");
				} else if (to.isList)
				{
					sb.append(cast + "copyList(" + getter + ")");

				} else
				{
					sb.append(cast + "checkCache(" + getter + "," + getter
							+ ".apply(this))");
				}
			} else if (JavaTypes.isPrimitiveType(to.getType()))
			{
				sb.append(getter);
			} else
			{
				sb.append(cast + "_factory.convert(" + getter + ")");
				addToConvertFactory(from, to);
			}

		} else if (!from.isTokenField)
		{
			if (!from.isList)
			{
				String cast = "(" + to.type.getName().getCanonicalName() + ")";
				sb.append(cast + "checkCache(" + getter + "," + getter
						+ ".apply(this))");
			} else if (from.isDoubleList)
			{
				String cast = "("
						+ Environment.collectionDef.getName().getCanonicalName()
						+ "<? extends "
						+ Environment.listDef.getName().getCanonicalName()
						+ "<" + to.type.getName().getCanonicalName() + ">>)";
				sb.append(cast + "copyListList(" + getter + ")");
			} else
			{
				sb.append("copyList(" + getter + ")");
			}
		} else
		{
			sb.append("checkCache(" + getter + "," + getter + ")");
		}
		sb.append(")," + nl);
	}

	private void addToConvertFactory(Field from, Field to)
	{
		if (!convertArgTypes.contains(from.type.getName().getCanonicalName()))
		{
			factory.addMethod(new ConvertMethod(factory, envDest, from, to));
			convertArgTypes.add(from.type.getName().getCanonicalName());
		}
	}

	private void addToConvertFactory(IClassDefinition from, IClassDefinition to)
	{
		if (!convertArgTypes.contains(from.getName().getCanonicalName()))
		{
			factory.addMethod(new ConvertMethod(factory, envDest, from, to));
			convertArgTypes.add(from.getName().getCanonicalName());
		}
	}

	@Override
	public Set<String> getRequiredImports()
	{
		Set<String> imports = new HashSet<String>();
		// imports.add("org.overturetool.util" + ".Factory");
		imports.add(factory.getName().getCanonicalName());
		return imports;
	}

}
