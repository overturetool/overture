package com.lausdahl.ast.creator.methods.analysis;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.InterfaceDefinition;
import com.lausdahl.ast.creator.methods.Method;

public class AnswerCaseMethod extends Method
{
	public AnswerCaseMethod()
	{
		super(null,null);
	}

	public AnswerCaseMethod(IClassDefinition c,Environment env)
	{
		super(c,env);
	}

	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;
		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");
		sb.append("\t* Called by the {@link " + c.getSignatureName()
				+ "} node from {@link " + c.getSignatureName() + "#apply(Switch)}.\n");
		sb.append("\t* @param node the calling {@link " + c.getSignatureName()
				+ "} node\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		this.name = "case" + InterfaceDefinition.javaClassName(c.getName());
		this.arguments.add(new Argument(c.getName(), "node"));
		// this.annotation="@override";
		this.body = "\t\treturn null;";
		this.returnType = "A";
	}
}
