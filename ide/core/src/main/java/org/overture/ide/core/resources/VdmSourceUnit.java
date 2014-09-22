/*
 * #%~
 * org.overture.ide.core
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
package org.overture.ide.core.resources;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.overture.ast.node.INode;
import org.overture.ide.core.ElementChangedEvent;
import org.overture.ide.core.IVdmElementDelta;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.VdmElementDelta;

public class VdmSourceUnit implements IVdmSourceUnit
{
	protected IVdmProject project;
	protected IFile file;
	protected int type;
	protected boolean parseErrors = false;

	protected List<INode> parseList = new Vector<INode>();

	public VdmSourceUnit(IVdmProject project, IFile file)
	{
		this.project = project;
		this.file = file;
		this.type = IVdmSourceUnit.VDM_DEFAULT;

	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public IFile getFile()
	{
		return file;
	}

	public File getSystemFile()
	{
		return project.getFile(file);
	}

	public synchronized void reconcile(List<INode> parseResult,
			boolean parseErrors)
	{
		boolean added = this.parseList.isEmpty();
		this.parseList.clear();
		this.parseErrors = parseErrors;
		if (!parseErrors)
		{
			this.parseList.addAll(parseResult);
		}

		if (added)
		{
			fireAddedEvent();
		}
		fireChangedEvent();
	}

	protected void fireChangedEvent()
	{
		VdmCore.getDeltaProcessor().fire(this, new ElementChangedEvent(new VdmElementDelta(this, IVdmElementDelta.CHANGED), ElementChangedEvent.DeltaType.POST_RECONCILE));
	}

	protected void fireAddedEvent()
	{
		VdmCore.getDeltaProcessor().fire(this, new ElementChangedEvent(new VdmElementDelta(this, IVdmElementDelta.ADDED), ElementChangedEvent.DeltaType.POST_RECONCILE));
	}

	public synchronized List<INode> getParseList()
	{
		return this.parseList;
	}

	public boolean exists()
	{
		return this.file.exists();
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public int getElementType()
	{
		return getType();
	}

	@Override
	public String toString()
	{
		return file.toString();
	}

	public synchronized void clean()
	{
		this.parseList.clear();

	}

	public IVdmProject getProject()
	{
		return project;
	}

	public boolean hasParseTree()
	{
		return parseList.size() > 0;
	}

	public boolean hasParseErrors()
	{
		return this.parseErrors;
	}

	public VdmSourceUnitWorkingCopy getWorkingCopy()
	{
		return new VdmSourceUnitWorkingCopy(this);
	}

}
