package org.overture.ide.ui.editor.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.VdmProject;


public class VdmDocument extends Document implements IDocument
{
	private IFile file;

	public IVdmProject getProject()
	{
		if (file != null && VdmProject.isVdmProject(file.getProject()))
		{
			return VdmProject.createProject(file.getProject());
		}
		return null;
	}

	public IFile getFile()
	{
		return this.file;
	}

	public void setFile(IFile file)
	{
		this.file = file;
	}

}
