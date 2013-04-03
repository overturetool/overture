package org.overture.tools.astcreator.methods.visitors.copy;

import java.util.HashSet;
import java.util.Set;

import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.Method;

public class CopyNode2ExtendedNodeListListHelper extends Method
{
	Environment envDest;

	public CopyNode2ExtendedNodeListListHelper()
	{
		super(null);
	}

	public CopyNode2ExtendedNodeListListHelper(Environment envDest)
	{
		super(null);
		this.envDest = envDest;
	}

	@Override
	protected void prepare(Environment env)
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
	public Set<String> getRequiredImports(Environment env)
	{
		Set<String> imports = new HashSet<String>();
		imports.addAll(super.getRequiredImports(env));
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
