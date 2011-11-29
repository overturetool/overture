package com.lausdahl.ast.creator.methods.visitors.copy;

import java.util.HashSet;
import java.util.Set;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.Method;

public class CopyNode2ExtendedNodeListHelper extends Method
{
	Environment env;
	Environment envDest;

	public CopyNode2ExtendedNodeListHelper()
	{
		super(null, null);
	}

	public CopyNode2ExtendedNodeListHelper(Environment env, Environment envDest)
	{
		super(null, env);
		this.env = env;
		this.envDest = envDest;
	}

	@Override
	protected void prepare()
	{
		this.name = "copyList";
		this.arguments.add(new Argument(Environment.listDef.getName().getCanonicalName()
				+ "<? extends "
				+ env.iNode.getName().getCanonicalName() + ">", "list"));
//		this.annotation = "@SuppressWarnings({ \"rawtypes\"/*, \"unchecked\" */})";
		this.returnType = envDest.nodeList.getName().getCanonicalName();
		StringBuilder bodySb = new StringBuilder();

		bodySb.append("\t\t" + this.returnType + " newList = new "
				+ this.returnType + "<" + envDest.iNode.getName().getPackageName() + "."
				+ envDest.iNode.getName().getName() + ">(null);\n");
		bodySb.append("\t\t" + "for( " + env.iNode.getName().getPackageName() + "."
				+ env.iNode.getName().getName() + " n : list)\n");
		bodySb.append("\t\t" + "{\n");
		bodySb.append("\t\t" + "\tnewList.add(checkCache(n,n.apply(this)));\n");
		bodySb.append("\t\t" + "}\n");
		bodySb.append("\t\t" + "return newList;");
		this.body = bodySb.toString();

	}

	@Override
	public Set<String> getRequiredImports()
	{
		Set<String> imports = new HashSet<String>();
		imports.addAll(super.getRequiredImports());
		imports.add(Environment.listDef.getName().getCanonicalName());
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
