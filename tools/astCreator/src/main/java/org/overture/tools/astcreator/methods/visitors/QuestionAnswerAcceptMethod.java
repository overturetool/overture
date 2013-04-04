package org.overture.tools.astcreator.methods.visitors;

import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.definitions.IInterfaceDefinition;
import org.overture.tools.astcreator.methods.Method;
import org.overture.tools.astcreator.utils.NameUtil;

public class QuestionAnswerAcceptMethod extends Method
{
	private String privilegedBody = null;
	
	
	
	public String getPrivilegedBody() {
		return privilegedBody;
	}

	public void setPrivilegedBody(String privilegedBody) {
		this.privilegedBody = privilegedBody;
	}

	public QuestionAnswerAcceptMethod()
	{
		super(null);
	}

	public QuestionAnswerAcceptMethod(IClassDefinition c)
	{
		super(c);
	}

	@Override
	protected void prepare(Environment env)
	{
		IClassDefinition c = classDefinition;
		StringBuilder sb = new StringBuilder();
		IInterfaceDefinition argDef = env.getTaggedDef(env.TAG_IQuestionAnswer);
		sb.append("\t/**\n");
		sb.append("\t* Calls the {@link "+argDef.getName().getName()+"#case"
				+ AnalysisUtil.getCaseClass(env, c).getName().getName() + "(" + AnalysisUtil.getCaseClass(env, c).getName().getName()
				+ ", Object)} of the {@link "+argDef.getName().getName()+"} {@code caller}.\n");
		sb.append("\t* @param caller the {@link "+argDef.getName().getName()+"} to which this {@link "
				+ AnalysisUtil.getCaseClass(env, c).getName().getName() + "} node is applied\n");
		sb.append("\t* @param question the question provided to {@code caller}\n");

		sb.append("\t*/");
		this.javaDoc = sb.toString();
		name = "apply";
		annotation = "@Override";
		returnType = "<Q, A> A";
		arguments.add(new Argument(NameUtil.getGenericName(argDef), "caller"));
		arguments.add(new Argument("Q", "question"));
		body = (privilegedBody == null ? "\t\treturn caller.case" + AnalysisUtil.getCaseClass(env, c).getName().getName() + "(this, question);" : privilegedBody);
		throwsDefinitions.add(env.analysisException);
	}
	
	@Override
	protected void prepareVdm(Environment env)
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
