package com.lausdahl.ast.creator.methods.analysis.adopter;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.InterfaceDefinition;

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
		CommonTreeClassDefinition cd = (CommonTreeClassDefinition) c;
		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");
		sb.append("\t* Called by the {@link " + c.getName()
				+ "} node from {@link " + c.getName() + "#apply(Switch)}.\n");
		sb.append("\t* @param node the calling {@link " + c.getName()
				+ "} node\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		this.name = "default" + InterfaceDefinition.javaClassName(c.getName());
		setupArguments();
		// this.annotation="@override";
		if (cd.getSuperDef() != null)
		{
			this.body = "\t\t"+(addReturnToBody?"return ":"")+"default"
					+ InterfaceDefinition.javaClassName(c.getSuperDef().getName()
							+ "("+getAdditionalBodyCallArguments()+");");
		} else
		{
			this.body = ""+(addReturnToBody?"\t\treturn null;":"");
		}
	}
	
	
}
