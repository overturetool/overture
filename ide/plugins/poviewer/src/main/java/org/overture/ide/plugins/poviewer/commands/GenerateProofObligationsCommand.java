package org.overture.ide.plugins.poviewer.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ide.plugins.poviewer.PoGeneratorUtil;

public class GenerateProofObligationsCommand extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;

			Object firstElement = structuredSelection.getFirstElement();
			IWorkbenchPage page =HandlerUtil.getActiveWorkbenchWindow(event)
	        .getActivePage();
			PoGeneratorUtil util = new PoGeneratorUtil(Display.getCurrent().getActiveShell(),page.getActivePart().getSite() );
			if (firstElement instanceof IProject)
			{
				util.generate((IProject) firstElement, null);
			}else if (firstElement instanceof IFile)
			{
				IFile file = (IFile) firstElement;
				util.generate(file.getProject(), file);
			}

		}

		return null;
	}

}
