package com.lausdahl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import asteditor.Activator;

public class FileUtility
{
	public static void addMarker(IFile file, String message, int lineNumber,
			int severity)
	{
		try
		{
			if (file == null)
				return;
			lineNumber -= 1;
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.MESSAGE).equals(message)
						&& marker.getAttribute(IMarker.SEVERITY).equals(severity)
						&& marker.getAttribute(IMarker.LINE_NUMBER).equals(lineNumber))
					return;

			}
	 		IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.SOURCE_ID, IAstEditorConstants.PLUGIN_ID);
			marker.setAttribute(IMarker.LOCATION, "line: " + lineNumber);

			if (lineNumber == -1)
			{
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e)
		{
			Activator.log("FileUtility addMarker", e);
		}
	}

	public static void addMarker(IFile file, String message, int lineNumber,
			int columnNumber, int severity)
	{
		try
		{
			if (file == null)
				return;
			lineNumber -= 1;
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.MESSAGE) != null
						&& marker.getAttribute(IMarker.MESSAGE).equals(message)
						&& marker.getAttribute(IMarker.SEVERITY) != null
						&& marker.getAttribute(IMarker.SEVERITY).equals(severity)
						&& marker.getAttribute(IMarker.LINE_NUMBER) != null
						&& marker.getAttribute(IMarker.LINE_NUMBER).equals(lineNumber))
					return;

			}
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.SOURCE_ID, IAstEditorConstants.PLUGIN_ID);
			marker.setAttribute(IMarker.LOCATION, "line: " + lineNumber);

			SourceLocationConverter converter = new SourceLocationConverter(getContent(file));
			marker.setAttribute(IMarker.CHAR_START, converter.getStartPos(lineNumber, columnNumber));
			marker.setAttribute(IMarker.CHAR_END, converter.getEndPos(lineNumber, columnNumber));
		} catch (CoreException e)
		{
			Activator.log("FileUtility addMarker", e);
		}
	}

	public static void addMarker(IFile file, String message, int lineNumber,
			int columnNumber, int severity, String content)
	{
		try
		{
			if (file == null)
				return;
			// lineNumber -= 1;
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.MESSAGE) != null
						&& marker.getAttribute(IMarker.MESSAGE).equals(message)
						&& marker.getAttribute(IMarker.SEVERITY) != null
						&& marker.getAttribute(IMarker.SEVERITY).equals(severity)
						&& marker.getAttribute(IMarker.LINE_NUMBER) != null
						&& marker.getAttribute(IMarker.LINE_NUMBER).equals(lineNumber))
					return;

			}
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.SOURCE_ID, IAstEditorConstants.PLUGIN_ID);
			marker.setAttribute(IMarker.LOCATION, "line: " + lineNumber);

			SourceLocationConverter converter = new SourceLocationConverter(content.toCharArray());
			if (lineNumber == 0 && columnNumber == -1)
			{
				marker.setAttribute(IMarker.LINE_NUMBER, converter.getLineCount());
				marker.setAttribute(IMarker.LOCATION, "line: " + converter.getLineCount());
			} else
			{
				marker.setAttribute(IMarker.CHAR_START, converter.getStartPos(lineNumber, columnNumber));
				marker.setAttribute(IMarker.CHAR_END, converter.getEndPos(lineNumber, columnNumber));
			}
		} catch (CoreException e)
		{
			Activator.log("FileUtility addMarker", e);
		}
	}

	public static void deleteMarker(IFile file, String type, String sourceId)
	{
		try
		{
			if (file == null)
				return;

			IMarker[] markers = file.findMarkers(type, true, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.SOURCE_ID) != null
						&& marker.getAttribute(IMarker.SOURCE_ID).equals(sourceId))
					marker.delete();
			}
		} catch (CoreException e)
		{
			if (Activator.DEBUG)
			{
				Activator.log("FileUtility deleteMarker", e);
			}
		}
	}

	// public static void gotoLocation(IFile file, LexLocation location,
	// String message) {
	// try {
	//
	// IWorkbench wb = PlatformUI.getWorkbench();
	// IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
	//
	// IEditorPart editor = IDE.openEditor(win.getActivePage(), file, true);
	//
	// IMarker marker = file.createMarker(IMarker.MARKER);
	// marker.setAttribute(IMarker.MESSAGE, message);
	// marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
	//
	// SourceLocationConverter converter = new SourceLocationConverter(getContent(file));
	// marker.setAttribute(IMarker.CHAR_START,converter.getStartPos( location));
	// marker.setAttribute(IMarker.CHAR_END,converter.getEndPos(location));
	// // marker.setAttribute(IMarker.LINE_NUMBER, location.startLine);
	// // System.out.println("Marker- file: " + file.getName() + " ("
	// // + location.startLine + "," + location.startPos + "-"
	// // + location.endPos + ")");
	//
	// IDE.gotoMarker(editor, marker);
	//
	// marker.delete();
	//
	// } catch (CoreException e) {
	//
	// e.printStackTrace();
	// }
	// }

	public static List<Character> getContent(IFile file)
	{

		InputStream inStream;
		InputStreamReader in = null;
		List<Character> content = new Vector<Character>();
		try
		{
			if (!file.isSynchronized(IResource.DEPTH_ONE))
			{
				file.refreshLocal(IResource.DEPTH_ONE, null);
			}
			inStream = file.getContents();
			in = new InputStreamReader(inStream, file.getCharset());

			int c = -1;
			while ((c = in.read()) != -1)
				content.add((char) c);

		} catch (Exception e)
		{
			Activator.log("FileUtility getContent", e);
		} finally
		{
			if (in != null)
				try
				{
					in.close();
				} catch (IOException e)
				{
				}
		}

		return content;

	}

	public static char[] getCharContent(List<Character> content)
	{
		char[] source = new char[content.size()];
		for (int i = 0; i < content.size(); i++)
		{
			source[i] = content.get(i);
		}
		return source;
	}
}
