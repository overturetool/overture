package com.lausdahl.ast.creator.methods.analysis.depthfirst;

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

public class DepthFirstCaseMethod extends Method
{
	public DepthFirstCaseMethod()
	{
		super(null, null);
	}

	public DepthFirstCaseMethod(IClassDefinition c, Environment env)
	{
		super(c, env);
	}

	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;
		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");
		sb.append("\t* Called by the {@link "
				+ AnalysisUtil.getClass(env, c).getName().getName()
				+ "} node from {@link "
				+ AnalysisUtil.getClass(env, c).getName().getName()
				+ "#apply("+env.getTaggedDef(env.TAG_IAnalysis).getName().getName()+")}.\n");
		sb.append("\t* @param node the calling {@link " + AnalysisUtil.getClass(env, c).getName().getName()
				+ "} node\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		String thisNodeMethodName =NameUtil.getClassName(AnalysisUtil.getCaseClass(env, c).getName().getName()); 
		this.name = "case" + thisNodeMethodName;
		this.arguments.add(new Argument(AnalysisUtil.getCaseClass(env, classDefinition).getName().getName(), "node"));
		this.requiredImports.add("java.util.ArrayList");
		this.requiredImports.add("java.util.List");

		StringBuffer bodySb = new StringBuffer();
		
		bodySb.append("\t\tif(_queue.contains(node))\n");
		bodySb.append("\t\t{ //already visiting this node from other path\n");
		bodySb.append("\t\t\treturn;\n");
		bodySb.append("\t\t}\n");
		bodySb.append("\t\t_queue.add(node);\n");
		
		bodySb.append("\t\tin"+thisNodeMethodName+"(node);\n\n");
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
				bodySb.append("\t\tif(" + getter + " != null) {\n");
				bodySb.append("\t\t\t" + getter + ".apply(this);\n");
				bodySb.append("\t\t}\n");
			} else if(f.isList && !f.isDoubleList)
			{
				bodySb.append("\t\t{\n");
				bodySb.append("\t\t\tList<" + f.getInnerTypeForList()
						+ "> copy = new ArrayList<" + f.getInnerTypeForList()
						+ ">(" + getter + ");\n");
				bodySb.append("\t\t\tfor( " + f.getInnerTypeForList()
						+ " e : copy) {\n");
				bodySb.append("\t\t\t\te.apply(this);\n");
				bodySb.append("\t\t\t}\n");

				bodySb.append("\t\t}\n");
			}else if(f.isDoubleList)
			{
				bodySb.append("\t\t{\n");
				bodySb.append("\t\t\tList<List<" + f.getInnerTypeForList()
						+ ">> copy = new ArrayList<List<" + f.getInnerTypeForList()
						+ ">>(" + getter + ");\n");
				bodySb.append("\t\t\tfor( List<" + f.getInnerTypeForList()
						+ "> list : copy) {\n");

				bodySb.append("\t\t\t\tfor( " + f.getInnerTypeForList()
						+ " e : list) {\n");
				bodySb.append("\t\t\t\t\te.apply(this);\n");
				bodySb.append("\t\t\t\t}\n");
				
				bodySb.append("\t\t\t}\n");

				bodySb.append("\t\t}\n");
//				List<List<PExp>> copy = new ArrayList<List<PExp>>(node.getFf());
//				for (List<PExp> list : copy)
//				{
//					for( PExp e : list) {
//						e.apply(this);
//					}
//				}
			}
		}
		
		bodySb.append("\n\t\tout"+thisNodeMethodName+"(node);\n");
		bodySb.append("\t\t_queue.remove(node);\n");

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
			if(f.isList || f.isDoubleList)
			{
				f.getInnerTypeForList();
				imports.add(f.type.getName().getCanonicalName());
			}
		}
		return imports;
	}
}
