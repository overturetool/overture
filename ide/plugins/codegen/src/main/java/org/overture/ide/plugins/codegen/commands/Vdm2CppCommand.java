package org.overture.ide.plugins.codegen.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ide.plugins.codegen.vdm2cpp.Vdm2Cpp;

public class Vdm2CppCommand extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object firstElement = structuredSelection.getFirstElement();

			if (firstElement instanceof IProject)
			{

				final Vdm2Cpp vdm2cpp = new Vdm2Cpp();
				final Display display = Display.getCurrent();
				Job convert = new Job("Generating C++ code")
				{

					@Override
					protected IStatus run(IProgressMonitor progress)
					{
						progress.beginTask("Generating C++ code", 100);
						progress.worked(5);
						
						progress.worked(40);
						display.asyncExec(new Runnable()
						{
							public void run()
							{
								vdm2cpp.generateCode();
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
