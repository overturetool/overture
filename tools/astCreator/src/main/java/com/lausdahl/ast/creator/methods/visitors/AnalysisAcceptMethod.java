package com.lausdahl.ast.creator.methods.visitors;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.utils.NameUtil;

public class AnalysisAcceptMethod extends Method
{
	private String privilegedBody;
	
	
	public String getPrivilegedBody() {
		return privilegedBody;
	}

	public void setPrivilegedBody(String privilegedBody) {
		this.privilegedBody = privilegedBody;
	}

	public AnalysisAcceptMethod()
	{
		super(null);
	}

	public AnalysisAcceptMethod(IClassDefinition c,Environment env)
	{
		super(c);
	}

	@Override
	protected void prepare(Environment env)
	{
		IClassDefinition c = classDefinition;
		IInterfaceDefinition argDef = env.getTaggedDef(env.TAG_IAnalysis);
		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");
		sb.append("\t* Calls the {@link "+argDef.getName().getName()+"#case" + AnalysisUtil.getCaseClass(env, c).getName().getName() + "("
				+ c.getName().getName()
				+ ")} of the {@link "+argDef.getName().getName()+"} {@code analysis}.\n");
		sb.append("\t* @param analysis the {@link "+argDef.getName().getName()+"} to which this {@link "
				+ c.getName().getName() + "} node is applied\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		name = "apply";
		annotation = "@Override";
		arguments.add(new Argument(NameUtil.getGenericName(argDef), "analysis"));
		body = privilegedBody == null ? "\t\tanalysis.case" + AnalysisUtil.getCaseClass(env, c).getName().getName() + "(this);" : privilegedBody;
		throwsDefinitions.add(env.analysisException);
	}
	
	
	@Override
	protected void prepareVdm(Environment env)
	{
		super.prepareVdm(env);
		optionalVdmArgument = false;
	}
}
