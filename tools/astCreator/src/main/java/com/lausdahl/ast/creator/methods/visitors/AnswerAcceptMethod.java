package com.lausdahl.ast.creator.methods.visitors;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.utils.NameUtil;

public class AnswerAcceptMethod extends Method
{
	public AnswerAcceptMethod()
	{
		super(null);
	}

	public AnswerAcceptMethod(IClassDefinition c)
	{
		super(c);
	}

	@Override
	protected void prepare(Environment env)
	{
		IClassDefinition c = classDefinition;
		StringBuilder sb = new StringBuilder();
		IInterfaceDefinition argDef = env.getTaggedDef(env.TAG_IAnswer);
		sb.append("\t/**\n");
		sb.append("\t* Calls the {@link "+argDef.getName().getName()+"#case" + AnalysisUtil.getCaseClass(env, c).getName().getName() + "("
				+ AnalysisUtil.getCaseClass(env, c).getName().getName() + ")} of the {@link "+argDef.getName().getName()+"} {@code caller}.\n");
		sb.append("\t* @param caller the {@link "+argDef.getName().getName()+"} to which this {@link "
				+ AnalysisUtil.getCaseClass(env, c).getName().getName() + "} node is applied\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		name = "apply";
		annotation = "@Override";
		returnType = "<A> A";
		arguments.add(new Argument(NameUtil.getGenericName(argDef), "caller"));
		body = "\t\treturn caller.case" + AnalysisUtil.getCaseClass(env, c).getName().getName() + "(this);";
		throwsDefinitions.add(env.analysisException);
	}
	
	@Override
	protected void prepareVdm(Environment env)
	{
		optionalVdmArgument = false;
		IClassDefinition c = classDefinition;
		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");
		sb.append("\t* Calls the {@link IAnswer<A>#case" + c.getName().getName() + "("
				+ c.getName().getName() + ")} of the {@link IAnswer<A>} {@code caller}.\n");
		sb.append("\t* @param caller the {@link IAnswer<A>} to which this {@link "
				+ c.getName() + "} node is applied\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		name = "apply";
		annotation = "/*@Override*/";
		returnType = "?/*<A> A*/";
		arguments.add(new Argument("IAnswer/*<A>*/", "caller"));
		body = "\t\treturn caller.case" + c.getName().getName() + "(this);";
	}
}
