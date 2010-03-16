package org.overture.ide.core.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.overture.ide.core.VdmCore;
import org.overturetool.vdmj.lex.LexLocation;

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
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM,
					false,
					IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.MESSAGE).equals(message)
						&& marker.getAttribute(IMarker.SEVERITY)
								.equals(severity)
						&& marker.getAttribute(IMarker.LINE_NUMBER)
								.equals(lineNumber))
					return;

			}
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.SOURCE_ID, "org.overture.ide");
			if (lineNumber == -1)
			{
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e)
		{
			e.printStackTrace();
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
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM,
					false,
					IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.MESSAGE).equals(message)
						&& marker.getAttribute(IMarker.SEVERITY)
								.equals(severity)
						&& marker.getAttribute(IMarker.LINE_NUMBER)
								.equals(lineNumber))
					return;

			}
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.SOURCE_ID, "org.overture.ide");

			SourceLocationConverter converter = new SourceLocationConverter(getContent(file));
			marker.setAttribute(IMarker.CHAR_START,
					converter.getStartPos(lineNumber, columnNumber));
			marker.setAttribute(IMarker.CHAR_END,
					converter.getEndPos(lineNumber, columnNumber));
		} catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Add markers to ifile. This is used to mark a problem by e.g. builder and parser.
	 * 
	 * Important: If a marker already exists at the specified location with the same message
	 * 
	 * @param file
	 *            The IFile which is the source where the marker should be set
	 * @param message
	 *            The message of the marker
	 * @param location
	 *            The lex location where the marker should be set
	 * @param severity
	 *            The severity, e.g: IMarker.SEVERITY_ERROR or IMarker.SEVERITY_ERROR
	 * @param sourceId
	 *            The source if of the plugin calling this function. The PLUGIN id.
	 */
	public static void addMarker(IFile file, String message,
			LexLocation location, int severity, String sourceId)
	{
		try
		{
			if (file == null)
				return;
			SourceLocationConverter converter = new SourceLocationConverter(getContent(file));
			// lineNumber -= 1;
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM,
					true,
					IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if ((marker.getAttribute(IMarker.MESSAGE) != null && marker.getAttribute(IMarker.MESSAGE)
						.equals(message))
						&& (marker.getAttribute(IMarker.SEVERITY) != null && marker.getAttribute(IMarker.SEVERITY)
								.equals(severity))
						&& (marker.getAttribute(IMarker.CHAR_START) != null && marker.getAttribute(IMarker.CHAR_START)
								.equals(converter.getStartPos(location)))
						&& (marker.getAttribute(IMarker.CHAR_END) != null && marker.getAttribute(IMarker.CHAR_END)
								.equals(converter.getEndPos(location))))
					return;

			}
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.SOURCE_ID, sourceId);

			marker.setAttribute(IMarker.CHAR_START,
					converter.getStartPos(location));
			marker.setAttribute(IMarker.CHAR_END, converter.getEndPos(location));
		} catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	public static void deleteMarker(IFile file, String type, String sourceId)
	{
		try
		{
			if (file == null)
				return;

			IMarker[] markers = file.findMarkers(type,
					true,
					IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.SOURCE_ID) != null
						&& marker.getAttribute(IMarker.SOURCE_ID)
								.equals(sourceId))
					marker.delete();
			}
		} catch (CoreException e)
		{
			if (VdmCore.DEBUG)
			{
				e.printStackTrace();
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
			inStream = file.getContents();
			in = new InputStreamReader(inStream, file.getCharset());

			int c = -1;
			while ((c = in.read()) != -1)
				content.add((char) c);

		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
