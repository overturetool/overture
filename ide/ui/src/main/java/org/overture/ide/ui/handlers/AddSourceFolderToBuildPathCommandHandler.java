package org.overture.ide.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.VdmUIPlugin;

public class AddSourceFolderToBuildPathCommandHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		final IContainer c = (IContainer) selection.getFirstElement();

		final IProject project = c.getProject();

		final IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);
		if (vdmProject != null)
		{
			vdmProject.getModelBuildPath().add(c);
			try
			{
				vdmProject.getModelBuildPath().save();
				vdmProject.getModel().clean();
				Job job = new Job("Rebuild")
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						try
						{
							project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
						} catch (CoreException e)
						{
							VdmUIPlugin.log("Faild to save model path changed", e);
							return new Status(IStatus.ERROR, IVdmUiConstants.PLUGIN_ID, "Faild to rebuild after model path added", e);
						}
						return Status.OK_STATUS;
					}
				};
				job.schedule();

			} catch (CoreException e)
			{
				VdmUIPlugin.log("Faild to save model path changed", e);
			}
		}
		return null;
	}

}
