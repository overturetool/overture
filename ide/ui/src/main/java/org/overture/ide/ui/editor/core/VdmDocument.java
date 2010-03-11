package org.overture.ide.ui.editor.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

public class VdmDocument extends Document implements IDocument
{
	private IFile file;

	public IProject getProject()
	{
		if (file != null)
			return file.getProject();

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
