package com.lausdahl.ast.creator.methods.visitors;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.utils.NameUtil;

public class AnalysisAcceptMethod extends Method
{
	public AnalysisAcceptMethod()
	{
		super(null,null);
	}

	public AnalysisAcceptMethod(IClassDefinition c,Environment env)
	{
		super(c,env);
	}

	@Override
	protected void prepare()
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
		body = "\t\tanalysis.case" + AnalysisUtil.getCaseClass(env, c).getName().getName() + "(this);";
		throwsDefinitions.add(env.analysisException);
	}
	
	@Override
	protected void prepareVdm()
	{
		super.prepareVdm();
		optionalVdmArgument = false;
	}
}
