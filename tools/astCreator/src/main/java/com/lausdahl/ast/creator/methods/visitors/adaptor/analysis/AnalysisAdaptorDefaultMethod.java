package com.lausdahl.ast.creator.methods.visitors.adaptor.analysis;

import java.util.Set;

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
	public Set<String> getRequiredImports()
	{
		Set<String> temp = super.getRequiredImports();
		temp.add(AnalysisUtil.getClass(env, classDefinition).getName().getCanonicalName());
		temp.add(env.getTaggedDef(env.TAG_IAnalysis).getName().getCanonicalName());
		return temp;
	}
	
	@Override
	public Set<String> getRequiredImportsSignature()
	{
		Set<String> temp =super.getRequiredImportsSignature();
		temp.add(AnalysisUtil.getClass(env, classDefinition).getName().getCanonicalName());
		temp.add(env.getTaggedDef(env.TAG_IAnalysis).getName().getCanonicalName());
		return temp;
	}

	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;
		// CommonTreeClassDefinition cd = (CommonTreeClassDefinition) c;
		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");
		sb.append("\t* Called by the {@link " + AnalysisUtil.getClass(env, c).getName().getName()
				+ "} node from {@link " +AnalysisUtil.getClass(env, c).getName().getName()
				+ "#apply("+env.getTaggedDef(env.TAG_IAnalysis).getName().getName()+")}.\n");
		sb.append("\t* @param node the calling {@link " +AnalysisUtil.getClass(env, c).getName().getName()
				+ "} node\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		this.name = "default"+defaultPostFix + AnalysisUtil.getClass(env, c).getName().getName();//NameUtil.getClassName(c.getName().getName());
		setupArguments();
		// this.annotation="@override";
		if (c.getSuperDef() != null)
		{
			this.body = "\t\t" + (addReturnToBody ? "return " : "") + "default"+defaultPostFix;

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
