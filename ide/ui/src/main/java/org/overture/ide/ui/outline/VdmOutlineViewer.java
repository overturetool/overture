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

import org.eclipse.jface.util.IOpenEventListener;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
	protected void hookControl(Control control) {
		//super.hookControl(control);
		OpenStrategy handler = new OpenStrategy(control);
		handler.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				// On Windows, selection events may happen during a refresh.
				// Ignore these events if we are currently in preservingSelection().
				// See bug 184441.
				// if (!inChange) {
				//	handleSelect(e);
				// }
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				handleDoubleSelect(e);
			}
		});
		handler.addPostSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handlePostSelect(e);
			}
		});
		handler.addOpenListener(new IOpenEventListener() {
			public void handleOpen(SelectionEvent e) {
				VdmOutlineViewer.this.handleOpen(e);
			}
		});
	}
	
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