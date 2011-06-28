package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.IClassDefinition;

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
		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");
		sb.append("\t* Calls the {@link IAnalysis#case" + c.getName() + "("
				+ c.getName()
				+ ")} of the {@link IAnalysis} {@code analysis}.\n");
		sb.append("\t* @param analysis the {@link IAnalysis} to which this {@link "
				+ c.getName() + "} node is applied\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		name = "apply";
		annotation = "@Override";
		arguments.add(new Argument("IAnalysis", "analysis"));
		body = "\t\tanalysis.case" + c.getName() + "(this);";
	}
	
	@Override
	protected void prepareVdm()
	{
		super.prepareVdm();
		optionalVdmArgument = false;
	}
}
