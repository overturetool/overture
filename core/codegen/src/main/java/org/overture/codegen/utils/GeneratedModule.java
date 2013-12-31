package org.overture.codegen.utils;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.node.INode;

public class GeneratedModule extends Generated
{
	private String name;
	
	public GeneratedModule(String name, String content, Set<INode> unsupportedNodes)
	{
		super(content, unsupportedNodes);
		this.name = name;
	}
	
	public GeneratedModule(String name, String content)
	{
		this(name, content, new HashSet<INode>());
	}
	
	public String getName()
	{
		return name;
	}
}
