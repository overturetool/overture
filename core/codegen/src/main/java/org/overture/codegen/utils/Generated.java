package org.overture.codegen.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.codegen.ooast.NodeInfo;

public class Generated
{
	protected String content;
	protected Set<NodeInfo> unsupportedNodes;
	protected List<Exception> mergeErrors;
		
	public Generated(String content, Set<NodeInfo> unsupportedNodes, List<Exception> mergeErrors)
	{
		this.content = content;
		this.unsupportedNodes = unsupportedNodes;
		this.mergeErrors = mergeErrors;
	}
	
	public Generated(String content)
	{
		this(content, new HashSet<NodeInfo>(), new LinkedList<Exception>());
	}
	
	public Generated(Set<NodeInfo> unsupportedNodes)
	{
		this(null, unsupportedNodes, new LinkedList<Exception>());
	}
	
	public Generated(List<Exception> mergeErrrors)
	{
		this(null, new HashSet<NodeInfo>(), mergeErrrors);
	}

	public String getContent()
	{
		return content;
	}
	
	public Set<NodeInfo> getUnsupportedNodes()
	{
		return unsupportedNodes;
	}
	
	public List<Exception> getMergeErrors()
	{
		return mergeErrors;
	}
	
	public boolean canBeGenerated()
	{
		return unsupportedNodes.isEmpty();
	}
	
	public boolean hasMergeErrors()
	{
		return !mergeErrors.isEmpty();
	}
}
