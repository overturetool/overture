package com.lausdahl.ast.creator.methods.analysis;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.methods.Method;

public class AnswerAcceptMethod extends Method
{
	public AnswerAcceptMethod()
	{
		super(null,null);
	}

	public AnswerAcceptMethod(IClassDefinition c,Environment env)
	{
		super(c,env);
	}

	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;
		StringBuilder sb = new StringBuilder();
		IInterfaceDefinition argDef = env.getTaggedDef(env.TAG_IAnswer);
		sb.append("\t/**\n");
		sb.append("\t* Calls the {@link "+argDef.getSignatureName()+"#case" + c.getName() + "("
				+ c.getSignatureName() + ")} of the {@link "+argDef.getSignatureName()+"} {@code caller}.\n");
		sb.append("\t* @param caller the {@link "+argDef.getSignatureName()+"} to which this {@link "
				+ c.getName() + "} node is applied\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		name = "apply";
		annotation = "@Override";
		returnType = "<A> A";
		arguments.add(new Argument(argDef.getName(), "caller"));
		body = "\t\treturn caller.case" + c.getName() + "(this);";
	}
	
	@Override
	protected void prepareVdm()
	{
		optionalVdmArgument = false;
		IClassDefinition c = classDefinition;
		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");
		sb.append("\t* Calls the {@link IAnswer<A>#case" + c.getName() + "("
				+ c.getName() + ")} of the {@link IAnswer<A>} {@code caller}.\n");
		sb.append("\t* @param caller the {@link IAnswer<A>} to which this {@link "
				+ c.getName() + "} node is applied\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		name = "apply";
		annotation = "/*@Override*/";
		returnType = "?/*<A> A*/";
		arguments.add(new Argument("IAnswer/*<A>*/", "caller"));
		body = "\t\treturn caller.case" + c.getName() + "(this);";
	}
}
