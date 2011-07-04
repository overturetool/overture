package com.lausdahl.ast.creator.methods.analysis;

import com.lausdahl.ast.creator.Environment;
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
		this.arguments.add(new Argument(env.nodeList.getName()+"<? extends "+env.node.getName()+">", "list"));
		 this.annotation="@SuppressWarnings({ \"rawtypes\", \"unchecked\" })";
		this.returnType = envDest.nodeList.getSignatureName();
		StringBuilder bodySb = new StringBuilder();
		
		bodySb.append("\t\t"+this.returnType+" newList = new " +this.returnType+"<"+envDest.node.getName()+">(null);\n");
		bodySb.append("\t\t"+"for( "+env.node.getName()+" n : list)\n");
		bodySb.append("\t\t"+"{\n");
		bodySb.append("\t\t"+"\tnewList.add(n.apply(this));\n");
		bodySb.append("\t\t"+"}\n");
		bodySb.append("\t\t"+"return newList;");
		this.body = bodySb.toString();

	}
	
	
	
	
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public static NodeListInterpreter copyList(NodeList<? extends Node> list)
//	{
//		NodeListInterpreter newList = new NodeListInterpreter<NodeInterpreter>(null);
//		for (Node n : list)
//		{
//			newList.add(n);
//		}
//		return newList;
//	}
}
