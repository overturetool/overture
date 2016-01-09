package org.overture.ide.plugins.isatrans.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.isatrans.IsaTransControl;

public class IsaTransBothCommand extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;

			Object firstElement = structuredSelection.getFirstElement();
			IVdmProject proj = null;
			if (firstElement instanceof IProject)
			{
				proj = (IVdmProject) ((IProject) firstElement).getAdapter(IVdmProject.class);
			} else if (firstElement instanceof IFile)
			{
				IFile file = (IFile) firstElement;
				proj = (IVdmProject) ((IProject) file.getProject()).getAdapter(IVdmProject.class);
			} else if (firstElement instanceof IFolder)
			{
				IFolder file = (IFolder) firstElement;
				proj = (IVdmProject) ((IProject) file.getProject()).getAdapter(IVdmProject.class);
			}

			IsaTransControl util = new IsaTransControl(HandlerUtil.getActiveWorkbenchWindow(event), proj, Display.getCurrent().getActiveShell());
			util.generateTheoryFilesModelPos();
		}

		return null;
	}
}
