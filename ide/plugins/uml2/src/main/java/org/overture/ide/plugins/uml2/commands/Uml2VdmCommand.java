package org.overture.ide.plugins.uml2.commands;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ide.plugins.uml2.uml2vdm.Uml2Vdm;

//import org.overture.typechecker.ClassTypeChecker;

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
				IFile iFile = (IFile) firstElement;
				java.net.URI absolutePath = iFile.getLocationURI();
				URI uri = URI.createFileURI(absolutePath.getPath());
				Uml2Vdm uml2vdm = new Uml2Vdm(uri);
				uml2vdm.convert(new File(iFile.getProject().getLocation().toFile(),"uml_import"));
				try
				{
					iFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				} catch (CoreException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return null;
	}

}
