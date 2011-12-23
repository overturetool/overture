package com.lausdahl.ast.creator.methods.visitors.adaptor.analysis;

import com.lausdahl.ast.creator.definitions.ExternalJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.AnalysisUtil;
import com.lausdahl.ast.creator.utils.NameUtil;

public class AnalysisAdaptorCaseMethod extends AnalysisMethodTemplate
{
	private String methodNamePrefix = "case";
	

	public AnalysisAdaptorCaseMethod()
	{
		super(null, null);
	}

	public AnalysisAdaptorCaseMethod(IClassDefinition c, Environment env)
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
		sb.append("\t* Called by the {@link "
				+ AnalysisUtil.getClass(env, c).getName().getName()
				+ "} node from {@link "
				+ AnalysisUtil.getClass(env, c).getName().getName() + "#apply("
				+ env.getTaggedDef(env.TAG_IAnalysis).getName().getName()
				+ ")}.\n");
		sb.append("\t* @param node the calling {@link "
				+ AnalysisUtil.getClass(env, c).getName().getName()
				+ "} node\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		this.name = methodNamePrefix
				+ NameUtil.getClassName(AnalysisUtil.getCaseClass(env, c).getName().getName());
		setupArguments();
		// this.annotation="@override";
		if (c.getSuperDef() != null
				&& !(c instanceof ExternalJavaClassDefinition))
		{
			this.body = "\t\t" + (addReturnToBody ? "return " : "") + "default";

			switch (env.classToType.get(c))
			{
				case Production:
				case SubProduction:
					this.body +=defaultPostFix+ AnalysisUtil.getClass(env, c).getSuperDefs().iterator().next().getName().getName();
					break;
				case Token:
					this.body +=defaultPostFix+ AnalysisUtil.getClass(env, c).getName().getName();
					break;
				case Alternative:
					this.body +=defaultPostFix+ AnalysisUtil.getClass(env, c.getSuperDef()).getName().getName();
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

	public void setMethodNamePrefix(String nm)
	{
		this.methodNamePrefix = nm;
	}
	
	

}
