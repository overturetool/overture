/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.codegen.ir.NodeInfo;

public class Generated
{
	protected String content;
	protected Set<NodeInfo> unsupportedNodes;
	protected List<Exception> mergeErrors;

	public Generated(String content, Set<NodeInfo> unsupportedNodes,
			List<Exception> mergeErrors)
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
