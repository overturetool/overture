package org.overture.ide.builders.builder;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
	
	/***
	 * This method syncs the project resources. 
	 * It is called before an instance of the AbstractBuilder is created
	 * @param project The project which should be synced
	 */
	public static void syncProjectResources(IProject project)
	{
		if (!project.isSynchronized(IResource.DEPTH_INFINITE))
			try
			{
				project.refreshLocal(
						IResource.DEPTH_INFINITE,
						null);
			} catch (CoreException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
	/***
	 * This method removed all problem markers and its sub-types from the project.
	 * It is called before an instance of the AbstractBuilder is created
	 * @param project The project which should be build.
	 */
	public static void clearProblemMarkers(IProject project)
	{
		try {
			project.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
