package org.overture.ide.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.CoreException;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.utility.FileUtility;

public class ExternalSourceReferenceManager extends SourceReferenceManager
{
	public ExternalSourceReferenceManager(IVdmSourceUnit sourceUnit)
	{
		super(sourceUnit);
	}

	
	@Override
	protected InputStream getContent(IVdmSourceUnit sourceUnit2)
			throws CoreException
	{
		String charset = sourceUnit2.getFile().getCharset();
		try
		{
			return new ByteArrayInputStream(FileUtility.getContentExternalText(sourceUnit2.getFile()).getBytes(charset));
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
