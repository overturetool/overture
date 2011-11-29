package com.lausdahl.ast.creator.methods.visitors.adaptor.analysis;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.AnalysisUtil;

public class AnalysisAdaptorDefaultMethod extends AnalysisMethodTemplate
{

	public AnalysisAdaptorDefaultMethod()
	{
		super(null, null);
	}

	public AnalysisAdaptorDefaultMethod(IClassDefinition c, Environment env)
	{
		super(c, env);
	}

	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;
		// CommonTreeClassDefinition cd = (CommonTreeClassDefinition) c;
		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");
		sb.append("\t* Called by the {@link " + c.getName().getName()
				+ "} node from {@link " + c.getName().getName()
				+ "#apply(Switch)}.\n");
		sb.append("\t* @param node the calling {@link " + c.getName().getName()
				+ "} node\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		this.name = "default" + AnalysisUtil.getClass(env, c).getName().getName();//NameUtil.getClassName(c.getName().getName());
		setupArguments();
		// this.annotation="@override";
		if (c.getSuperDef() != null)
		{
			this.body = "\t\t" + (addReturnToBody ? "return " : "") + "default";

			// + NameUtil.getClassName(c.getSuperDef().getName().getName()
			// + "("+getAdditionalBodyCallArguments()+");");

			switch (env.classToType.get(c))
			{
				case Production:
				case SubProduction:
					this.body += AnalysisUtil.getClass(env, c).getSuperDefs().iterator().next().getName().getName();
					break;
				default:
					this.body += c.getSuperDef().getName().getName();
					break;
			}
			// + NameUtil.getClassName(c.getSuperDef().getName().getName()

			this.body += "(" + getAdditionalBodyCallArguments() + ");";
		} else
		{
			this.body = "" + (addReturnToBody ? "\t\treturn null;" : "");
		}
	}

}
