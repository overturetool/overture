package org.overture.ide.plugins.uml2.commands;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.databinding.Activator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
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
						if (!uml2vdm.initialize(uri))
						{
							return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed importing .uml file. Maybe it doesnt have the EMF UML format");
						}

						progress.worked(40);
						display.asyncExec(new Runnable()
						{
							public void run()
							{
								uml2vdm.convert(new File(iFile.getProject().getLocation().toFile(), "uml_import"));
							}
						});

						progress.worked(50);
						try
						{
							iFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
						} catch (CoreException e)
						{
							// ignore
						}
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
