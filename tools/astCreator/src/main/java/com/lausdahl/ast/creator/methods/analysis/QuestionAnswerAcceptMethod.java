package com.lausdahl.ast.creator.methods.analysis;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.methods.Method;

public class QuestionAnswerAcceptMethod extends Method
{
	public QuestionAnswerAcceptMethod()
	{
		super(null,null);
	}

	public QuestionAnswerAcceptMethod(IClassDefinition c,Environment env)
	{
		super(c,env);
	}

	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;
		StringBuilder sb = new StringBuilder();
		IInterfaceDefinition argDef = env.getTaggedDef(env.TAG_IQuestionAnswer);
		sb.append("\t/**\n");
		sb.append("\t* Calls the {@link "+argDef.getSignatureName()+"#case"
				+ c.getName() + "(" + c.getSignatureName()
				+ ")} of the {@link "+argDef.getSignatureName()+"} {@code caller}.\n");
		sb.append("\t* @param caller the {@link "+argDef.getSignatureName()+"} to which this {@link "
				+ c.getName() + "} node is applied\n");
		sb.append("\t* @param question the question provided to {@code caller}\n");

		sb.append("\t*/");
		this.javaDoc = sb.toString();
		name = "apply";
		annotation = "@Override";
		returnType = "<Q, A> A";
		arguments.add(new Argument(argDef.getName(), "caller"));
		arguments.add(new Argument("Q", "question"));
		body = "\t\treturn caller.case" + c.getName() + "(this, question);";
	}
	
	@Override
	protected void prepareVdm()
	{
		optionalVdmArgument=false;
		IClassDefinition c = classDefinition;
		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");
		sb.append("\t* Calls the {@link IQuestionAnswer<Q, A>#case"
				+ c.getName() + "(" + c.getName()
				+ ")} of the {@link IQuestionAnswer<Q, A>} {@code caller}.\n");
		sb.append("\t* @param caller the {@link IQuestionAnswer<Q, A>} to which this {@link "
				+ c.getName() + "} node is applied\n");
		sb.append("\t* @param question the question provided to {@code caller}\n");

		sb.append("\t*/");
		this.javaDoc = sb.toString();
		name = "apply";
		annotation = "/*@Override*/";
		returnType = "?/*<Q, A> A*/";
		arguments.add(new Argument("IQuestionAnswer/*<Q, A>*/", "caller"));
		arguments.add(new Argument("?/*Q*/", "question"));
		body = "\t\treturn caller.case" + c.getName() + "(this, question);";
	}
}
