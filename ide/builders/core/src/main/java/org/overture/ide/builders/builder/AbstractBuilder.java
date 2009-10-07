package org.overture.ide.builders.builder;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.overture.ide.ast.dltk.DltkConverter;

public abstract class AbstractBuilder
{
	public abstract IStatus buileModelElements(IProject project,List modelElements);
	
	public abstract String getNatureId();
	
	protected IFile findIFile(File file)
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile ifile = workspace.getRoot().getFileForLocation(location);
		return ifile;
	}

	protected void addMarker(IFile file, String message, int lineNumber,
			int severity, int charStart, int charEnd) throws CoreException
	{
		if (file != null)
		{
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1)
			{
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			StringBuilder content = inputStreamToString(file.getContents());
			if (content != null)
			{
				DltkConverter converter = new DltkConverter(
						content.toString().toCharArray());
				marker.setAttribute(IMarker.CHAR_START, converter.convert(
						lineNumber,
						charStart));
				marker.setAttribute(IMarker.CHAR_END, converter.convert(
						lineNumber,
						charEnd));
			}
		} else
			System.out.println("Cannot set marker in missing file: " + file);
	}

	private StringBuilder inputStreamToString(InputStream is)
	{
		try
		{
			final char[] buffer = new char[0x10000];
			StringBuilder out = new StringBuilder();
			Reader in = new InputStreamReader(is, "UTF-8");
			int read;
			do
			{
				read = in.read(buffer, 0, buffer.length);
				if (read > 0)
				{
					out.append(buffer, 0, read);
				}
			} while (read >= 0);
			return out;
		} catch (Exception e)
		{
			return null;
		}
	}
}
