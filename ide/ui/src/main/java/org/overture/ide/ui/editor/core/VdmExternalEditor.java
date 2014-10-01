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

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public abstract class VdmExternalEditor extends VdmEditor
{

	public VdmExternalEditor()
	{
		super();
		setDocumentProvider(new VdmExternalDocumentProvider());
	}

	@Override
	public boolean isEditable()
	{
		return false;
	}
	
	@Override
	protected void initializeViewerColors(ISourceViewer viewer)
	{
		super.initializeViewerColors(viewer);
		StyledText styledText= viewer.getTextWidget();
		Color color= new Color(styledText.getDisplay(), new RGB(245, 245, 245));
		styledText.setBackground(color);
	}
	
	//FIXME
//	@Override
//	public SourceReferenceManager getSourceReferenceManager()
//	{
//		if(this.sourceReferenceManager == null)
//		{
//			IVdmElement inputElement = getInputVdmElement();
//			if (inputElement instanceof IVdmSourceUnit) {
//				this.sourceReferenceManager = new ExternalSourceReferenceManager(
//				(IVdmSourceUnit) inputElement);
//			}
//		}
//		return this.sourceReferenceManager;
//	}
}
