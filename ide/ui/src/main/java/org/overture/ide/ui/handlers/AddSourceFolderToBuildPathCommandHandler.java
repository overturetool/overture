package org.overture.ide.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.VdmUIPlugin;

public class AddSourceFolderToBuildPathCommandHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		IContainer c = (IContainer) selection.getFirstElement();
		
		final IProject project = c.getProject();
		
		final IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);
		if(vdmProject!=null)
		{
			vdmProject.getModelBuildPath().add(c);
			try
			{
				vdmProject.getModelBuildPath().save();
				vdmProject.getModel().clean();
				project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());

			} catch (CoreException e)
			{
				VdmUIPlugin.log("Faild to save model path changed", e);
			}
		}
		return null;
	}

}
