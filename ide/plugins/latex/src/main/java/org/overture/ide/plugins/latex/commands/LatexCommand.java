/*
 * #%~
 * org.overture.ide.plugins.latex
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
package org.overture.ide.plugins.latex.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ast.lex.Dialect;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.latex.utility.LatexUtils;
import org.overture.ide.ui.internal.util.ConsoleWriter;
import org.overture.ide.vdmpp.core.IVdmPpCoreConstants;
import org.overture.ide.vdmrt.core.IVdmRtCoreConstants;
import org.overture.ide.vdmsl.core.IVdmSlCoreConstants;

public class LatexCommand extends AbstractHandler
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
				IVdmProject selectedProject = (IVdmProject) project.getAdapter(IVdmProject.class);

				final Shell shell = Display.getCurrent().getActiveShell();
				if (selectedProject != null)
				{
					try
					{
						LatexUtils util = new LatexUtils(shell);
						if (project.hasNature(IVdmPpCoreConstants.NATURE))
						{
							util.makeLatex(selectedProject, Dialect.VDM_PP);
						}
						if (project.hasNature(IVdmSlCoreConstants.NATURE))
						{
							util.makeLatex(selectedProject, Dialect.VDM_SL);
						}
						if (project.hasNature(IVdmRtCoreConstants.NATURE))
						{
							util.makeLatex(selectedProject, Dialect.VDM_RT);
						}
					} catch (Exception ex)
					{
						ConsoleWriter console = new ConsoleWriter("LATEX");
						System.err.println(ex.getMessage() + ex.getStackTrace());
						console.print(ex);
						console.close();
					}
				}
			}

		}

		return null;
	}

}
