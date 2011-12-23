package com.lausdahl.ast.creator.methods.visitors.adaptor.analysis;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.utils.NameUtil;

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
		setClassDefinition(env.iNode);
		IClassDefinition c = classDefinition;
		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");
		sb.append("\t* Called by the {@link " + c.getName().getName()
				+ "} node from {@link " + c.getName().getName() + "#apply("+env.getTaggedDef(env.TAG_IAnalysis).getName().getName()+")}.\n");
		sb.append("\t* @param node the calling {@link " + c.getName().getName()
				+ "} node\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		this.name = "default"+defaultPostFix + NameUtil.getClassName(c.getName().getName());
		setupArguments();
		// this.annotation="@override";
		
		this.body = "\t\t"+ (addReturnToBody ? "return null;" : "")
					+"//nothing to do";
		
	}
}
