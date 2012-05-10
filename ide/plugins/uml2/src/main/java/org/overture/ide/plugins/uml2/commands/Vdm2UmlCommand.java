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
import org.overture.typecheck.ClassTypeChecker;
import org.overturetool.util.definitions.ClassList;

public class Vdm2UmlCommand extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if(selection instanceof IStructuredSelection)
		{
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			
			Object firstElement = structuredSelection.getFirstElement();
			if(firstElement instanceof IProject)
			{
				IProject project = ((IProject) firstElement);
				IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);
				
				if(vdmProject == null)
				{
					return null;
				}
				
				IVdmModel model = vdmProject.getModel();
				if(model.isParseCorrect())
				{
					ClassList classes = null;
					try {
						classes = model.getClassList();
					} catch (NotAllowedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(!model.isTypeChecked())
					{
						
						ClassTypeChecker tc = new ClassTypeChecker(classes);
						tc.typeCheck();
					}

					if (ClassTypeChecker.getErrorCount() == 0) {
						Vdm2Uml vdm2uml = new Vdm2Uml();
						vdm2uml.init(classes);

						IFile iFile = project.getFile(project.getName());
						java.net.URI absolutePath = iFile.getLocationURI();
						URI uri = URI.createFileURI(absolutePath.getPath());
						try {
							vdm2uml.save(uri);
						} catch (IOException e) {
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
