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

import org.eclipse.core.runtime.IAdaptable;
import org.overture.ct.utils.TraceTestResult;

public class NotYetReadyTreeNode extends TraceTestTreeNode implements
		IAdaptable
{
	private ITreeNode parent;

	public NotYetReadyTreeNode()
	{
		super(new TraceTestResult());

	}

	@Override
	public ITreeNode getParent()
	{
		return parent;
	}


	@Override
	public String toString()
	{
		return "Please wait...";
	}

	@Override
	public String getName()
	{

		return toString();

	}

	@Override
	public void setParent(ITreeNode parent)
	{
		this.parent = parent;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		return null;
	}

	@Override
	public boolean hasChildren()
	{
		return false;
	}

	@Override
	public boolean hasChild(String name)
	{

		return false;
	}

}
