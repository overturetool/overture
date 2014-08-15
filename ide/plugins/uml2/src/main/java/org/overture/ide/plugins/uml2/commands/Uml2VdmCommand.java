/*
 * #%~
 * UML2 Translator
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
package org.overture.ide.plugins.uml2.commands;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.uml2.Activator;
import org.overture.ide.plugins.uml2.uml2vdm.Uml2Vdm;

public class Uml2VdmCommand extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;

			Object firstElement = structuredSelection.getFirstElement();
			if (firstElement instanceof IFile)
			{
				final IFile iFile = (IFile) firstElement;
				java.net.URI absolutePath = iFile.getLocationURI();
				final URI uri = URI.createFileURI(absolutePath.getPath());
				final Uml2Vdm uml2vdm = new Uml2Vdm();
				final Display display = Display.getCurrent();
				Job convert = new Job("Importing UML")
				{

					@Override
					protected IStatus run(IProgressMonitor progress)
					{
						progress.beginTask("Importing", 100);
						progress.worked(5);

						IVdmProject p = (IVdmProject) iFile.getProject().getAdapter(IVdmProject.class);
						String extension = null;
						if (p != null)
						{
							for (IContentType ct : p.getContentTypeIds())
							{
								if (!ct.getId().contains(".external."))
								{
									extension = ct.getFileSpecs(IContentType.FILE_EXTENSION_SPEC)[0];
									break;
								}
							}

						}

						if (!uml2vdm.initialize(uri, extension))
						{
							return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed importing .uml file. Maybe it doesnt have the EMF UML format");
						}

						progress.worked(40);
						display.asyncExec(new Runnable()
						{
							public void run()
							{
								uml2vdm.convert(new File(iFile.getProject().getLocation().toFile(), "uml_import"));

								try
								{
									iFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
								} catch (CoreException e)
								{
									// ignore
								}
							}
						});

						progress.worked(50);

						progress.worked(5);
						progress.done();

						return Status.OK_STATUS;
					}
				};
				convert.schedule();
			}

		}

		return null;
	}

}
