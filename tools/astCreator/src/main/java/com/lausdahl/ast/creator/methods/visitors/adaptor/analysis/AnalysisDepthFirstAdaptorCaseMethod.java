package com.lausdahl.ast.creator.methods.visitors.adaptor.analysis;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.GetMethod;
import com.lausdahl.ast.creator.methods.Method;
import com.lausdahl.ast.creator.methods.visitors.AnalysisUtil;
import com.lausdahl.ast.creator.utils.NameUtil;

public class AnalysisDepthFirstAdaptorCaseMethod extends AnalysisMethodTemplate
{
	private Field visitedNodesField;

	public AnalysisDepthFirstAdaptorCaseMethod()
	{
		super(null, null);
	}

	public AnalysisDepthFirstAdaptorCaseMethod(IClassDefinition c,
			Environment source, Field visitedNodesField)
	{
		super(null, null);
		this.visitedNodesField = visitedNodesField;
	}

	public AnalysisDepthFirstAdaptorCaseMethod(IClassDefinition c,
			Environment env)
	{
		super(c, env);
	}

	public void setVisitedNodesField(Field visitedNodesField)
	{
		this.visitedNodesField = visitedNodesField;
	}

	@Override
	protected void prepare()
	{
		throwsDefinitions.add(env.analysisException);
		IClassDefinition c = classDefinition;
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
		String thisNodeMethodName = NameUtil.getClassName(AnalysisUtil.getCaseClass(env, c).getName().getName());
		this.name = "case" + thisNodeMethodName;
		// this.arguments.add(new Argument(AnalysisUtil.getCaseClass(env, classDefinition).getName().getName(),
		// "node"));
		setupArguments();
		this.requiredImports.add("java.util.ArrayList");
		this.requiredImports.add("java.util.List");
		this.requiredImports.add(env.analysisException.getName().getCanonicalName());

		StringBuffer bodySb = new StringBuffer();

		// bodySb.append("\t\tif(_"+visitedNodesField.name+".contains(node))\n");
		// bodySb.append("\t\t{ //already visiting this node from other path\n");
		// bodySb.append("\t\t\treturn;\n");
		// bodySb.append("\t\t}\n");

		// bodySb.append("\t\tif(node instanceof "+env.iNode.getName()+")\n");
		// bodySb.append("\t\t{\n");
		bodySb.append("\t\t_visitedNodes.add(node);\n");
		// bodySb.append("\t\t}\n");

		if(addReturnToBody)
		{
			bodySb.append("\t\tA retVal = createNewReturnValue("+getAdditionalBodyCallArguments()+");\n");
		}
		
		bodySb.append("\t\t"+wrapForMerge("in" + thisNodeMethodName + "("
				+ getAdditionalBodyCallArguments() )+");\n\n");
		List<Field> allFields = new Vector<Field>();
		allFields.addAll(c.getInheritedFields());
		allFields.addAll(c.getFields());
		for (Field f : allFields)
		{
			if (f.isTokenField)
			{
				continue;
			}
			Method getMethod = new GetMethod(c, f, env);
			getMethod.getJavaSourceCode();
			String getMethodName = getMethod.name;
			String getter = "node." + getMethodName + "()";

			if (!f.isList)
			{
				bodySb.append("\t\tif(" + getter + " != null && !_"
						+ visitedNodesField.name + ".contains(" + getter
						+ ")) \n");
				bodySb.append("\t\t{\n");
				bodySb.append("\t\t\t" +wrapForMerge( getter + ".apply("
						+ getCallArguments() )+ ");\n");
				bodySb.append("\t\t}\n");
			} else if (f.isList && !f.isDoubleList)
			{
				bodySb.append("\t\t{\n");
				bodySb.append("\t\t\tList<" + f.getInnerTypeForList()
						+ "> copy = new ArrayList<" + f.getInnerTypeForList()
						+ ">(" + getter + ");\n");
				bodySb.append("\t\t\tfor( " + f.getInnerTypeForList()
						+ " e : copy) \n");
				bodySb.append("\t\t\t{\n");
				bodySb.append("\t\t\t\tif(!_" + visitedNodesField.name
						+ ".contains(e))\n");
				bodySb.append("\t\t\t\t{\n");
				bodySb.append("\t\t\t\t\t"+wrapForMerge("e.apply(" + getCallArguments()
					)	+ ");\n");
				bodySb.append("\t\t\t\t}\n");
				bodySb.append("\t\t\t}\n");

				bodySb.append("\t\t}\n");
			} else if (f.isDoubleList)
			{
				bodySb.append("\t\t{\n");
				bodySb.append("\t\t\tList<List<" + f.getInnerTypeForList()
						+ ">> copy = new ArrayList<List<"
						+ f.getInnerTypeForList() + ">>(" + getter + ");\n");
				bodySb.append("\t\t\tfor( List<" + f.getInnerTypeForList()
						+ "> list : copy) {\n");

				bodySb.append("\t\t\t\tfor( " + f.getInnerTypeForList()
						+ " e : list) \n");
				bodySb.append("\t\t\t{\n");
				bodySb.append("\t\t\t\t\tif(!_" + visitedNodesField.name
						+ ".contains(e))\n");
				bodySb.append("\t\t\t\t\t{\n");
				bodySb.append("\t\t\t\t\t\t"+wrapForMerge("e.apply(" + getCallArguments()
						)+ ");\n");
				bodySb.append("\t\t\t\t\t}\n");
				bodySb.append("\t\t\t\t}\n");

				bodySb.append("\t\t\t}\n");

				bodySb.append("\t\t}\n");
				// List<List<PExp>> copy = new ArrayList<List<PExp>>(node.getFf());
				// for (List<PExp> list : copy)
				// {
				// for( PExp e : list) {
				// e.apply(this);
				// }
				// }
			}
		}

		bodySb.append("\n\t\t"+wrapForMerge("out" + thisNodeMethodName + "("
				+ getAdditionalBodyCallArguments() )+ ");\n");
		// bodySb.append("\t\t_"+visitedNodesField.name+".remove(node);\n");
		if (addReturnToBody)
		{
			bodySb.append("\t\treturn retVal;");
		}
		this.body = bodySb.toString();
		// this.annotation="@override";
		// if (cd.getSuperDef() != null
		// && !(cd instanceof ExternalJavaClassDefinition))
		// {
		// this.body = "\t\t"
		// + (addReturnToBody ? "return " : "")
		// + "default"
		// + InterfaceDefinition.javaClassName(c.getSuperDef().getName()
		// + "(" + getAdditionalBodyCallArguments() + ");");
		// } else
		// {
		// this.body = "" + (addReturnToBody ? "\t\treturn null;" : "");
		// }
	}
	
	private String wrapForMerge(String call)
	{
		return (addReturnToBody?"mergeReturns(retVal,":"")+call + (addReturnToBody?")":"")+"";
	}

	private String getCallArguments()
	{
		String callArgs = "this";
		Iterator<Argument> itr = arguments.iterator();
		if (itr.hasNext())
		{
			itr.next();// Skip first
			if (itr.hasNext())
			{
				callArgs += ", ";
			}
		}
		while (itr.hasNext())
		{
			callArgs += itr.next().name;
			if (itr.hasNext())
			{
				callArgs += ", ";
			}

		}
		return callArgs;
	}

	@Override
	public Set<String> getRequiredImports()
	{
		Set<String> imports = super.getRequiredImports();
		for (Field f : classDefinition.getFields())
		{
			if (f.isTokenField)
			{
				continue;
			}
			if (f.isList || f.isDoubleList)
			{
				f.getInnerTypeForList();
				imports.add(f.type.getName().getCanonicalName());
			}
		}
		return imports;
	}
}
