package org.overture.codegen.utils;

import java.util.List;
import java.util.Set;

import org.overture.codegen.ooast.NodeInfo;

public class GeneratedModule extends Generated
{
	private String name;
	
	public GeneratedModule(String name, String content, Set<NodeInfo> unsupportedNodes, List<Exception> mergeErrors)
	{
		super(content, unsupportedNodes, mergeErrors);
		this.name = name;
	}

	public GeneratedModule(String name, String content)
	{
		super(content);
		this.name = name;
	}
	
	public GeneratedModule(String name, Set<NodeInfo> unsupportedNodes)
	{
		super(unsupportedNodes);
		this.name = name;
	}

	public GeneratedModule(String name, List<Exception> mergeErrors)
	{
		super(mergeErrors);
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
}
