package org.overture.codegen.utils;

import java.util.HashSet;
import java.util.Set;

import org.overture.codegen.ooast.NodeInfo;

public class GeneratedModule extends Generated
{
	private String name;
	
	public GeneratedModule(String name, String content, Set<NodeInfo> unsupportedNodes)
	{
		super(content, unsupportedNodes);
		this.name = name;
	}
	
	public GeneratedModule(String name, String content)
	{
		this(name, content, new HashSet<NodeInfo>());
	}
	
	public String getName()
	{
		return name;
	}
}
