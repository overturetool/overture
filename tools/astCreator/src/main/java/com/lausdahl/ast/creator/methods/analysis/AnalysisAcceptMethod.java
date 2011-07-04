package com.lausdahl.ast.creator.methods.analysis;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.methods.Method;

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
		sb.append("\t* Calls the {@link "+argDef.getSignatureName()+"#case" + c.getName() + "("
				+ c.getSignatureName()
				+ ")} of the {@link "+argDef.getSignatureName()+"} {@code analysis}.\n");
		sb.append("\t* @param analysis the {@link "+argDef.getSignatureName()+"} to which this {@link "
				+ c.getName() + "} node is applied\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		name = "apply";
		annotation = "@Override";
		arguments.add(new Argument(argDef.getName(), "analysis"));
		body = "\t\tanalysis.case" + c.getName() + "(this);";
	}
	
	@Override
	protected void prepareVdm()
	{
		super.prepareVdm();
		optionalVdmArgument = false;
	}
}
