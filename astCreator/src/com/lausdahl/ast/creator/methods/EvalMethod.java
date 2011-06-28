package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.IClassDefinition;

public class EvalMethod extends Method
{
	public EvalMethod(Environment env)
	{
		super(null,env);
	}

	public EvalMethod(IClassDefinition c,Environment env)
	{
		super(c,env);
	}

	@Override
	protected void prepare()
	{
		javaDoc = "\t/**\n";
		javaDoc += "\t * Delegates the evaluation to the evaluator argument\n";
		javaDoc += "\t * @param evaluator the evaluation implementation\n";
		javaDoc += "\t * @param ctxt the evaluation context\n";
		javaDoc += "\t */";
		
		IClassDefinition c = classDefinition;
		this.name = "eval";// + ClassDefinition.javaClassName(f.getName());
		this.returnType = "IValue";
		this.annotation = "@Override";
		this.arguments.add(new Argument("Eval", "evaluator"));
		this.requiredImports.add("com.lausdahl.runtime.Context");
		this.requiredImports.add("com.lausdahl.ast.values.IValue");
		this.arguments.add(new Argument("Context", "ctxt"));

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
		sb.append("\t\treturn evaluator.get" + checkerName + "().");
		// sb.append("caseTypeCheck(");
		sb.append("case" + c.getName() + "(");
		// sb.append(c.getName());
		sb.append("this, ctxt);");

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
