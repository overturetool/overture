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
