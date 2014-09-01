/*
 * #%~
 * Combinatorial Testing
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
package org.overture.ide.plugins.combinatorialtesting.views.treeView;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

public class TreeParent implements IAdaptable, ITreeNode
{
	private String name;
	private List<ITreeNode> children;

	public TreeParent(String name)
	{
		this.name = name;
		children = new ArrayList<ITreeNode>();
	}

	@Override
	public String toString()
	{
		return getName();
	}

	public String getName()
	{
		return name;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		return null;
	}

	public void addChild(ITreeNode child)
	{
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(ITreeNode child)
	{
		children.remove(child);
		child.setParent(null);
	}

	public List<ITreeNode> getChildren()
	{
		return children;
	}

	public boolean hasChildren()
	{
		return children.size() > 0;
	}

	public ITreeNode getParent()
	{
		return null;
	}

	public boolean hasChild(String name)
	{
		return false;
	}

	public void setParent(ITreeNode parent)
	{

	}
}
