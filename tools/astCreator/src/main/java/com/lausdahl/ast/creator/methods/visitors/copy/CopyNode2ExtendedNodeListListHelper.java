package com.lausdahl.ast.creator.methods.visitors.copy;

import java.util.HashSet;
import java.util.Set;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.Method;

public class CopyNode2ExtendedNodeListListHelper extends Method
{
	Environment env;
	Environment envDest;

	public CopyNode2ExtendedNodeListListHelper()
	{
		super(null, null);
	}

	public CopyNode2ExtendedNodeListListHelper(Environment env, Environment envDest)
	{
		super(null, env);
		this.env = env;
		this.envDest = envDest;
	}

	@Override
	protected void prepare()
	{
		this.name = "copyListList";
		this.arguments.add(new Argument(Environment.listDef.getName().getCanonicalName()
				+ "<? extends "
				+ Environment.listDef.getName().getCanonicalName()
				+"<? extends "
				+ env.iNode.getName().getCanonicalName()+ ">>", "list"));
//		this.annotation = "@SuppressWarnings({ \"rawtypes\"/*, \"unchecked\"*/ })";
		this.returnType = envDest.nodeListList.getName().getCanonicalName();
		StringBuilder bodySb = new StringBuilder();

		bodySb.append("\t\t" + this.returnType + " newList = new "
				+ this.returnType + "<" + envDest.iNode.getName().getCanonicalName() + ">(null);\n");
		bodySb.append("\t\t" + "for( "+Environment.listDef.getName().getCanonicalName()+"<? extends " + env.iNode.getName().getCanonicalName()+ "> innerList : list)\n");
		bodySb.append("\t\t" + "{\n");
		bodySb.append("\t\t" + "\tnewList.add(copyList(innerList));\n");
		bodySb.append("\t\t" + "}\n");
		bodySb.append("\t\t" + "return newList;");
		this.body = bodySb.toString();

	}

	@Override
	public Set<String> getRequiredImports()
	{
		Set<String> imports = new HashSet<String>();
		imports.addAll(super.getRequiredImports());
		imports.add(Environment.linkedListDef.getName().getCanonicalName());
		return imports;
	}

	// @SuppressWarnings({ "rawtypes", "unchecked" })
	// public static NodeListInterpreter copyList(NodeList<? extends Node> list)
	// {
	// NodeListInterpreter newList = new NodeListInterpreter<NodeInterpreter>(null);
	// for (Node n : list)
	// {
	// newList.add(n);
	// }
	// return newList;
	// }
}
