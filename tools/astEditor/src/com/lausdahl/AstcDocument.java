package com.lausdahl;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

public class AstcDocument  extends Document implements IDocument
{

	private IFile file;

	public void setFile(IFile file)
	{
		this.file = file;
		
	}
	
	public IFile getFile()
	{
		return this.file;
	}

}
