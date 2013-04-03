package org.overture.tools.astcreator.methods.visitors.adaptor.analysis;

import java.util.Set;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.visitors.AnalysisUtil;

public class AnalysisAdaptorDefaultMethod extends AnalysisMethodTemplate
{
	public AnalysisAdaptorDefaultMethod()
	{
		super(null);
	}

	public AnalysisAdaptorDefaultMethod(IClassDefinition c)
	{
		super(c);
	}
	
	@Override
	public Set<String> getRequiredImports(Environment env)
	{
		Set<String> temp = super.getRequiredImports(env);
		temp.add(AnalysisUtil.getClass(env, classDefinition).getName().getCanonicalName());
		temp.add(env.getTaggedDef(env.TAG_IAnalysis).getName().getCanonicalName());
		return temp;
	}
	
	@Override
	public Set<String> getRequiredImportsSignature(Environment env)
	{
		Set<String> temp =super.getRequiredImportsSignature(env);
		temp.add(AnalysisUtil.getClass(env, classDefinition).getName().getCanonicalName());
		temp.add(env.getTaggedDef(env.TAG_IAnalysis).getName().getCanonicalName());
		return temp;
	}

	@Override
	protected void prepare(Environment env)
	{
		super.prepare(env);
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
		setupArguments(env);
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
