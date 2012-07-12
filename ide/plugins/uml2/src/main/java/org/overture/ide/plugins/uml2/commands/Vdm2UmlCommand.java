package org.overture.ide.plugins.uml2.commands;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.uml2.vdm2uml.Vdm2Uml;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;

//import org.overture.typechecker.ClassTypeChecker;

public class Vdm2UmlCommand extends AbstractHandler
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
				IProject project = ((IProject) firstElement);
				IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);

				if (vdmProject == null)
				{
					return null;
				}
				
				final IVdmModel model = vdmProject.getModel();
				if (model.isParseCorrect())
				{
					

					if (!model.isParseCorrect())
					{
						return null;
						//return new Status(Status.ERROR, IPoviewerConstants.PLUGIN_ID, "Project contains parse errors");
					}

					if (model == null || !model.isTypeCorrect())
					{
						VdmTypeCheckerUi.typeCheck(HandlerUtil.getActiveShell(event), vdmProject);
					}

					if (model.isTypeCorrect())
					{
						Vdm2Uml vdm2uml = new Vdm2Uml();
						try
						{
							vdm2uml.init(model.getClassList());
						} catch (NotAllowedException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						IFile iFile = project.getFile(project.getName());
						java.net.URI absolutePath = iFile.getLocationURI();
						URI uri = URI.createFileURI(absolutePath.getPath());
						try
						{
							vdm2uml.save(uri);
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}

			}

		}

		return null;
	}

}
