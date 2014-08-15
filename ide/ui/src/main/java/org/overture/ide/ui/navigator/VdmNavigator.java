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
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.overture.ast.node.INode;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.core.VdmEditor;

public class VdmNavigator extends CommonNavigator
{

	public VdmNavigator()
	{
		super();
		super.setLinkingEnabled(true);
	}

	@Override
	protected void handleDoubleClick(DoubleClickEvent anEvent)
	{
		super.handleDoubleClick(anEvent);
		IAction openHandler = getViewSite().getActionBars().getGlobalActionHandler(ICommonActionConstants.OPEN);

		if (openHandler == null)
		{
			IStructuredSelection selection = (IStructuredSelection) anEvent.getSelection();
			Object element = selection.getFirstElement();

			if (element instanceof INode)
			{
				IFile file = (IFile) Platform.getAdapterManager().getAdapter(element, IFile.class);
				if (file != null)
				{
					try
					{
						IEditorPart editor = IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, true);

						if (editor instanceof VdmEditor)
						{
							((VdmEditor) editor).selectAndReveal((INode) element);
						}
					} catch (PartInitException e)
					{
						VdmUIPlugin.log("Failed to sync inode in navigator doubleclick with editor", e);
					}
				}
			}
		}
	}

}
