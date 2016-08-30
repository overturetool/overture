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
package org.overture.ide.ui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.overture.ast.node.INode;
import org.overture.ide.core.ElementChangedEvent;
import org.overture.ide.core.ElementChangedEvent.DeltaType;
import org.overture.ide.core.IElementChangedListener;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.IVdmElementDelta;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.resources.IVdmSourceUnit;

/**
 * Please notice that the events send to this class is based on the navigatorContent:
 * <ul>
 * <li>triggerPoints</li>
 * <li>possibleChildren</li>
 * </ul>
 * 
 * @author kela
 */
@SuppressWarnings("restriction")
public class VdmNavigatorCustomContentProvider
		extends
		org.eclipse.ui.internal.navigator.resources.workbench.ResourceExtensionContentProvider
{
	private Viewer viewer;
	private static final Object[] NO_CHILDREN = new Object[0];

	private IElementChangedListener vdmlistner = new IElementChangedListener()
	{

		@Override
		public void elementChanged(ElementChangedEvent event)
		{

			if (event.getType() == DeltaType.POST_BUILD) // happens when the project is build
			{
				if (event.getDelta().getKind() == IVdmElementDelta.CHANGED)
				{
					IVdmElement source = event.getDelta().getElement();
					if (source instanceof IVdmSourceUnit)
					{
						refreshView();
					}
				}
			} else if (event.getType() == DeltaType.POST_RECONCILE) // happens when a source unit is parsed for the
																	// first time
			{
				if (event.getDelta().getKind() == IVdmElementDelta.ADDED)
				{
					IVdmElement source = event.getDelta().getElement();
					if (source instanceof IVdmSourceUnit)
					{
						refreshView();
					}
				}
			}
		}

		private void refreshView()
		{
			if (viewer != null && viewer.getControl() != null
					&& viewer.getControl().getDisplay() != null)
				viewer.getControl().getDisplay().asyncExec(new Runnable()
				{
					/*
					 * (non-Javadoc)
					 * @see java.lang.Runnable#run()
					 */
					public void run()
					{
						if (!viewer.getControl().isDisposed())
						{
							viewer.refresh();
						}
					}
				});
		}
	};

	public VdmNavigatorCustomContentProvider()
	{
		VdmCore.addElementChangedListener(vdmlistner);
	}

	@Override
	public void dispose()
	{
		super.dispose();
		VdmCore.removeElementChangedListener(vdmlistner);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.model.WorkbenchContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 * java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		super.inputChanged(viewer, oldInput, newInput);
		this.viewer = viewer;
	}

	@Override
	protected IWorkbenchAdapter getAdapter(Object element)
	{
		if (element instanceof IFile)
		{
			IVdmSourceUnit source = (IVdmSourceUnit) Adapters.adapt(((IFile) element), IVdmSourceUnit.class);
			IWorkbenchAdapter adapter = getAdapter(source);
			return adapter;
		}
		return super.getAdapter(element);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object element)
	{
		if (element instanceof IResource || element instanceof INode)
		{
			IWorkbenchAdapter adapter = getAdapter(element);
			if (adapter != null)
			{
				return adapter.getChildren(element);
			}
			return NO_CHILDREN;
		}
		return NO_CHILDREN;
	}
}
