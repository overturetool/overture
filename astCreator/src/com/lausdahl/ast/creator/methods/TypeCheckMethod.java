package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.IClassDefinition;

public class TypeCheckMethod extends Method
{
	public TypeCheckMethod(Environment env)
	{
		super(null,env);
	}

	public TypeCheckMethod(IClassDefinition c,Environment env)
	{
		super(c,env);
	}

	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;

		javaDoc = "\t/**\n";
		javaDoc += "\t * Delegates the typeCheck to the tc argument\n";
		javaDoc += "\t * @param tc the type check implementation\n";
		javaDoc += "\t * @param env the Environment\n";
		javaDoc += "\t * @param scope the scope\n";
		javaDoc += "\t * @param qualifiers the ?\n";
		javaDoc += "\t */";

		this.name = "typeCheck";// + ClassDefinition.javaClassName(f.getName());
		this.returnType = "<Typ extends Node> Typ";
		this.annotation = "@Override";
		this.arguments.add(new Argument("TypeChecker", "tc"));
		this.arguments.add(new Argument("Environment", "env"));
		this.arguments.add(new Argument("NameScope", "scope"));
		this.arguments.add(new Argument("TypeList ", "qualifiers"));
		this.requiredImports.add("com.lausdahl.runtime.Environment");
		this.requiredImports.add("com.lausdahl.runtime.NameScope");
		this.requiredImports.add("com.lausdahl.runtime.TypeList");

		/**
		 * Sets the {@code left} child of this {@link ABinopExp} node.
		 * 
		 * @param value
		 *            the new {@code left} child of this {@link ABinopExp} node
		 */
		StringBuilder sbDoc = new StringBuilder();
		sbDoc.append("\t");
		sbDoc.append("/**\n");
		sbDoc.append("\t");
		// sbDoc.append("* Sets the {@code "+f.getName()+"} child of this {@link "+c.getName()+"} node.\n");
		// sbDoc.append("\t* @param value the new {@code "+f.getName()+"} child of this {@link "+c.getName()+"} node\n");

		StringBuilder sb = new StringBuilder();

		String checkerName = "Node";
		if (c.hasSuper())
		{
			checkerName = c.getSuperName();
		}
		sb.append("\t\treturn tc.get" + checkerName + "().");
		// sb.append("caseTypeCheck(");
		sb.append("case" + c.getName() + "(");
		// sb.append(c.getName());
		sb.append("this, env, scope, qualifiers);");

		sbDoc.append("\t*/");
		// this.javaDoc=sbDoc.toString();
		this.body = sb.toString();
	}

	@Override
	protected void prepareVdm()
	{
		skip = true;
	}
}
