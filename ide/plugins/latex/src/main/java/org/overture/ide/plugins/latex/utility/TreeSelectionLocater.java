/*
 * #%~
 * org.overture.ide.plugins.latex
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
package org.overture.ide.plugins.latex.utility;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.overture.ide.core.resources.IVdmProject;

@SuppressWarnings("restriction")
public class TreeSelectionLocater
{
	public static IVdmProject getSelectedProject(IAction action)
	{
		IVdmProject project = null;
		if (action instanceof ObjectPluginAction)
		{
			ObjectPluginAction objectPluginAction = (ObjectPluginAction) action;
			if (objectPluginAction.getSelection() instanceof ITreeSelection)
			{
				ITreeSelection selection = (ITreeSelection) objectPluginAction.getSelection();
				if (selection.getPaths().length > 0)
				{
					IProject p = (IProject) selection.getPaths()[0].getFirstSegment();
					project = (IVdmProject) p.getAdapter(IVdmProject.class);

				}
			} else if (objectPluginAction.getSelection() instanceof IStructuredSelection)
			{
				IStructuredSelection selection = (IStructuredSelection) objectPluginAction.getSelection();
				if (selection.getFirstElement() instanceof IProject)
				{
					IProject p = (IProject) selection.getFirstElement();
					project = (IVdmProject) p.getAdapter(IVdmProject.class);
				}
			}
		}
		return project;
	}
}
