/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.ui.editor.core;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.overture.ide.core.ExternalSourceReferenceManager;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.SourceReferenceManager;
import org.overture.ide.core.resources.IVdmSourceUnit;

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
	
	@Override
	public SourceReferenceManager getSourceReferenceManager()
	{
		if(this.sourceReferenceManager == null)
		{
			IVdmElement inputElement = getInputVdmElement();
			if (inputElement instanceof IVdmSourceUnit) {
				this.sourceReferenceManager = new ExternalSourceReferenceManager(
				(IVdmSourceUnit) inputElement);
			}
		}
		return this.sourceReferenceManager;
	}
}
