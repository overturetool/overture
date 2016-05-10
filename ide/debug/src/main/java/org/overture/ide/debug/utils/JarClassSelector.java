/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.utils;

import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class JarClassSelector
{

	public static String selectClass(Shell shell, IContainer container)
			throws CoreException
	{
		// TODO Auto-generated method stub
		// final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new
		// DecorationgVdmLabelProvider(new VdmUILabelProvider()), new MergedModuleVdmOutlineTreeContentProvider());
		// ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(),
		// new BaseWorkbenchContentProvider());

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new LabelProvider());

		dialog.setTitle("Select Class");
		dialog.setMessage("Select a remote control class");

		// dialog.addFilter(new ExecutableFilter());
		// dialog.setComparator(new
		// ResourceComparator(ResourceComparator.NAME));
		// dialog.setAllowMultiple(false);

		final List<String> jars = new Vector<String>();

		if (container.exists())
		{
			container.accept(new IResourceVisitor()
			{

				@Override
				public boolean visit(IResource resource) throws CoreException
				{
					if (resource.getType() == IResource.FILE
							&& resource.getFileExtension() != null
							&& resource.getFileExtension().equals("jar"))
					{
						jars.add(resource.getLocation().toFile().getAbsolutePath());
					} else if (resource.getType() == IResource.FOLDER)
					{
						return true;
					}
					return false;
				}
			});
		}

		List<String> classes = new ArrayList<String>();
		for (String path : jars)
		{
			classes.addAll(PackageUtils.getClasseNamesInPackage(path, null));
		}

		// dialog.setInput(classes);
		dialog.setElements(classes.toArray());
		if (dialog.open() == IDialogConstants.OK_ID)
		{
			if (dialog.getFirstResult() instanceof String)
			{
				return dialog.getFirstResult().toString();
			}

		}
		return null;
	}

}
