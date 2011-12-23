package com.lausdahl.ast.creator.methods.visitors.adaptor.analysis;

import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.utils.NameUtil;

public class AnalysisAdaptorDefaultTokenMethod extends AnalysisMethodTemplate
{
	public AnalysisAdaptorDefaultTokenMethod()
	{
		super(null, null);
	}

	public AnalysisAdaptorDefaultTokenMethod(Environment env)
	{
		super(null, env);
			}

	@Override
	protected void prepare()
	{
		intf = env.iToken;
		IInterfaceDefinition c = intf;
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

		this.body = "\t\t" + (addReturnToBody ? "return null;" : "")
				+ "//nothing to do";

	}
}
