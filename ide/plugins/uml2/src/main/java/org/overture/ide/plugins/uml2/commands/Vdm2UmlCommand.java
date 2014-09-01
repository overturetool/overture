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

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ast.lex.Dialect;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.uml2.Activator;
import org.overture.ide.plugins.uml2.IUml2Constants;
import org.overture.ide.plugins.uml2.vdm2uml.Vdm2Uml;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;

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
				IProject project = (IProject) firstElement;
				IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);

				if (vdmProject == null)
				{
					return null;
				}

				final IVdmModel model = vdmProject.getModel();
				if (model.isParseCorrect())
				{

					if (model == null || !model.isTypeCorrect())
					{
						VdmTypeCheckerUi.typeCheck(HandlerUtil.getActiveShell(event), vdmProject);
					}

					if (model.isTypeCorrect()
							&& (vdmProject.getDialect() == Dialect.VDM_PP || vdmProject.getDialect() == Dialect.VDM_RT))
					{
						IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
						boolean preferAssociations = preferences.getBoolean(IUml2Constants.PREFER_ASSOCIATIONS_PREFERENCE);
						boolean deployArtifactsOutsideNodes = preferences.getBoolean(IUml2Constants.DISABLE_NESTED_ARTIFACTS_PREFERENCE);
						Vdm2Uml vdm2uml = new Vdm2Uml(preferAssociations, deployArtifactsOutsideNodes);
						try
						{
							vdm2uml.convert(project.getName(), model.getClassList());
						} catch (NotAllowedException e1)
						{
							Activator.log("Faild converting VDM to UML", e1);
						}

						// IFile iFile =
						// vdmProject.getModelBuildPath().getOutput().getLocationURI()..getFile(project.getName());
						java.net.URI absolutePath = vdmProject.getModelBuildPath().getOutput().getLocationURI();// iFile.getLocationURI();
						URI uri = URI.createFileURI(absolutePath.getPath()
								+ "/" + project.getName());
						try
						{
							vdm2uml.save(uri);
							project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (CoreException e)
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
