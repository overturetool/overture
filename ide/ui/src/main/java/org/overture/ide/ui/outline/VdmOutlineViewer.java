/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.outline;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;

public class VdmOutlineViewer extends TreeViewer
{
	/**
	 * 
	 */

	public VdmOutlineViewer(Composite parent)
	{
		super(parent);
		setAutoExpandLevel(2);
		setUseHashlookup(true);
	}
	
	boolean disableSelectionChangeEvents = false;

	@Override
	protected void fireSelectionChanged(SelectionChangedEvent event)
	{
		if(!disableSelectionChangeEvents)
		{
			super.fireSelectionChanged(event);
		}
	}
	
	public synchronized void setSelection(ISelection selection, boolean reveal) {
		disableSelectionChangeEvents = true;
		super.setSelection(selection, reveal);
		disableSelectionChangeEvents = false;
	}

	public void dispose()
	{

		Tree t = getTree();
		if (t != null && !t.isDisposed())
		{
			if (t.getChildren() != null)
			{
				for (Control c : t.getChildren())
				{
					c.dispose();
				}
			}
			getLabelProvider().dispose();
			t.dispose();
		}

	}
}
