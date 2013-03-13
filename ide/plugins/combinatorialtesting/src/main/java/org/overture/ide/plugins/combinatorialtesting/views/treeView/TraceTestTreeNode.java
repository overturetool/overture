/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.plugins.combinatorialtesting.views.treeView;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.overture.interpreter.traces.Verdict;
import org.overture.ct.utils.TraceTestResult;

public class TraceTestTreeNode implements IAdaptable, ITreeNode
{
	private TraceTestResult result;
	protected ITreeNode parent;

	private boolean runTimeError = false;

	public TraceTestTreeNode(TraceTestResult status)
	{

		this.result = status;
	}

	public boolean hasRunTimeError()
	{
		return runTimeError;
	}

	public void setRunTimeError()
	{
		this.runTimeError = true;
	}

	public void setStatus(Verdict status)
	{
		this.runTimeError = false;
		this.result.setStatus(status);
	}

	public void setResult(TraceTestResult result)
	{

		this.result = result;
	}

	public Verdict getStatus()
	{
		return this.result.getStatus();
	}

	public ITreeNode getParent()
	{
		return parent;
	}

	public TraceTestResult getResult()
	{
		return result;
	}

	@Override
	public String toString()
	{
		String tmp = result.getNumber().toString();
		while (tmp.length() < 6)
			tmp = "0" + tmp;
		return "Test " + tmp;
	}

	public String getName()
	{
		return toString();
	}

	public Integer getNumber()
	{
		return result.getNumber();
	}

	public void setParent(ITreeNode parent)
	{
		this.parent = parent;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		return null;
	}

	public void addChild(ITreeNode child)
	{
		// TODO Auto-generated method stub

	}

	public List<ITreeNode> getChildren()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChild(String name)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasChildren()
	{
		// TODO Auto-generated method stub
		return false;
	}

	// public boolean equals(Object obj)
	// {
	// if(obj instanceof TraceTestCaseTreeNode)
	// return this.name.equals(((TraceTestCaseTreeNode)obj).getName());
	// else
	// return false;
	// }

}
