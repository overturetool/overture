package com.lausdahl.ast.creator.methods.analysis.adopter;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.InterfaceDefinition;

public class AnalysisAdaptorDefaultNodeMethod extends AnalysisMethodTemplate
{
	public AnalysisAdaptorDefaultNodeMethod()
	{
		super(null,null);
	}

	public AnalysisAdaptorDefaultNodeMethod(Environment env)
	{
		super(null,env);
		
	}

	@Override
	protected void prepare()
	{
		classDefinition = env.node;
		IClassDefinition c = classDefinition;
		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");
		sb.append("\t* Called by the {@link " + c.getName()
				+ "} node from {@link " + c.getName() + "#apply("+(c.getInterfaces().isEmpty()?c.getSignatureName():c.getInterfaces().get(0).getSignatureName())+")}.\n");
		sb.append("\t* @param node the calling {@link " + c.getName()
				+ "} node\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		this.name = "default" + InterfaceDefinition.javaClassName(c.getName());
		setupArguments();
		// this.annotation="@override";
		
		this.body = "\t\t"+ (addReturnToBody ? "return null;" : "")
					+"//nothing to do";
		
	}
}
