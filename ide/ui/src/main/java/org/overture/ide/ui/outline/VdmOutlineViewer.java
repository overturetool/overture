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
package org.overture.ide.ui.outline;

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
	private final VdmContentOutlinePage vdmContentOutlinePage;

	public VdmOutlineViewer(VdmContentOutlinePage vdmContentOutlinePage, Composite parent)
	{
		super(parent);
		this.vdmContentOutlinePage = vdmContentOutlinePage;
		setAutoExpandLevel(2);
		setUseHashlookup(true);

		//setSorter(new OutlineSorter());
	}

	@Override
	protected void fireSelectionChanged(SelectionChangedEvent event)
	{
		if (!this.vdmContentOutlinePage.inExternalSelectionMode)
		{
			super.fireSelectionChanged(event);
		}
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