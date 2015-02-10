/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.editor.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.overture.ide.core.VdmCore;

import org.overture.ide.core.parser.ISourceParser;
import org.overture.ide.core.parser.SourceParserManager;
import org.overture.ide.core.resources.IVdmProject;

public class VdmReconcilingStrategy implements IReconcilingStrategy
{
	private VdmDocument currentDocument;
	ISourceParser parser;
	IVdmProject vdmProject;

	public void reconcile(IRegion partition)
	{
		if (currentDocument.getSourceUnit() == null)
		{
			return;// No reconcile if the source unit had been removed from the project (build path change)
		} else if (parser != null && !vdmProject.getModel().hasWorkingCopies())
		{
			parser.parse(currentDocument.getSourceUnit(), currentDocument.get(),false /*only process with and display errors*/);

			// Setting type checked to false after some alteration
			vdmProject.getModel().setIsTypeChecked(false);
		}
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	{

	}

	public void setDocument(IDocument document)
	{
		if (document instanceof VdmDocument)
		{
			currentDocument = (VdmDocument) document;
			vdmProject = currentDocument.getProject();//.getAdapter(IVdmProject.class);
			if (currentDocument.getSourceUnit() != null && vdmProject != null)
			{

				try
				{
					parser = SourceParserManager.getInstance().getSourceParser(vdmProject);
				} catch (CoreException e)
				{
					if (VdmCore.DEBUG)
						e.printStackTrace();
				}

			}
		}
	}

}
