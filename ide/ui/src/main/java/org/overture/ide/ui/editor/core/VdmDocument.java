package org.overture.ide.ui.editor.core;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;

public class VdmDocument extends Document implements IDocument
{
	private IVdmSourceUnit source;

	public IVdmProject getProject()
	{
		if (source != null)
		{
			return source.getProject();
		}
		return null;
	}

	public IVdmSourceUnit getSourceUnit()
	{
		return this.source;
	}

	public void setSourceUnit(IVdmSourceUnit source)
	{
		this.source = source;
	}

}
