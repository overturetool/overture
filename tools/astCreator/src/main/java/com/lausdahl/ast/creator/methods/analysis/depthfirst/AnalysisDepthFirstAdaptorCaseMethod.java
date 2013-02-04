package com.lausdahl.ast.creator.methods.analysis.depthfirst;

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
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisMethodTemplate;
import com.lausdahl.ast.creator.utils.NameUtil;

public class AnalysisDepthFirstAdaptorCaseMethod extends AnalysisMethodTemplate  {

	private Field visitedNodesField;

    	public AnalysisDepthFirstAdaptorCaseMethod()
	{
		super(null);
	}

	public AnalysisDepthFirstAdaptorCaseMethod(IClassDefinition c, Field visitedNodesField) {
		super(null);
		this.visitedNodesField = visitedNodesField;
	}

	public AnalysisDepthFirstAdaptorCaseMethod(IClassDefinition c) {
		super(c);
	}

    	public void setVisitedNodesField(Field visitedNodesField)
	{
		this.visitedNodesField = visitedNodesField;
	}

	@Override
	protected void prepare(Environment env) {
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
		String thisNodeMethodName = NameUtil.getClassName(AnalysisUtil
				.getCaseClass(env, c).getName().getName());
		this.name = "case" + thisNodeMethodName;
                    // RWL Manuel merge, commented out in astV2
                    //		this.arguments
                    //		.add(new Argument(
                    //				AnalysisUtil.getCaseClass(env, classDefinition)
                    //						.getName().getName(), "node"));
		this.requiredImports.add("java.util.ArrayList");
		this.requiredImports.add("java.util.List");
		this.requiredImports.add(env.analysisException.getName()
				.getCanonicalName());

		StringBuffer bodySb = new StringBuffer();

		bodySb.append("\t\t_visitedNodes.add(node);\n");

		if(addReturnToBody)
		{
			bodySb.append("\t\tA retVal = createNewReturnValue("+getAdditionalBodyCallArguments()+");\n");
		}

		bodySb.append("\t\t"+wrapForMerge("in" + thisNodeMethodName + "("
				+ getAdditionalBodyCallArguments() )+");\n\n");
		List<Field> allFields = new Vector<Field>();
		allFields.addAll(c.getInheritedFields());
		allFields.addAll(c.getFields());
		for (Field f : allFields) {
			if (f.isTokenField) {
				continue;
			}
			Method getMethod = new GetMethod(c, f);
			getMethod.getJavaSourceCode(env);
			String getMethodName = getMethod.name;
			String getter = "node." + getMethodName + "()";
			requiredImports.addAll(getMethod.getRequiredImports(env));
			if (!f.isList) {
				bodySb.append("\t\tif(" + getter + " != null && !_"
						+ visitedNodesField.name + ".contains(" + getter
						+ ")) \n");
				bodySb.append("\t\t{\n");
				bodySb.append("\t\t\t" +wrapForMerge( getter + ".apply("
						+ getCallArguments() )+ ");\n");
				bodySb.append("\t\t}\n");
			} else if (f.isList && !f.isDoubleList) {
				bodySb.append("\t\t{\n");
				bodySb.append("\t\t\tList<" + f.getInnerTypeForList(env)
						+ "> copy = new ArrayList<"
						+ f.getInnerTypeForList(env) + ">(" + getter + ");\n");
				bodySb.append("\t\t\tfor( " + f.getInnerTypeForList(env)
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
			} else if (f.isDoubleList) {
				bodySb.append("\t\t{\n");
				bodySb.append("\t\t\tList<List<" + f.getInnerTypeForList(env)
						+ ">> copy = new ArrayList<List<"
						+ f.getInnerTypeForList(env) + ">>(" + getter + ");\n");
				bodySb.append("\t\t\tfor( List<" + f.getInnerTypeForList(env)
						+ "> list : copy) {\n");

				bodySb.append("\t\t\t\tfor( " + f.getInnerTypeForList(env)
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
			}
		}

		bodySb.append("\n\t\t"+wrapForMerge("out" + thisNodeMethodName + "("
				+ getAdditionalBodyCallArguments() )+ ");\n");
		if (addReturnToBody)
		{
			bodySb.append("\t\treturn retVal;");
		}
		this.body = bodySb.toString();
	}
	private String wrapForMerge(String call)
	{
		return (addReturnToBody? "mergeReturns(retVal,"
                        : "" )+call + (addReturnToBody?")":"")+"";
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
	public Set<String> getRequiredImports(Environment env) {
		Set<String> imports = super.getRequiredImports(env);
		for (Field f : classDefinition.getFields()) {
			if (f.isTokenField) {
				continue;
			}
			if (f.isList || f.isDoubleList) {
				f.getInnerTypeForList(env);
				imports.add(f.type.getName().getCanonicalName());
			}
		}
		return imports;
	}
}
